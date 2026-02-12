package foxiwhitee.FoxDarkMagic.mixins;

import foxiwhitee.FoxDarkMagic.api.IInfusionMatrixAccessor;
import foxiwhitee.FoxDarkMagic.tile.TileStabilizer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.tiles.TileInfusionMatrix;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = TileInfusionMatrix.class, priority = 1500, remap = false)
public abstract class TileInfusionMatrixMixin extends TileEntity implements IInfusionMatrixAccessor {

    @Shadow public int instability;
    @Shadow private int countDelay;

    @Shadow
    private AspectList recipeEssentia;
    @Unique
    private final List<ChunkCoordinates> cachedStabilizers = new ArrayList<>();

    @Unique
    private boolean fullyStabilized = false;

    @Inject(method = "craftingStart", at = @At("HEAD"))
    private void scanOnCraftStart(CallbackInfo ci) {
        cachedStabilizers.clear();
        int dist = 6;
        int found = 0;
        for (int x = xCoord - dist; x <= xCoord + dist; x++) {
            for (int y = yCoord - dist; y <= yCoord + dist; y++) {
                for (int z = zCoord - dist; z <= zCoord + dist; z++) {
                    if (worldObj.getTileEntity(x, y, z) instanceof TileStabilizer) {
                        cachedStabilizers.add(new ChunkCoordinates(x, y, z));
                        found++;
                    }
                }
            }
        }
        this.fullyStabilized = (found >= 4);
    }

    @Inject(method = "craftCycle", at = @At("RETURN"))
    private void applyStabilization(CallbackInfo ci) {
        if (this.fullyStabilized) {
            int valid = 0;
            for (ChunkCoordinates cc : cachedStabilizers) {
                if (worldObj.getTileEntity(cc.posX, cc.posY, cc.posZ) instanceof TileStabilizer) {
                    valid++;
                }
            }

            if (valid >= 4) {
                this.instability = 0;
                this.countDelay = 1;
            } else {
                this.fullyStabilized = false;
            }
        }
    }

    @Inject(method = "writeCustomNBT", at = @At("TAIL"), remap = false)
    private void writeStabilizerData(NBTTagCompound nbt, CallbackInfo ci) {
        nbt.setBoolean("fullyStabilized", this.fullyStabilized);

        NBTTagList list = new NBTTagList();
        for (ChunkCoordinates cc : cachedStabilizers) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("x", cc.posX);
            tag.setInteger("y", cc.posY);
            tag.setInteger("z", cc.posZ);
            list.appendTag(tag);
        }
        nbt.setTag("stabilizerCoords", list);
    }

    @Inject(method = "readCustomNBT", at = @At("TAIL"), remap = false)
    private void readStabilizerData(NBTTagCompound nbt, CallbackInfo ci) {
        this.fullyStabilized = nbt.getBoolean("fullyStabilized");

        this.cachedStabilizers.clear();
        if (nbt.hasKey("stabilizerCoords")) {
            NBTTagList list = nbt.getTagList("stabilizerCoords", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                this.cachedStabilizers.add(new ChunkCoordinates(
                    tag.getInteger("x"),
                    tag.getInteger("y"),
                    tag.getInteger("z")
                ));
            }
        }
    }

    public AspectList foxDarkMagic$getRecipeEssentiaDirect() {
        return this.recipeEssentia;
    }
}
