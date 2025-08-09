package ralf2oo2.portalgun.events.init;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent;
import net.modificationstation.stationapi.api.util.Identifier;
import ralf2oo2.portalgun.PortalGun;
import ralf2oo2.portalgun.block.entity.PortalBlockEntity;

public class BlockEntityRegistry {
    @EventListener
    public void registerBlockEntities(BlockEntityRegisterEvent event){
        event.register(PortalBlockEntity.class, Identifier.of(PortalGun.NAMESPACE, "portal_blockentity").toString());
    }

}
