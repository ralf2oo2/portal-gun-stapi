package ralf2oo2.portalgun.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import ralf2oo2.portalgun.client.portal.PortalStatus;

public class PortalGunClient {
    public static boolean keySwitchDown = false;
    public static boolean keyResetDown = false;

    public static PortalStatus status = null;
    public static int teleportCooldown = 0;

    public static boolean justTeleported = false;
    public static double mX = 0D;
    public static double mY = 0D;
    public static double mZ = 0D;

    public static Minecraft getMc(){
        return Minecraft.class.cast(FabricLoader.getInstance().getGameInstance());
    }
}
