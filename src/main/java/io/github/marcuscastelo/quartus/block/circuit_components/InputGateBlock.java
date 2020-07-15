package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitComponent;
import io.github.marcuscastelo.quartus.circuit.components.QuartusCircuitInput;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.Collections;
import java.util.List;

public class InputGateBlock extends AbstractCircuitComponentBlock {
    public InputGateBlock() {
        super(Settings.of(Material.PART));
        setDefaultState(this.getStateManager().getDefaultState().with(Properties.POWERED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return VoxelShapes.cuboid(0f, 0.0f, 0f, 1f, 2/16f, 1f);
    }

    @Override
    public QuartusCircuitComponent getCircuitComponent() {
        return new QuartusCircuitInput() {
            @Override
            public void updateComponent() {}

            @Override
            public List<Direction> getPossibleInputDirections() {
                return InputGateBlock.this.getPossibleInputDirections();
            }

            @Override
            public List<Direction> getPossibleOutputDirections() {
                return InputGateBlock.this.getPossibleOutputDirections();
            }
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.POWERED);
    }

    List<Direction> possibleInputDirections = Collections.emptyList();
    List<Direction> possibleOutputDirections = Collections.singletonList(Direction.NORTH);

    @Override
    public List<Direction> getPossibleInputDirections() {
        return possibleInputDirections;
    }

    @Override
    public List<Direction> getPossibleOutputDirections() {
        return possibleOutputDirections;
    }
}
