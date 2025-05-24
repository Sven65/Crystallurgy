package xyz.mackan.crystallurgy.util;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class BlockUtils {

    public enum Side {
        FRONT,
        BACK,
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

    public static Side getSideFromDirection(BlockState state, Direction direction) {
        Direction facing = state.get(Properties.HORIZONTAL_FACING);

        // Map the world-relative direction to block sides based on the facing of the block
        switch (facing) {
            case NORTH:
                // NORTH is the front, EAST is the right, WEST is the left, SOUTH is the back, UP is top, DOWN is bottom
                switch (direction) {
                    case NORTH: return Side.FRONT;
                    case EAST:  return Side.RIGHT;
                    case WEST:  return Side.LEFT;
                    case SOUTH: return Side.BACK;
                    case UP:    return Side.TOP;
                    case DOWN:  return Side.BOTTOM;
                    default: return null;
                }
            case SOUTH:
                // SOUTH is the front, WEST is the right, EAST is the left, NORTH is the back, UP is top, DOWN is bottom
                switch (direction) {
                    case SOUTH: return Side.FRONT;
                    case WEST:  return Side.RIGHT;
                    case EAST:  return Side.LEFT;
                    case NORTH: return Side.BACK;
                    case UP:    return Side.TOP;
                    case DOWN:  return Side.BOTTOM;
                    default: return null;
                }
            case EAST:
                // EAST is the front, NORTH is the right, SOUTH is the left, WEST is the back, UP is top, DOWN is bottom
                switch (direction) {
                    case EAST:  return Side.FRONT;
                    case NORTH: return Side.RIGHT;
                    case SOUTH: return Side.LEFT;
                    case WEST:  return Side.BACK;
                    case UP:    return Side.TOP;
                    case DOWN:  return Side.BOTTOM;
                    default: return null;
                }
            case WEST:
                // WEST is the front, SOUTH is the right, NORTH is the left, EAST is the back, UP is top, DOWN is bottom
                switch (direction) {
                    case WEST:  return Side.FRONT;
                    case SOUTH: return Side.RIGHT;
                    case NORTH: return Side.LEFT;
                    case EAST:  return Side.BACK;
                    case UP:    return Side.TOP;
                    case DOWN:  return Side.BOTTOM;
                    default: return null;
                }
            default:
                return null;
        }
    }
}