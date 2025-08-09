package ralf2oo2.portalgun.events.init;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.util.Identifier;
import ralf2oo2.portalgun.PortalGun;
import ralf2oo2.portalgun.block.PortalBlock;

public class BlockRegistry {
    public static Block portalBlock;
    @EventListener
    public void registerBlocks(BlockRegistryEvent event){
        portalBlock = new PortalBlock(Identifier.of(PortalGun.NAMESPACE, "portal_block")).setTranslationKey("portal_block");
    }
}
