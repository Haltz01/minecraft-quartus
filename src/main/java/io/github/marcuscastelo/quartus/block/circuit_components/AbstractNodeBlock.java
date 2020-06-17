package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusNodeConvertible;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public abstract class AbstractNodeBlock extends HorizontalFacingBlock implements QuartusNodeConvertible {
    protected AbstractNodeBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerFacing());
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    //TODO: remover (para forçar que seja criado com subclasse apropriada pelos filhos)
    @Override
    @Nullable
    public QuartusNode createQuartusNode(World world, BlockPos pos) throws QuartusNode.QuartusWrongNodeBlockException {
        final AbstractNodeBlock nodeBlock = this;
        return new QuartusNode(world, pos) {
            @Override
            public List<Direction> getPossibleOutputDirections() {
                return nodeBlock.getPossibleOutputDirections(world, pos);
            }

            @Override
            public List<Direction> getPossibleInputDirections() {
                return nodeBlock.getPossibleInputDirections(world, pos);
            }
        };
    }
}
