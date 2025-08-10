package ralf2oo2.portalgun.util;

import net.modificationstation.stationapi.api.util.math.Direction;

public class Util {
    public static Direction getDirectionFromSide(int side){
        return switch (side) {
            case 1 -> Direction.DOWN;
            case 2 -> Direction.SOUTH;
            case 3 -> Direction.NORTH;
            case 4 -> Direction.EAST;
            case 5 -> Direction.WEST;
            default -> Direction.UP;
        };
    }
}
