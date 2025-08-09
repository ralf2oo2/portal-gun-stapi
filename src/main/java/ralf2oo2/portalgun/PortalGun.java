package ralf2oo2.portalgun;

import net.minecraft.world.World;
import ralf2oo2.portalgun.state.PortalState;

public class PortalGun {
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
