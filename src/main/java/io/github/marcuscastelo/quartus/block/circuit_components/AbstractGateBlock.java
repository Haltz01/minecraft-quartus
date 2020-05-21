package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusNodeConvertible;
import io.github.marcuscastelo.quartus.circuit_logic.native_programs.AndGateNode;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public abstract class AbstractGateBlock extends HorizontalFacingBlock implements QuartusNodeConvertible {
    public AbstractGateBlock() {
        super(Settings.copy(Blocks.REPEATER));
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return VoxelShapes.cuboid(0f, 0.0f, 0f, 1f, 2/16f, 1f);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerFacing());
    }

    @Override
    @Nullable
    public QuartusNode createQuartusNode(World world, BlockPos pos) throws QuartusNode.QuartusWrongNodeBlockException {
        AbstractGateBlock abstractGateBlock = this;
        return new QuartusNode(world, pos) {
            @Override
            public List<Direction> getPossibleOutputDirections() {
                return abstractGateBlock.getPossibleOutputDirections(world, pos);
            }

            @Override
            public List<Direction> getPossibleInputDirections() {
                return abstractGateBlock.getPossibleInputDirections(world, pos);
            }
        };
    }

}
