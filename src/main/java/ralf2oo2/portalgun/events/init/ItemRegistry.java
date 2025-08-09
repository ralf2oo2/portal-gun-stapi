package ralf2oo2.portalgun.events.init;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.util.Identifier;
import ralf2oo2.portalgun.PortalGun;
import ralf2oo2.portalgun.item.PortalGunItem;

public class ItemRegistry {
    public static Item portalGunItem;
    @EventListener
    public void registerItems(ItemRegistryEvent event){
        portalGunItem = new PortalGunItem(Identifier.of(PortalGun.NAMESPACE, "portal_gun")).setTranslationKey(Identifier.of(PortalGun.NAMESPACE, "portal_gun"));
    }
}
