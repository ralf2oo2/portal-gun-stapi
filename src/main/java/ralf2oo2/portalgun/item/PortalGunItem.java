package ralf2oo2.portalgun.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import ralf2oo2.portalgun.entity.PortalProjectileEntity;

public class PortalGunItem extends TemplateItem {
    public PortalGunItem(Identifier identifier) {
        super(identifier);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, PlayerEntity user) {
        if(!world.isRemote){
            world.playSound(user.x, user.y + user.getEyeHeight(), user.z, user.isSneaking() ? "fireblue" : "firered", 0.3F, 1.0F);
            world.spawnEntity(new PortalProjectileEntity(world, user, user.isSneaking()));
        }
        return stack;
    }
}
