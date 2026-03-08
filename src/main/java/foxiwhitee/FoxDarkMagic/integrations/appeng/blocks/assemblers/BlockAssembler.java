package foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.assemblers;

import foxiwhitee.FoxDarkMagic.DarkCore;
import foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.AppliedBlock;
import net.minecraft.tileentity.TileEntity;

public abstract class BlockAssembler extends AppliedBlock {
    public BlockAssembler(String name, Class<? extends TileEntity> tileEntityClass) {
        super(name, tileEntityClass);
        setLightLevel(1F);
        setBlockTextureName(DarkCore.MODID + ":encoders/arcaneEncoderDown");
    }

    @Override
    public String getFolder() {
        return "assemblers/";
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

}
