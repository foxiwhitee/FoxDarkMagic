package foxiwhitee.FoxDarkMagic.integrations.appeng.helpers;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrafterHelper {
    public static void trySendItems(AENetworkProxy proxy, BaseActionSource source, List<IAEStack<?>> needSend) {
        try {
            if (proxy != null) {
                List<IAEStack<?>> remove = new ArrayList<>();
                IMEMonitor<IAEItemStack> storage = proxy.getStorage().getItemInventory();
                IMEMonitor<IAEFluidStack> storageFluid = proxy.getStorage().getFluidInventory();
                for (IAEStack<?> s : needSend) {
                    if (s.getStackSize() == 0) {
                        remove.add(s);
                        continue;
                    }
                    IAEStack<?> rest = null;
                    if (s instanceof IAEItemStack stack) {
                        rest = Platform.poweredInsert(proxy.getEnergy(), storage, stack, source);
                    } else if (s instanceof IAEFluidStack stack) {
                        rest = Platform.poweredInsert(proxy.getEnergy(), storageFluid, stack, source);
                    }
                    if (rest != null && rest.getStackSize() > 0) {
                        s.setStackSize(rest.getStackSize());
                        break;
                    }
                    s.setStackSize(0);
                    remove.add(s);
                }
                needSend.removeAll(remove);
            }
        } catch (GridAccessException ignored) {
        }
    }

    public static List<IAEStack<?>> calculateOutputs(ICraftingPatternDetails activePattern, long craftCount, InventoryCrafting craftingGrid) {
        List<IAEStack<?>> outputs = calculateOutputs(activePattern, craftCount);
        for (int i = 0; i < craftingGrid.getSizeInventory(); i++) {
            ItemStack item = Platform.getContainerItem(craftingGrid.getStackInSlot(i));
            if (item != null) {
                outputs.add(AEItemStack.create(item));
            }
        }
        return outputs;
    }

    public static List<IAEStack<?>> calculateOutputs(ICraftingPatternDetails activePattern, long craftCount) {
        List<IAEStack<?>> outputs = new ArrayList<>();
        Arrays.stream(activePattern.getCondensedAEOutputs())
            .map(stack -> {
                IAEStack<?> copy = stack.copy();
                copy.setStackSize(copy.getStackSize() * craftCount);
                return copy;
            }).forEach(outputs::add);
        return outputs;
    }

    public static void writeToNbtNeedItems(NBTTagCompound data, List<IAEStack<?>> needSend) {
        data.setInteger("needSendSize", needSend.size());
        for (int i = 0; i < needSend.size(); i++) {
            IAEStack<?> is = needSend.get(i);
            NBTTagCompound tag = new NBTTagCompound();
            is.writeToNBT(tag);
            data.setTag("needSend_" + i, tag);
        }
    }

    public static void readFromNbtNeedItems(NBTTagCompound data, List<IAEStack<?>> needSend) {
        if (data.hasKey("needSendSize")) {
            int size = data.getInteger("needSendSize");
            needSend.clear();
            for (int i = 0; i < size; i++) {
                NBTTagCompound tag = data.getCompoundTag("needSend_" + i);
                needSend.add(AEItemStack.loadItemStackFromNBT(tag));
            }
        }
    }
}
