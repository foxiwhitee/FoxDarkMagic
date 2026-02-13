package foxiwhitee.FoxDarkMagic.helpers;

import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.WorldCoordinates;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXEssentiaSource;
import thaumcraft.common.tiles.TileMirrorEssentia;

import java.util.ArrayList;
import java.util.HashMap;

public class FoxEssentiaHelper {
    private static final HashMap<WorldCoordinates, ArrayList<WorldCoordinates>> sources = new HashMap<>();
    private static final HashMap<WorldCoordinates, Long> sourcesDelay = new HashMap<>();

    public static ArrayList<WorldCoordinates> getSourcesList(TileEntity tile, ForgeDirection direction, int range) {
        WorldCoordinates tileLoc = new WorldCoordinates(tile.xCoord, tile.yCoord, tile.zCoord, tile.getWorldObj().provider.dimensionId);

        if (!sources.containsKey(tileLoc)) {
            getSources(tile.getWorldObj(), tileLoc, direction, range);
        }

        return sources.getOrDefault(tileLoc, new ArrayList<>());
    }

    public static boolean drainEssentia(TileEntity tile, Aspect aspect, int count, ForgeDirection direction, int range, boolean ignoreMirror) {
        WorldCoordinates tileLoc = new WorldCoordinates(tile.xCoord, tile.yCoord, tile.zCoord, tile.getWorldObj().provider.dimensionId);

        if (!sources.containsKey(tileLoc)) {
            getSources(tile.getWorldObj(), tileLoc, direction, range);
            if (!sources.containsKey(tileLoc)) return false;
        }

        ArrayList<WorldCoordinates> sourceList = sources.get(tileLoc);
        for (WorldCoordinates source : sourceList) {
            TileEntity sourceTile = tile.getWorldObj().getTileEntity(source.x, source.y, source.z);

            if (sourceTile == null) {
                sources.remove(tileLoc);
            }

            if (!(sourceTile instanceof IAspectSource as)) {
                continue;
            }

            if (!ignoreMirror || !(sourceTile instanceof TileMirrorEssentia)) {
                if (as.takeFromContainer(aspect, count)) {
                    PacketHandler.INSTANCE.sendToAllAround(
                        new PacketFXEssentiaSource(tile.xCoord, tile.yCoord, tile.zCoord,
                            (byte) (tile.xCoord - source.x), (byte) (tile.yCoord - source.y), (byte) (tile.zCoord - source.z),
                            aspect.getColor()),
                        new NetworkRegistry.TargetPoint(tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord, 32.0D)
                    );
                    return true;
                }
            }
        }

        return false;
    }

    private static void getSources(World world, WorldCoordinates tileLoc, ForgeDirection direction, int range) {
        if (sourcesDelay.containsKey(tileLoc)) {
            if (sourcesDelay.get(tileLoc) > System.currentTimeMillis()) {
                return;
            }
            sourcesDelay.remove(tileLoc);
        }

        ArrayList<WorldCoordinates> sourceList = new ArrayList<>();
        int start = (direction == ForgeDirection.UNKNOWN) ? -range : 0;
        ForgeDirection dir = (direction == ForgeDirection.UNKNOWN) ? ForgeDirection.UP : direction;

        for (int aa = -range; aa <= range; ++aa) {
            for (int bb = -range; bb <= range; ++bb) {
                for (int cc = start; cc <= range; ++cc) {
                    if (aa == 0 && bb == 0 && cc == 0) {
                        continue;
                    }

                    int xx = tileLoc.x, yy = tileLoc.y, zz = tileLoc.z;
                    if (dir.offsetY != 0) {
                        xx += aa;
                        yy += cc * dir.offsetY;
                        zz += bb;
                    } else if (dir.offsetX == 0) {
                        xx += aa;
                        yy += bb;
                        zz += cc * dir.offsetZ;
                    } else {
                        xx += cc * dir.offsetX;
                        yy += aa;
                        zz += bb;
                    }

                    TileEntity te = world.getTileEntity(xx, yy, zz);
                    if (te instanceof IAspectSource) {
                        sourceList.add(new WorldCoordinates(xx, yy, zz, world.provider.dimensionId));
                    }
                }
            }
        }

        if (sourceList.isEmpty()) {
            sourcesDelay.put(tileLoc, System.currentTimeMillis() + 5000L);
        } else {
            sources.put(tileLoc, sourceList);
        }
    }

    public static void removeSource(TileEntity tile) {
        WorldCoordinates tileLoc = new WorldCoordinates(tile.xCoord, tile.yCoord, tile.zCoord, tile.getWorldObj().provider.dimensionId);
        sources.remove(tileLoc);
        sourcesDelay.remove(tileLoc);
    }
}
