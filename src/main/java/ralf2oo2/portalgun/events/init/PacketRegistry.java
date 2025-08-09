package ralf2oo2.portalgun.events.init;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.network.packet.PacketRegisterEvent;
import net.modificationstation.stationapi.api.registry.PacketTypeRegistry;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.Identifier;
import ralf2oo2.portalgun.PortalGun;
import ralf2oo2.portalgun.packet.EntityLocationPacket;
import ralf2oo2.portalgun.packet.PortalStatusPacket;
import ralf2oo2.portalgun.packet.RequestTeleportPacket;

public class PacketRegistry {
    @EventListener
    public void registerPackets(PacketRegisterEvent event){
        Registry.register(PacketTypeRegistry.INSTANCE, Identifier.of(PortalGun.NAMESPACE, "entity_location_packet"), EntityLocationPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, Identifier.of(PortalGun.NAMESPACE, "portal_status_packet"), PortalStatusPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, Identifier.of(PortalGun.NAMESPACE, "request_teleport_packet"), RequestTeleportPacket.TYPE);
    }
}
