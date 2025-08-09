package ralf2oo2.portalgun.packet;

import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;
import ralf2oo2.portalgun.client.PortalGunClient;
import ralf2oo2.portalgun.client.portal.PortalStatus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PortalStatusPacket extends Packet implements ManagedPacket<PortalStatusPacket> {
    public boolean blue;
    public boolean orange;

    public static final PacketType<PortalStatusPacket> TYPE = PacketType.builder(true, false, PortalStatusPacket::new).build();

    public PortalStatusPacket(){}

    public PortalStatusPacket(boolean blue, boolean orange){
        this.blue = blue;
        this.orange = orange;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            blue = stream.readBoolean();
            orange = stream.readBoolean();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeBoolean(blue);
            stream.writeBoolean(orange);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PortalGunClient.status = new PortalStatus(blue, orange);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull PacketType<PortalStatusPacket> getType() {
        return TYPE;
    }
}
