package ralf2oo2.portalgun.packet;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.math.StationBlockPos;
import org.jetbrains.annotations.NotNull;
import ralf2oo2.portalgun.PortalGun;
import ralf2oo2.portalgun.block.entity.PortalBlockEntity;
import ralf2oo2.portalgun.portal.PortalInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestTeleportPacket extends Packet implements ManagedPacket<RequestTeleportPacket> {
    public BlockPos pos;

    public static final PacketType<RequestTeleportPacket> TYPE = PacketType.builder(false, true, RequestTeleportPacket::new).build();

    public RequestTeleportPacket(){}
    public RequestTeleportPacket(BlockPos pos){
        this.pos = pos;
    }
    @Override
    public void read(DataInputStream stream) {
        try {
            this.pos = StationBlockPos.fromLong(stream.readLong());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeLong(pos.asLong());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity playerEntity = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(playerEntity == null) return;
        if(playerEntity instanceof ServerPlayerEntity serverPlayerEntity){
            BlockEntity blockEntity = serverPlayerEntity.world.getBlockEntity(pos.getX(), pos.getY(), pos.getZ());
            if(blockEntity instanceof PortalBlockEntity portalBlockEntity){
                if(PortalGun.getState(serverPlayerEntity.world).portalInfo.containsKey(serverPlayerEntity.world.dimension.id)){
                    PortalInfo info = PortalGun.getState(serverPlayerEntity.world).portalInfo.get(serverPlayerEntity.world.dimension.id).get(portalBlockEntity.orange ? "blue" : "orange");
                    if(info != null){
                        BlockEntity destinationBlockEntity = serverPlayerEntity.world.getBlockEntity(info.pos.getX(), info.pos.getY(), info.pos.getZ());
                        if(destinationBlockEntity instanceof PortalBlockEntity destinationPortalBlockEntity){
                            portalBlockEntity.teleport(serverPlayerEntity, destinationPortalBlockEntity);
                            serverPlayerEntity.networkHandler.teleport(serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z, serverPlayerEntity.yaw, serverPlayerEntity.pitch);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull PacketType<RequestTeleportPacket> getType() {
        return TYPE;
    }
}
