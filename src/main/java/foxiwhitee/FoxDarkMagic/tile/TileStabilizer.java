package foxiwhitee.FoxDarkMagic.tile;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.network.NetworkRegistry;
import dev.rndmorris.salisarcana.api.IVariableInfusionStabilizer;
import foxiwhitee.FoxDarkMagic.api.IInfusionMatrixAccessor;
import foxiwhitee.FoxLib.tile.FoxBaseTile;
import foxiwhitee.FoxLib.tile.event.TileEvent;
import foxiwhitee.FoxLib.tile.event.TileEventType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.api.WorldCoordinates;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXEssentiaSource;
import thaumcraft.common.tiles.TileInfusionMatrix;
import thaumcraft.common.tiles.TileMirrorEssentia;

import java.util.HashMap;
import java.util.Map;

public class TileStabilizer extends FoxBaseTile {
    private static final int RANGE = 5;
    private static final int SCAN_INTERVAL = 40;
    private static final int STABILIZER_LIMIT_PER_MATRIX = 10;
    private static final Map<WorldCoordinates, Integer> matrixUsageMap = new HashMap<>();

    private int targetX, targetY, targetZ;
    private boolean hasTarget = false;

    @TileEvent(TileEventType.TICK)
    public void tick() {
        if (worldObj.isRemote) {
            return;
        }

        TileInfusionMatrix matrix = getOrFindMatrix();
        if (matrix == null || !matrix.active) return;

        if (worldObj.getTotalWorldTime() % 2L == 0) {
            processEssentiaStabilization(matrix);
        }
    }

    private TileInfusionMatrix getOrFindMatrix() {
        TileEntity te = worldObj.getTileEntity(targetX, targetY, targetZ);
        if (te instanceof TileInfusionMatrix) {
            return (TileInfusionMatrix) te;
        }

        if (worldObj.getTotalWorldTime() % SCAN_INTERVAL == 0L) {
            return findNewMatrix();
        }
        return null;
    }

    private TileInfusionMatrix findNewMatrix() {
        for (int x = xCoord - RANGE; x <= xCoord + RANGE; x++) {
            for (int y = yCoord - RANGE; y <= yCoord + RANGE; y++) {
                for (int z = zCoord - RANGE; z <= zCoord + RANGE; z++) {
                    TileEntity te = worldObj.getTileEntity(x, y, z);
                    if (te instanceof TileInfusionMatrix) {
                        WorldCoordinates coords = new WorldCoordinates(x, y, z, worldObj.provider.dimensionId);
                        int count = matrixUsageMap.getOrDefault(coords, 0);

                        if (count < STABILIZER_LIMIT_PER_MATRIX) {
                            this.targetX = x;
                            this.targetY = y;
                            this.targetZ = z;
                            this.hasTarget = true;
                            matrixUsageMap.put(coords, count + 1);
                            return (TileInfusionMatrix) te;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void processEssentiaStabilization(TileInfusionMatrix matrix) {
        AspectList needed = getRecipeEssentia(matrix);
        if (needed == null || needed.visSize() <= 0) return;

        for (Aspect aspect : needed.getAspects()) {
            if (needed.getAmount(aspect) <= 0) continue;

            WorldCoordinates sourceCoords = findEssentiaSource(aspect);

            if (sourceCoords != null) {
                sendVisualEffect(matrix, sourceCoords, aspect);

                needed.reduce(aspect, 1);
                matrix.markDirty();
                worldObj.markBlockForUpdate(matrix.xCoord, matrix.yCoord, matrix.zCoord);
                break;
            }
        }
    }

    private WorldCoordinates findEssentiaSource(Aspect aspect) {
        for (int x = xCoord - RANGE; x <= xCoord + RANGE; x++) {
            for (int y = yCoord - RANGE; y <= yCoord + RANGE; y++) {
                for (int z = zCoord - RANGE; z <= zCoord + RANGE; z++) {
                    TileEntity te = worldObj.getTileEntity(x, y, z);

                    if (te instanceof IAspectSource && !(te instanceof TileMirrorEssentia)) {
                        if (((IAspectSource) te).takeFromContainer(aspect, 1)) {
                            return new WorldCoordinates(x, y, z, worldObj.provider.dimensionId);
                        }
                    }
                }
            }
        }
        return null;
    }

    private void sendVisualEffect(TileInfusionMatrix matrix, WorldCoordinates source, Aspect aspect) {
        int mx = matrix.xCoord;
        int my = matrix.yCoord;
        int mz = matrix.zCoord;

        byte dx = (byte) (mx - source.x);
        byte dy = (byte) (my - source.y);
        byte dz = (byte) (mz - source.z);

        PacketFXEssentiaSource fx = new PacketFXEssentiaSource(mx, my, mz, dx, dy, dz, aspect.getColor());

        PacketHandler.INSTANCE.sendToAllAround(fx,
            new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, mx, my, mz, 32.0D));
    }

    private AspectList getRecipeEssentia(TileInfusionMatrix matrix) {
        try {
            return ((IInfusionMatrixAccessor)((Object)matrix)).foxDarkMagic$getRecipeEssentiaDirect();
        } catch (Exception e) {
            return null;
        }
    }

    @TileEvent(TileEventType.SERVER_NBT_WRITE)
    public void writeToNbt(NBTTagCompound data) {
        if (hasTarget) {
            data.setInteger("targetX", targetX);
            data.setInteger("targetY", targetY);
            data.setInteger("targetZ", targetZ);
            data.setBoolean("hasTarget", true);
        }
    }

    @TileEvent(TileEventType.SERVER_NBT_READ)
    public void readFromNbt(NBTTagCompound data) {
        this.hasTarget = data.getBoolean("hasTarget");
        if (hasTarget) {
            this.targetX = data.getInteger("targetX");
            this.targetY = data.getInteger("targetY");
            this.targetZ = data.getInteger("targetZ");
        }
    }
}
