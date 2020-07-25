package io.github.marcuscastelo.quartus.util;

import io.github.marcuscastelo.quartus.block.circuit_parts.WireBlock;
import io.github.marcuscastelo.quartus.registry.QuartusProperties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.HashMap;
import java.util.Map;

public class WireShapes {
    public static final float WIRE_HEIGHT = 2/16f;
    public static final float WIRE_SIDE_START = 6/16f;
    public static final float WIRE_SIDE_END = 10/16f;

    public static final float WIRE_EDGE_1_START = 0f;
    public static final float WIRE_EDGE_1_END = 6/16f;
    public static final float WIRE_EDGE_2_START = 10/16f;
    public static final float WIRE_EDGE_2_END = 1f;

    public static final float WIRE_UP_Y_START = WIRE_HEIGHT;
    public static final float WIRE_UP_Y_END = WIRE_HEIGHT + 1F;
    public static final float WIRE_UP_THICKNESS = WIRE_HEIGHT;


    public static final VoxelShape BASE_DOT_SHAPE = VoxelShapes.cuboid(6/16f, 0, 6/16f, 10/16f, WIRE_HEIGHT, 10/16f);
    public static final Map<Direction, VoxelShape> BASE_EDGE_PER_DIR_MAP = new HashMap<>();
    public static final Map<Direction, VoxelShape> UP_EDGE_PER_DIR_MAP = new HashMap<>();

    public static VoxelShape getBaseEdgeShape(Direction direction) {
        return BASE_EDGE_PER_DIR_MAP.getOrDefault(direction, VoxelShapes.empty());
    }

    public static VoxelShape getBaseShape(Direction facingDirection, Direction auxDirection) {
        return VoxelShapes.union(BASE_DOT_SHAPE, getBaseEdgeShape(facingDirection), getBaseEdgeShape(auxDirection));
    }

    public static VoxelShape getUpEdgeVoxelShape(Direction direction) {
        return UP_EDGE_PER_DIR_MAP.getOrDefault(direction, VoxelShapes.empty());
    }

    public static VoxelShape getUpVoxelShape(Direction facingDirection, Direction auxDirection, WireBlock.UpValue a) {
        VoxelShape initialShape = VoxelShapes.empty();
        if (a == WireBlock.UpValue.FACING) {
            initialShape = VoxelShapes.union(getUpEdgeVoxelShape(facingDirection));
        } else if (a == WireBlock.UpValue.BOTH) {
            initialShape = VoxelShapes.union(getUpEdgeVoxelShape(facingDirection), getUpEdgeVoxelShape(auxDirection));
        }
        return initialShape;
    }

    static  {
        BASE_EDGE_PER_DIR_MAP.put(Direction.NORTH, VoxelShapes.cuboid(WIRE_SIDE_START,0, WIRE_EDGE_1_START, WIRE_SIDE_END, WIRE_HEIGHT, WIRE_EDGE_1_END));
        BASE_EDGE_PER_DIR_MAP.put(Direction.SOUTH, VoxelShapes.cuboid(WIRE_SIDE_START,0, WIRE_EDGE_2_START, WIRE_SIDE_END, WIRE_HEIGHT, WIRE_EDGE_2_END));
        BASE_EDGE_PER_DIR_MAP.put(Direction.WEST, VoxelShapes.cuboid(WIRE_EDGE_1_START,0, WIRE_SIDE_START, WIRE_EDGE_1_END, WIRE_HEIGHT, WIRE_SIDE_END));
        BASE_EDGE_PER_DIR_MAP.put(Direction.EAST, VoxelShapes.cuboid(WIRE_EDGE_2_START,0, WIRE_SIDE_START, WIRE_EDGE_2_END, WIRE_HEIGHT, WIRE_SIDE_END));

        UP_EDGE_PER_DIR_MAP.put(Direction.NORTH, VoxelShapes.cuboid(WIRE_SIDE_START, WIRE_UP_Y_START, 0, WIRE_SIDE_END, WIRE_UP_Y_END, WIRE_UP_THICKNESS));
        UP_EDGE_PER_DIR_MAP.put(Direction.SOUTH, VoxelShapes.cuboid(WIRE_SIDE_START, WIRE_UP_Y_START, 1, WIRE_SIDE_END, WIRE_UP_Y_END, 1-WIRE_UP_THICKNESS));
        UP_EDGE_PER_DIR_MAP.put(Direction.WEST, VoxelShapes.cuboid(0, WIRE_UP_Y_START, WIRE_SIDE_START, WIRE_UP_THICKNESS, WIRE_UP_Y_END, WIRE_SIDE_END));
        UP_EDGE_PER_DIR_MAP.put(Direction.EAST, VoxelShapes.cuboid(1, WIRE_UP_Y_START, WIRE_SIDE_START, 1-WIRE_UP_THICKNESS, WIRE_UP_Y_END, WIRE_SIDE_END));
    }
}
