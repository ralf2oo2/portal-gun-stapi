package ralf2oo2.portalgun;

import net.minecraft.world.World;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;
import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.Logger;
import ralf2oo2.portalgun.state.PortalState;

import java.lang.invoke.MethodHandles;

public class PortalGun {

    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static final Namespace NAMESPACE = Namespace.resolve();

    public static final Logger LOGGER = NAMESPACE.getLogger();

    public static PortalState getState(World world){
        PortalState state = (PortalState) world.getOrCreateState(PortalState.class, PortalState.DATA_ID);

        if(state == null){
            state = new PortalState(PortalState.DATA_ID);
            world.setState(PortalState.DATA_ID, state);
            state.markDirty();
        }
        return state;
    }
}
