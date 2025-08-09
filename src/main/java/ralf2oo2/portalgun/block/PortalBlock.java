package ralf2oo2.portalgun.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.States;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import ralf2oo2.portalgun.PortalGun;
import ralf2oo2.portalgun.block.entity.PortalBlockEntity;

public class PortalBlock extends TemplateBlockWithEntity {

    public static final Box EMPTY_BOX = Box.create(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

    public PortalBlock(Identifier identifier, Material material) {
        super(identifier, Material.METAL);
        setHardness(-1F);
        setResistance(1000000.0F);
        setLuminance(0.5F);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new PortalBlockEntity();
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public Box getBoundingBox(World world, int x, int y, int z) {
        return EMPTY_BOX;
    }

    @Override
    public Box getCollisionShape(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public void neighborUpdate(World world, int x, int y, int z, int id) {
        BlockEntity blockEntity = world.getBlockEntity(x, y, z);
        if(blockEntity instanceof PortalBlockEntity portalBlockEntity){
            if(portalBlockEntity.setup){
                if(portalBlockEntity.facing.getAxis() == Direction.Axis.Y){
                    if(!world.getBlockState(x, y, z).getBlock().isSolidFace(world, x, y, z, portalBlockEntity.facing.getId())){
                        PortalGun.getState(world).kill(world, portalBlockEntity.orange);
                        world.setBlockState(x, y, z, States.AIR.get());
                    }
                }
            }
        }
    }

    public static boolean canPlace(World world, BlockPos pos, Direction sideHit, boolean isOrange){
        if(world.getBlockState(pos).getMaterial().isReplaceable() || world.getBlockEntity(pos.getX(), pos.getY(), pos.getZ()) instanceof PortalBlockEntity && ((PortalBlockEntity)world.getBlockEntity(pos.getX(), pos.getY(), pos.getZ())).setup && ((PortalBlockEntity)world.getBlockEntity(pos.getX(), pos.getY(), pos.getZ())).orange == isOrange)
        {
            if(sideHit.getAxis() == Direction.Axis.Y) //1 block portal
            {
                BlockPos offset = pos.offset(sideHit, -1);
                return world.getBlockState(offset).getBlock().isSolidFace(world, offset.getX(), offset.getY(), offset.getZ(), sideHit.getId());
            }
            else
            {
                BlockPos offset = pos.offset(sideHit, -1);
                BlockPos posDown = pos.down();
                return world.getBlockState(offset).getBlock().isSolidFace(world, offset.getX(), offset.getY(), offset.getZ(), sideHit.getId()) && (world.getBlockState(posDown).getMaterial().isReplaceable() || world.getBlockEntity(posDown.getX(), posDown.getY(), posDown.getZ()) instanceof PortalBlockEntity && ((PortalBlockEntity)world.getBlockEntity(posDown.getX(), posDown.getY(), posDown.getZ())).setup && ((PortalBlockEntity)world.getBlockEntity(posDown.getX(), posDown.getY(), posDown.getZ())).orange == isOrange) && world.getBlockState(posDown).getBlock().isSolidFace(world, posDown.getX(), posDown.getY(), posDown.getZ(), sideHit.getId());
            }
        }
        return false;
    }
}
