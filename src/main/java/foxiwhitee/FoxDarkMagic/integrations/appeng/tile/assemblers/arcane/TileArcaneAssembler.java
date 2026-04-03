package foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.arcane;

import appeng.api.implementations.items.IMemoryCard;
import appeng.api.implementations.items.MemoryCardMessages;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.me.GridAccessException;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import cpw.mods.fml.common.network.ByteBufUtils;
import foxiwhitee.FoxDarkMagic.helpers.FoxEssentiaHelper;
import foxiwhitee.FoxDarkMagic.integrations.appeng.api.IAspectPatternHelper;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.TileAssembler;
import foxiwhitee.FoxLib.tile.inventory.FoxInternalInventory;
import foxiwhitee.FoxLib.tile.inventory.InvOperation;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumicenergistics.api.grid.IDigiVisSource;
import thaumicenergistics.common.integration.tc.DigiVisSourceData;
import thaumicenergistics.common.integration.tc.VisCraftingHelper;

import java.util.Hashtable;
import java.util.List;

import static thaumicenergistics.common.tiles.TileArcaneAssembler.PRIMALS;

public abstract class TileArcaneAssembler extends TileAssembler {
    private final FoxInternalInventory upgrade = new FoxInternalInventory(this, 1, 1);
    private final FoxInternalInventory wand = new FoxInternalInventory(this, 1, 1);
    private final FoxInternalInventory armor = new FoxInternalInventory(this, 4, 1);
    private final FoxInternalInventory patterns;
    private final Hashtable<Aspect, Float> visDiscount = new Hashtable<>();
    private int visTickCounter = 0, delayTickCounter = 0;
    private boolean delayedUpdate = false;
    private final DigiVisSourceData visSourceInfo = new DigiVisSourceData();
    private final AspectList storedVis = new AspectList();
    private boolean infinityVis, flagRecalcVis;

    public TileArcaneAssembler(long maxCount, int patternsInventory) {
        super(maxCount);
        this.patterns = new FoxInternalInventory(this, patternsInventory, 1);
        for (Aspect primal : PRIMALS) {
            this.visDiscount.put(primal, getBaseDiscount() / 100f);
        }
        this.processor.setAfterCraftingFunction(this::afterCrafting);
    }

    @Override
    public FoxInternalInventory getPatterns() {
        return patterns;
    }

    public FoxInternalInventory getUpgradeInventory() {
        return upgrade;
    }

    public FoxInternalInventory getWandInventory() {
        return wand;
    }

    public FoxInternalInventory getArmorInventory() {
        return armor;
    }

    @TileEvent(TileEventType.TICK)
    @SuppressWarnings("unused")
    public void tick() {
        if (worldObj.isRemote) {
            return;
        }

        if (this.flagRecalcVis) {
            this.calculateVisDiscounts();
        }

        if (this.visTickCounter++ == 5) {
            this.visTickCounter = 0;
            if (this.visSourceInfo.hasSourceData()) {
                this.replenishVis();
            } else {
                this.replenishVisFromWand();
            }
        }

        if (this.delayedUpdate) {
            if (this.delayTickCounter++ >= 5) {
                this.markForUpdate();

                this.delayedUpdate = false;
                this.delayTickCounter = 0;
            }
        }
    }

    @Override
    public void onReady() {
        super.onReady();
        this.calculateVisDiscounts();
    }

    protected void afterCrafting(ICraftingPatternDetails activePattern) {
        if (!infinityVis && activePattern instanceof IAspectPatternHelper helper) {
            AspectList needVis = helper.getAspects();
            for (Aspect aspect : needVis.getAspects()) {
                storedVis.reduce(aspect, getRequiredAmountForAspect(aspect, needVis.getAmount(aspect)));
            }
            markForDelayedUpdate();
        }
    }

    @Override
    protected long getMaxCount() {
        if (!infinityVis && this.processor.getActivePattern() instanceof IAspectPatternHelper helper) {
            AspectList needVis = helper.getAspects();
            long can = 0;
            for (Aspect aspect : needVis.getAspects()) {
                can = Math.min(can, storedVis.getAmount(aspect) / getRequiredAmountForAspect(aspect, needVis.getAmount(aspect)));
            }
            return Math.min(can - 1, super.getMaxCount());
        }
        return super.getMaxCount();
    }

    @Override
    protected boolean isValidCraft(ICraftingPatternDetails pattern) {
        return pattern instanceof IAspectPatternHelper;
    }

    public void onMemoryCardActivate(final EntityPlayer player, final IMemoryCard memoryCard, final ItemStack playerHolding) {
        String settingsName = memoryCard.getSettingsName(playerHolding);

        if (settingsName.equals(DigiVisSourceData.SOURCE_UNLOC_NAME)) {
            NBTTagCompound data = memoryCard.getData(playerHolding);

            this.visSourceInfo.readFromNBT(data);

            if (this.visSourceInfo.hasSourceData()) {
                memoryCard.notifyUser(player, MemoryCardMessages.SETTINGS_LOADED);
            }

            this.markDirty();
        } else if (settingsName.equals("gui.appliedenergistics2.Blank")) {
            this.visSourceInfo.clearData();
            memoryCard.notifyUser(player, MemoryCardMessages.SETTINGS_CLEARED);
            this.markDirty();
        }
    }

    private void replenishVis() {
        IDigiVisSource visSource = null;

        if (!this.getProxy().isReady()) {
            return;
        }

        try {
            visSource = this.visSourceInfo.tryGetSource(this.getProxy().getGrid());
        } catch (GridAccessException ignored) {}

        if (visSource == null) {
            replenishVisFromWand();
            return;
        }

        for (Aspect primal : PRIMALS) {
            int numNeeded = getMaxStoredVis() - this.storedVis.getAmount(primal);

            if (numNeeded > 0) {
                int amountDrained = visSource.consumeVis(primal, numNeeded);

                if (amountDrained > 0) {
                    this.storedVis.add(primal, Math.min(amountDrained * 10, numNeeded));

                    this.markForDelayedUpdate();
                }
            }
        }
    }

    private void replenishVisFromWand() {
        ItemStack wand = this.wand.getStackInSlot(0);
        if (wand != null && wand.getItem() instanceof ItemWandCasting wandCasting) {
            for (Aspect aspect : PRIMALS) {
                int needAspect = (this.getMaxStoredVis() - this.storedVis.getAmount(aspect));

                if (needAspect > 0) {
                    if (wandCasting.getRod(wand).getTag().equals("infinity")) {
                        this.storedVis.add(aspect, needAspect);
                        markForDelayedUpdate();
                    } else {
                        int inWand = wandCasting.getVis(wand, aspect);
                        int drained = Math.min(inWand, needAspect);

                        if (drained > 0) {
                            wandCasting.storeVis(wand, aspect, inWand - drained);
                            this.storedVis.add(aspect, drained);
                            markForDelayedUpdate();
                        }
                    }
                }
            }
        }
    }

    private void calculateVisDiscounts() {
        this.flagRecalcVis = false;
        float discount;

        for (Aspect primal : PRIMALS) {
            discount = getBaseDiscount() / 100f;
            discount -= VisCraftingHelper.INSTANCE.calculateArmorDiscount(this.armor, 0, 4, primal);

            this.visDiscount.put(primal, discount);
        }
        markForDelayedUpdate();
    }

    private int getRequiredAmountForAspect(Aspect aspect, int basicAmount) {
        return (int) Math.ceil(this.visDiscount.get(aspect) * (basicAmount * 100));
    }

    @Override
    public void onChangeInventory(IInventory iInventory, int i, InvOperation invOperation, ItemStack itemStack, ItemStack itemStack1) {
        super.onChangeInventory(iInventory, i, invOperation, itemStack, itemStack1);
        if (iInventory == upgrade) {
            infinityVis = upgrade.getStackInSlot(0) != null;
        } else if (iInventory == armor) {
            this.flagRecalcVis = true;
        }
    }

    @Override
    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeToNbt_(NBTTagCompound data) {
        super.writeToNbt_(data);
        this.upgrade.writeToNBT(data, "upgrade");
        this.wand.writeToNBT(data, "wand");
        this.armor.writeToNBT(data, "armor");
        this.visSourceInfo.writeToNBT(data, "visSourceInfo");
        this.storedVis.writeToNBT(data, "storedVis");
        data.setBoolean("infinityVis", infinityVis);
    }

    @Override
    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNbt_(NBTTagCompound data) {
        super.readFromNbt_(data);
        this.upgrade.readFromNBT(data, "upgrade");
        this.wand.readFromNBT(data, "wand");
        this.armor.readFromNBT(data, "armor");
        if (data.hasKey("visSourceInfo")) {
            this.visSourceInfo.readFromNBT(data, "visSourceInfo");
        }
        this.storedVis.readFromNBT(data, "storedVis");
        this.infinityVis = data.getBoolean("infinityVis");
    }

    @TileEvent(TileEventType.NETWORK_WRITE)
    @SuppressWarnings("unused")
    public void writeToStream(ByteBuf data) {
        FoxEssentiaHelper.writeToStream(data, storedVis);
        writeDiscount(data, visDiscount);
    }

    @TileEvent(TileEventType.NETWORK_READ)
    @SuppressWarnings("unused")
    public boolean readFromStream(ByteBuf data) {
        FoxEssentiaHelper.readFromStream(data, storedVis);
        return readAndSyncDiscount(data, visDiscount);
    }

    private void writeDiscount(ByteBuf data, Hashtable<Aspect, Float> visDiscount) {
        data.writeInt(visDiscount.size());

        visDiscount.forEach((aspect, discount) -> {
            ByteBufUtils.writeUTF8String(data, aspect.getTag());

            data.writeFloat(discount);
        });
    }

    public boolean readAndSyncDiscount(ByteBuf data, Hashtable<Aspect, Float> currentMap) {
        Hashtable<Aspect, Float> newMap = new Hashtable<>();

        int size = data.readInt();
        for (int i = 0; i < size; i++) {
            String aspectTag = ByteBufUtils.readUTF8String(data);
            Aspect aspect = Aspect.getAspect(aspectTag);
            float discount = data.readFloat();

            if (aspect != null) {
                newMap.put(aspect, discount);
            }
        }

        boolean hasChanged = !currentMap.equals(newMap);

        if (hasChanged) {
            currentMap.clear();
            currentMap.putAll(newMap);
        }

        return hasChanged;
    }

    private void markForDelayedUpdate() {
        this.delayedUpdate = true;
    }

    public AspectList getStoredVis() {
        return storedVis;
    }

    @Override
    public int rows() {
        return getPatterns().getSizeInventory() / 9;
    }

    @Override
    public void getDrops(World w, int x, int y, int z, List<ItemStack> drops) {
        super.getDrops(w, x, y, z, drops);
        if (upgrade.getStackInSlot(0) != null) {
            drops.add(upgrade.getStackInSlot(0));
        }
        if (wand.getStackInSlot(0) != null) {
            drops.add(wand.getStackInSlot(0));
        }
        for (int i = 0; i < armor.getSizeInventory(); i++) {
            if (armor.getStackInSlot(i) != null) {
                drops.add(armor.getStackInSlot(i));
            }
        }
    }

    public void writeVisLevelsToNBT(NBTTagCompound data) {
        if (this.storedVis.size() > 0) {
            this.storedVis.writeToNBT(data, "storedVis");
        }
    }

    public void readVisLevelsFromNBT(NBTTagCompound data) {
        if (data.hasKey("storedVis")) {
            this.storedVis.readFromNBT(data, "storedVis");
        }
    }

    public float getVisDiscountForAspect(final Aspect aspect) {
        return this.visDiscount.get(aspect);
    }

    public abstract int getMaxStoredVis();

    public abstract int patternsYStartPos();

    public abstract String getGuiName();

    public abstract int getBaseDiscount();
}
