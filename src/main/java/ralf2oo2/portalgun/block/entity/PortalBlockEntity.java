package ralf2oo2.portalgun.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.util.math.Direction;
import ralf2oo2.portalgun.PortalGun;
import ralf2oo2.portalgun.client.PortalGunClient;
import ralf2oo2.portalgun.mixin.EntityAccessor;
import ralf2oo2.portalgun.packet.RequestTeleportPacket;
import ralf2oo2.portalgun.portal.PortalInfo;

import java.util.List;

public class PortalBlockEntity extends BlockEntity {
    public boolean setup;
    public boolean top;
    public boolean orange;
    public Direction facing;

    public PortalBlockEntity(){
        top = false;
        orange = false;
        facing = Direction.DOWN;
    }

    @Override
    public void tick() {
        if(top)
        {
            return; //The top never does anything and is there just to look pretty.
        }

        BlockPos pairLocation = BlockPos.ORIGIN;
        if(!world.isRemote)
        {
            if(!PortalGun.getState(world).portalInfo.containsKey(world.dimension.id))
            {
                return;
            }
            PortalInfo info = PortalGun.getState(world).portalInfo.get(world.dimension.id).get(orange ? "blue" : "orange");
            if(info == null)
            {
                return;
            }
            pairLocation = info.pos;
        }
        else
        {
            if(orange && !PortalGunClient.status.blue || !orange && !PortalGunClient.status.orange)
            {
                return;
            }
        }
        //Only hits here if we have a pair
        Box aabbScan = Box.create(x, y, z, x + 1, y + (facing.getAxis() != Direction.Axis.Y ? 2 : 1), z + 1).expand(facing.getOffsetX() * 4, facing.getOffsetY() * 4, facing.getOffsetZ() * 4);
        Box aabbInside = Box.create(x, y, z, x + 1, y + (facing.getAxis() != Direction.Axis.Y ? 2 : 1), z + 1).expand(facing.getOffsetX() * 9, facing.getOffsetY() * 9, facing.getOffsetZ() * 9).offset(-facing.getOffsetX() * 9.999D, -facing.getOffsetY() * 9.999D, -facing.getOffsetZ() * 9.999D);
        List<Entity> ents = world.collectEntitiesByClass(world.isRemote ? PlayerEntity.class : Entity.class, aabbScan);
        for(Entity ent : ents)
        {
            if(!world.isRemote && ent instanceof PlayerEntity)
            {
                continue; //we ignore players. They tell the server when they want a teleport.
            }

            if(ent.getBoundingBox() != null && ent.getBoundingBox().offset(ent.velocityX, ent.velocityY, ent.velocityZ).intersects(aabbInside))
            {
                if(world.isRemote)
                {
                    handleClientTeleport((PlayerEntity) ent);
                }
                else
                {
                    BlockEntity te = world.getBlockEntity(pairLocation.getX(), pairLocation.getY(), pairLocation.getZ());
                    if(te instanceof PortalBlockEntity portalBlockEntity)
                    {
                        teleport(ent, portalBlockEntity);
                    }
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public void handleClientTeleport(PlayerEntity playerEntity){
        if(PortalGunClient.teleportCooldown <= 0 && playerEntity == PortalGunClient.getMc().player){
            PortalGunClient.teleportCooldown = 3;
            PacketHelper.send(new RequestTeleportPacket(new BlockPos(this.x, this.y, this.z)));
        }
    }

    public void teleport(Entity ent, PortalBlockEntity pair)
    {
        ent.x = pair.x + 0.5D - (0.5D - (ent.getBoundingBox().maxX - ent.getBoundingBox().minX) / 2D) * 0.99D * pair.facing.getOffsetX();
        ent.z = pair.z + 0.5D - (0.5D - (ent.getBoundingBox().maxZ - ent.getBoundingBox().minZ) / 2D) * 0.99D * pair.facing.getOffsetZ();

        ent.y = pair.y + (pair.facing.getOffsetY() < 0 ? -(ent.getBoundingBox().maxY - ent.getBoundingBox().minY) + 0.999D : 0.001D);

        if(facing.getAxis() != Direction.Axis.Y && pair.facing.getAxis() != Direction.Axis.Y) //horizontal
        {
            float yawDiff = facing.getHorizontal() - (pair.facing.getOpposite().getHorizontal());
            ent.pitch -= yawDiff;
            ent.yaw -= yawDiff;

            double mX = ent.velocityX;
            double mZ = ent.velocityZ;

            if(pair.facing == facing)
            {
                ent.velocityX = -mX;
                ent.velocityZ = -mZ;
            }
            else if(facing.getAxis() == Direction.Axis.X)
            {
                if(pair.facing == Direction.NORTH)
                {
                    ent.velocityZ = -mX * - facing.getOffsetX();
                    ent.velocityX = mZ * - facing.getOffsetX();
                }
                else if(pair.facing == Direction.SOUTH)
                {
                    ent.velocityZ = mX * - facing.getOffsetX();
                    ent.velocityX = -mZ * - facing.getOffsetX();
                }
            }
            else if(facing.getAxis() == Direction.Axis.Z)
            {
                if(pair.facing == Direction.EAST)
                {
                    ent.velocityZ = -mX * - facing.getOffsetZ();
                    ent.velocityX = mZ * - facing.getOffsetZ();
                }
                else if(pair.facing == Direction.WEST)
                {
                    ent.velocityZ = mX * - facing.getOffsetZ();
                    ent.velocityX = -mZ * - facing.getOffsetZ();
                }
            }
        }
        else if(facing.getAxis() == Direction.Axis.Y && pair.facing.getAxis() != Direction.Axis.Y) //from vertical to horizontal
        {
            ent.pitch = 0F;
            ent.yaw = pair.facing.getHorizontal();
            ent.velocityX = Math.abs(ent.velocityY) * pair.facing.getOffsetX();
            ent.velocityZ = Math.abs(ent.velocityY) * pair.facing.getOffsetZ();
            ent.velocityY = 0D;
            ((EntityAccessor)ent).setFallDistance(0);
        }
        else if(facing.getAxis() != Direction.Axis.Y && pair.facing.getAxis() == Direction.Axis.Y) //from horizontal to vertical
        {
            ent.velocityY = Math.sqrt(ent.velocityX * ent.velocityX + ent.velocityZ * ent.velocityZ) * pair.facing.getOffsetY();
        }
        else //vertical only
        {
            if(pair.facing == facing)
            {
                ent.velocityY = -ent.velocityY;
            }
            ((EntityAccessor)ent).setFallDistance(0);
        }
        ent.velocityX += pair.facing.getOffsetX() * 0.2D;
        ent.velocityY += pair.facing.getOffsetY() * 0.2D;
        ent.velocityZ += pair.facing.getOffsetZ() * 0.2D;
        ent.setPositionAndAngles(ent.x, ent.y, ent.z, ent.yaw, ent.pitch);

        world.playSound(this.x + 0.5D, this.y + (facing.getAxis() != Direction.Axis.Y ? 1D : 0.5D), this.z + 0.5D, "entersound", 0.1F, 1.0F);
        world.playSound(pair.x + 0.5D, pair.y + (pair.facing.getAxis() != Direction.Axis.Y ? 1D : 0.5D), pair.z + 0.5D, "exitsound", 0.1F, 1.0F);

        //PortalGunClassic.channel.sendToAllAround(new PacketEntityLocation(ent), new NetworkRegistry.TargetPoint(ent.getEntityWorld().provider.getDimension(), pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 256));
    }

    //TODO: update packet

    public void setup(boolean top, boolean orange, Direction facing)
    {
        this.setup = true;

        this.top = top;
        this.orange = orange;
        this.facing = facing;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putBoolean("setup", setup);
        nbt.putBoolean("top", top);
        nbt.putBoolean("orange", orange);
        nbt.putInt("face", facing.getId());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        setup = nbt.getBoolean("setup");
        top = nbt.getBoolean("top");
        orange = nbt.getBoolean("orange");
        facing = Direction.byId(nbt.getInt("face"));
    }
}
