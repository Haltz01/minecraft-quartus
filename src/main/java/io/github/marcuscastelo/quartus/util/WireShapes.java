package io.github.marcuscastelo.quartus.util;

import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.HashMap;
import java.util.Map;

import static io.github.marcuscastelo.quartus.registry.QuartusProperties.UpValue;

/**
 *  Classe que define as constantes relacionadas às dimensões
 *  do WireBlock, de acordo com o estado dele (conexões com outros fios ou componentes) *
 */
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

    /**
     * Obtém a forma da ponta do fio na base em certa direção horizontal
     * @param direction Direção desejada
     * @return          Forma da ponta do fio
     */
    public static VoxelShape getBaseEdgeShape(Direction direction) {
        return BASE_EDGE_PER_DIR_MAP.getOrDefault(direction, VoxelShapes.empty());
    }

    /**
     * Obtém a forma total da base, dadas as duas direções do fio
     * @param facingDirection   Direção para qual o fio "olha" (direção principal)
     * @param auxDirection      Direção auxiliar (determinada por propriedades do fio e pela direção principal)
     * @return                  Forma da base, com o ponto central e as extremidades de cada direção
     */
    public static VoxelShape getBaseShape(Direction facingDirection, Direction auxDirection) {
        return VoxelShapes.union(BASE_DOT_SHAPE, getBaseEdgeShape(facingDirection), getBaseEdgeShape(auxDirection));
    }

    /**
     * Obtém a forma da extremidade que sobe na direção informada
     * @param direction Direção usada
     * @return          Forma da extremidade que sobe (apenas a parte da parede)
     */
    public static VoxelShape getUpEdgeVoxelShape(Direction direction) {
        return UP_EDGE_PER_DIR_MAP.getOrDefault(direction, VoxelShapes.empty());
    }

    /**
     * Obtém todas as extremidades que sobem, de acordo com as propriedades do fio
     * @param facingDirection   Direção para a qual o fio "olha"
     * @param auxDirection      Direção para qual a outra estremidade está apontada
     * @param upValue           Estado das extremidades, quais estão subindo
     * @return                  Forma da união de todos as formas que sobem
     */
    public static VoxelShape getUpVoxelShape(Direction facingDirection, Direction auxDirection, UpValue upValue) {
        VoxelShape initialShape = VoxelShapes.empty();
        if (upValue == UpValue.FACING) {
            initialShape = VoxelShapes.union(getUpEdgeVoxelShape(facingDirection));
        } else if (upValue == UpValue.BOTH) {
            initialShape = VoxelShapes.union(getUpEdgeVoxelShape(facingDirection), getUpEdgeVoxelShape(auxDirection));
        }
        return initialShape;
    }

    static  {
        //Cadastra as extremidades de acordo com a direção
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
