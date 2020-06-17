package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit_logic.QuartusOutput;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusOutputConvertible;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class OutputGateBlock extends AbstractNodeBlock implements QuartusOutputConvertible {
    public OutputGateBlock() {
        super(Settings.copy(Blocks.LEVER));
        setDefaultState(this.getStateManager().getDefaultState().with(Properties.POWERED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return VoxelShapes.cuboid(0f, 0.0f, 0f, 1f, 2/16f, 1f);
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.POWERED);
    }

    @Override
    public QuartusOutput createQuartusOutput(World world, BlockPos pos) {
        return new QuartusOutput(world, pos) {
            @Override
            public void setInGameValue(boolean newValue) {
                BlockState bs = world.getBlockState(pos);
                world.setBlockState(pos, bs.with(Properties.POWERED, newValue));
            }
        };
    }
}