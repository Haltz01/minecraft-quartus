package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit_logic.QuartusInput;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusInputConvertible;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class InputGateBlock extends AbstractNodeBlock implements QuartusInputConvertible {
    public InputGateBlock() {
        super(Settings.of(Material.PART));
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
    public QuartusInput createQuartusInput(World world, BlockPos pos) throws QuartusNode.QuartusWrongNodeBlockException {
        return new QuartusInput(world, pos) {
            @Override
            public boolean getInGameValue() {
                BlockState bs = world.getBlockState(pos);
                return bs.get(Properties.POWERED);
            }
        };
    }
}
