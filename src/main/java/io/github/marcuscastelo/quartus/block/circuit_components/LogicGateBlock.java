package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

//TODO: tranformar num registry
public class LogicGateBlock extends AbstractCircuitComponentBlock {
    private Supplier<QuartusCircuitComponent> componentSupplier;

    protected LogicGateBlock() {
        super(Settings.copy(Blocks.REPEATER));
    }
    public LogicGateBlock(Supplier<QuartusCircuitComponent> componentSupplier) {
        this();
        this.componentSupplier = componentSupplier;
    }

    @Override
    public QuartusCircuitComponent getCircuitComponent() {
        return componentSupplier.get();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return VoxelShapes.cuboid(0f, 0.0f, 0f, 1f, 2/16f, 1f);
    }

    private static final List<Direction> possibleOutputDirections = Collections.singletonList(Direction.NORTH);
    private static final List<Direction> possibleInputDirections = Arrays.asList(Direction.EAST, Direction.WEST);

    @Override
    public List<Direction> getPossibleInputDirections() {
        return possibleInputDirections;
    }

    @Override
    public List<Direction> getPossibleOutputDirections() {
        return possibleOutputDirections;
    }
}
