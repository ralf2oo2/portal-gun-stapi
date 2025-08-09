package ralf2oo2.portalgun.packet;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;
import ralf2oo2.portalgun.client.PortalGunClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class EntityLocationPacket extends Packet implements ManagedPacket<EntityLocationPacket> {

    public int id;
    public double ltX;
    public double ltY;
    public double ltZ;
    public double prevX;
    public double prevY;
    public double prevZ;
    public double posX;
    public double posY;
    public double posZ;
    public double mX;
    public double mY;
    public double mZ;
    public float prevYaw;
    public float prevPitch;
    public float yaw;
    public float pitch;

    public static final PacketType<EntityLocationPacket> TYPE = PacketType.builder(true, false, EntityLocationPacket::new).build();

    public EntityLocationPacket(){}

    public EntityLocationPacket(Entity ent){
        id = ent.id;
        ltX = ent.lastTickX;
        ltY = ent.lastTickY;
        ltZ = ent.lastTickZ;
        prevX = ent.prevX;
        prevY = ent.prevY;
        prevZ = ent.prevZ;
        posX = ent.x;
        posY = ent.y;
        posZ = ent.z;
        mX = ent.velocityX;
        mY = ent.velocityY;
        mZ = ent.velocityZ;
        prevYaw = ent.prevYaw;
        prevPitch = ent.prevPitch;
        yaw = ent.yaw;
        pitch = ent.pitch;
    }

    @Override
    public @NotNull PacketType<EntityLocationPacket> getType() {
        return null;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            id = stream.readInt();
            ltX = stream.readDouble();
            ltY = stream.readDouble();
            ltZ = stream.readDouble();
            prevX = stream.readDouble();
            prevY = stream.readDouble();
            prevZ = stream.readDouble();
            posX = stream.readDouble();
            posY = stream.readDouble();
            posZ = stream.readDouble();
            mX = stream.readDouble();
            mY = stream.readDouble();
            mZ = stream.readDouble();
            prevYaw = stream.readFloat();
            prevPitch = stream.readFloat();
            yaw = stream.readFloat();
            pitch = stream.readFloat();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeInt(id);
            stream.writeDouble(ltX);
            stream.writeDouble(ltY);
            stream.writeDouble(ltZ);
            stream.writeDouble(prevX);
            stream.writeDouble(prevY);
            stream.writeDouble(prevZ);
            stream.writeDouble(posX);
            stream.writeDouble(posY);
            stream.writeDouble(posZ);
            stream.writeDouble(mX);
            stream.writeDouble(mY);
            stream.writeDouble(mZ);
            stream.writeFloat(prevYaw);
            stream.writeFloat(prevPitch);
            stream.writeFloat(yaw);
            stream.writeFloat(pitch);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT){
            handleClient(networkHandler);
        }
    }

    public void handleClient(NetworkHandler networkHandler){
        Entity ent = null;
        List<Entity> entities =  PortalGunClient.getMc().world.getEntities();
        for(Entity entity : entities){
            if(entity.id == id){
                ent = entity;
                break;
            }
        }
        if(ent != null)
        {
            ent.lastTickX = ltX;
            ent.lastTickY = ltY;
            ent.lastTickZ = ltZ;
            ent.prevX = prevX;
            ent.prevY = prevY;
            ent.prevZ = prevZ;
            ent.x = posX;
            ent.y = posY;
            ent.z = posZ;
            ent.velocityX = mX;
            ent.velocityY = mY;
            ent.velocityZ = mZ;
            ent.prevYaw = prevYaw;
            ent.prevPitch = prevPitch;
            ent.yaw = yaw;
            ent.pitch = pitch;

            if(ent == PortalGunClient.getMc().player)
            {
                PortalGunClient.justTeleported = true;
                PortalGunClient.mX = ent.velocityX;
                PortalGunClient.mY = ent.velocityY;
                PortalGunClient.mZ = ent.velocityZ;
            }
        }
    }

    @Override
    public int size() {
        return 0;
    }
}
