package ralf2oo2.portalgun.portal;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.States;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.StationBlockPos;
import ralf2oo2.portalgun.block.entity.PortalBlockEntity;

public class PortalInfo {
    public boolean isOrange;
    public BlockPos pos;

    public PortalInfo(boolean o, BlockPos poss)
    {
        isOrange = o;
        pos = poss;
    }

    public void kill(World world)
    {
        BlockEntity blockEntity = world.getBlockEntity(pos.getX(), pos.getY(), pos.getZ());
        if(blockEntity instanceof PortalBlockEntity portalBlockEntity)
        {
            world.setBlockStateWithNotify(pos, States.AIR.get());
            if(portalBlockEntity.facing.getAxis() != Direction.Axis.Y)
            {
                BlockPos offset = portalBlockEntity.top ? pos.down() : pos.up();
                if(world.getBlockEntity(offset.getX(), offset.getY(), offset.getZ()) instanceof PortalBlockEntity)
                {
                    world.setBlockStateWithNotify(offset, States.AIR.get());
                }
            }

            world.playSound(pos.getX() + (portalBlockEntity.facing.getAxis() != Direction.Axis.Y ? 1D : 0.5D), pos.getY() + (portalBlockEntity.facing.getAxis() == Direction.Axis.Y ? 0.0D : 0.5D), pos.getZ() + (portalBlockEntity.facing.getAxis() != Direction.Axis.Y ? 1D : 0.5D), "random.hurt1", 0.3F, 1F);
        }
        else
        {
            world.setBlockStateWithNotify(pos, States.AIR.get());
        }
    }

    public NbtCompound toNBT()
    {
        NbtCompound tag = new NbtCompound();
        tag.putBoolean("orange", isOrange);
        tag.putLong("pos", pos.asLong());
        return tag;
    }

    public static PortalInfo createFromNBT(NbtCompound tag)
    {
        return new PortalInfo(tag.getBoolean("orange"), StationBlockPos.fromLong(tag.getLong("pos")));
    }
}
