package io.github.marcuscastelo.quartus.block;

import io.github.marcuscastelo.quartus.registry.QuartusItems;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ExecutorIOBlock extends HorizontalFacingBlock {
    public enum ExecutorIOState implements StringIdentifiable {
        VOID("void"), VOID_END("void_end"), INPUT("input"), OUPUT("output"), IO("io");

        String identifier;
        ExecutorIOState(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String asString() {
            return identifier;
        }
    }

    public static final EnumProperty<ExecutorIOState> EXTENSOR_STATE = EnumProperty.of("extensor_state", ExecutorIOState.class);
    public static final BooleanProperty POWERED = Properties.POWERED;

    public ExecutorIOBlock() {
        super(Settings.of(Material.PART).nonOpaque());
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(EXTENSOR_STATE, ExecutorIOState.VOID).with(POWERED, false));
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        Direction outputDir = state.get(FACING);
        if (facing != outputDir) return 0;
        return state.get(POWERED)? 15: 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        Direction outputDir = state.get(FACING);
        if (facing != outputDir) return 0;
        return state.get(POWERED)? 15: 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, EXTENSOR_STATE, POWERED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction futureFacing = ctx.getPlayerFacing().getOpposite();
        Direction left;
        left = futureFacing.rotateYClockwise();

        World world = ctx.getWorld();

        BlockState leftBlockState = world.getBlockState(ctx.getBlockPos().offset(left));

        if (!(leftBlockState.getBlock() instanceof HorizontalFacingBlock) || leftBlockState.get(FACING) != futureFacing || (!leftBlockState.getBlock().equals(this)  && !(leftBlockState.getBlock() instanceof ExecutorBlock))) return Blocks.AIR.getDefaultState();
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite()).with(EXTENSOR_STATE, ExecutorIOState.VOID_END);
    }

    //Define o comportamento de mudança de estado e propagação de informação pela corrente de ExecutorIO
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block previousBlock, BlockPos neighborPos, boolean moved) {
        //Obtém informações do blockstate atual
        ExecutorIOState executorIOState = state.get(EXTENSOR_STATE);
        Direction facingDirection = world.getBlockState(pos).get(FACING);

        //Obtém o blockstate à direita do bloco atual
        BlockPos rightBlockPos = pos.offset(facingDirection.rotateYClockwise());
        BlockState rightBlockState = world.getBlockState(rightBlockPos);

        //Obtém o blockstate à esquerda do bloco atual
        BlockPos leftBlockPos = pos.offset(facingDirection.rotateYCounterclockwise());
        BlockState leftBlockState = world.getBlockState(leftBlockPos);

        //Se o bloco a direita não for outra porta IO ou o próprio executor, quebra o bloco atual
        if (!rightBlockState.getBlock().equals(this) && !(rightBlockState.getBlock() instanceof ExecutorBlock)) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            dropStack(world, pos, new ItemStack(QuartusItems.EXTENSOR_IO, 1));
            return;
        }

        //Se um bloco do tipo ExecutorIO for quebrado à esquerda, propague a informação na cadeia até o executor (de modo que ele possa reagir quanto à adição ou remoção de I/O)
        if (previousBlock.equals(Blocks.END_PORTAL) ||
            (       previousBlock.equals(this) &&
                    neighborPos.equals(leftBlockPos) &&
                    (executorIOState == ExecutorIOState.INPUT || executorIOState == ExecutorIOState.OUPUT || executorIOState == ExecutorIOState.IO)
            )) {
            world.updateNeighbor(rightBlockPos, Blocks.END_PORTAL, pos);
        }

        //Se o estado atual for algum tipo de void, mantenha o void_end apenas no fim da cadeia de ExecutorIO (apenas estética)
        if (executorIOState == ExecutorIOState.VOID_END || executorIOState == ExecutorIOState.VOID) {
            //Se o bloco à esquerda for outro
            if (leftBlockState.getBlock().equals(this) && leftBlockState.get(FACING) == facingDirection) {
                world.setBlockState(pos, state.with(EXTENSOR_STATE, ExecutorIOState.VOID));
            } else {
                world.setBlockState(pos, state.with(EXTENSOR_STATE, ExecutorIOState.VOID_END));
            }
            return;
        }
    }
}