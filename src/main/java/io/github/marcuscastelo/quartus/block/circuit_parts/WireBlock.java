package io.github.marcuscastelo.quartus.block.circuit_parts;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.block.QuartusInGameComponent;
import io.github.marcuscastelo.quartus.block.QuartusTransportInfoProvider;
import io.github.marcuscastelo.quartus.circuit.CircuitUtils;
import io.github.marcuscastelo.quartus.registry.QuartusBlocks;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WireBlock extends HorizontalFacingBlock implements QuartusTransportInfoProvider {
    public enum UpValues implements StringIdentifiable {
        NONE("none"), FACING("facing"), BOTH("both");

        String identifier;

        UpValues(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String asString() {
            return identifier;
        }
    }

    public static final BooleanProperty TURN, POSITIVE;
    public static final EnumProperty<UpValues> UP;

    private static final List<Direction> HORIZONTAL_DIRECTIONS = Arrays.asList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    public WireBlock() {
        super(Settings.copy(Blocks.REPEATER));
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(TURN, false).with(POSITIVE, false).with(UP, UpValues.NONE));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        this.neighborUpdate(state,world,pos,state.getBlock(),null,false);
    }

    private Direction getAuxDirection(Direction facingDirection, boolean turned, boolean positive) {
        if (!turned) return facingDirection.getOpposite();

        if (positive) return facingDirection.rotateYCounterclockwise();
        else return facingDirection.rotateYClockwise();
    }

    //Supõe-se que a direção informada é uma das direções do fio
    //OBS: não garante que é um wire
    //Retorna nulo se bloqueado por bloco
    @Nullable
    private BlockPos getNextWirePos(World world, BlockPos currPos, Direction directionToGo, boolean isFacingDirection, UpValues upValues) {
        BlockPos nextWirePos = currPos.offset(directionToGo);
        BlockPos currTopPos = currPos.offset(Direction.UP);

        if ((isFacingDirection && upValues == UpValues.FACING) || upValues == UpValues.BOTH) {
            if (world.getBlockState(currTopPos).isSimpleFullBlock(world, currTopPos)) return null;
            nextWirePos = nextWirePos.offset(Direction.UP);
            if (world.getBlockState(nextWirePos).getBlock() != this) return null;
            return nextWirePos;
        }

        BlockState nextWireBlockState = world.getBlockState(nextWirePos);
        if (nextWireBlockState.getBlock() == this) return nextWirePos;
        if (nextWireBlockState.isSimpleFullBlock(world, nextWirePos)) return null;

        nextWirePos = currPos.offset(Direction.DOWN);
        nextWireBlockState = world.getBlockState(nextWirePos);
        if (nextWireBlockState.getBlock() != this) return null;
        return nextWirePos;

    }

    private boolean isWireNotFull(World world, BlockPos pos) {
        BlockState wireBlockState = world.getBlockState(pos);
        if (wireBlockState.getBlock() != this) return false;

        boolean turned = wireBlockState.get(TURN);
        boolean positive = wireBlockState.get(POSITIVE);
        UpValues upValues = wireBlockState.get(UP);

        Direction facingDir = wireBlockState.get(FACING);
        Direction auxDir = getAuxDirection(facingDir, turned, positive);

        BlockPos facingOtherWirePos = getNextWirePos(world, pos, facingDir, true, upValues);
        BlockPos auxOtherWirePos = getNextWirePos(world, pos, auxDir, false, upValues);

        return (facingOtherWirePos == null || auxOtherWirePos == null);
    }

    private void connectWireTo(World world, BlockPos mainWirePos, List<BlockPos> otherWiresPos) {
        if (otherWiresPos.size() == 0) return; //Nada a fazer
        if (otherWiresPos.size() > 2) throw new UnsupportedOperationException("Não é possível conectar um fio a mais de 2");

        BlockState mainWireBs = world.getBlockState(mainWirePos);

        for (BlockPos otherWirePos: otherWiresPos) {
            //TODO: terminar de conectar os fios
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos currPos, Block block, BlockPos neighborPos, boolean moved) {
        List<Direction> HORIZONTAL_DIRECTIONS = Arrays.asList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

        List<BlockPos> connectableWiresPosition = new ArrayList<>();

        for (Direction horizDir: HORIZONTAL_DIRECTIONS) {
            //Sem subir (reto e para baixo)
            BlockPos nextWirePos = getNextWirePos(world, currPos, horizDir, false, UpValues.NONE);
            //Subindo
            if (nextWirePos == null) {
                nextWirePos = getNextWirePos(world, currPos, horizDir, true, UpValues.BOTH);
                //Se não houver fio nessa direção (após as duas tentativas)
                if (nextWirePos == null) continue;
            }

            boolean isNextWireConnectable = isWireNotFull(world, nextWirePos);
            if (isNextWireConnectable) connectableWiresPosition.add(nextWirePos);
            if (connectableWiresPosition.size() >= 2) break;
        }

        connectWireTo(world, currPos, connectableWiresPosition);

    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState bottomBlockstate = world.getBlockState(pos.offset(Direction.DOWN));
        return bottomBlockstate.isSideSolidFullSquare(world,pos,Direction.UP);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        return canPlaceAt(state, world, pos)? state: Blocks.AIR.getDefaultState();
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        return Arrays.asList(new ItemStack(state.getBlock().asItem()));
    }


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        if (state.get(TURN)) {
            //Adds center part
            VoxelShape shape = VoxelShapes.cuboid(6/16f, 0, 6/16f, 10/16f, 2/16f, 10/16f);
            Direction d = state.get(FACING);

            if (d == Direction.NORTH) {
                //North
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(6/16f, 0, 0, 10/16f, 2/16f, 6/16f));
                //West
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 6/16f, 6/16f, 2/16f, 10/16f));
            }
            else if (d == Direction.SOUTH) {
                //South
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(6/16f, 0, 10/16f, 10/16f, 2/16f, 1f));
                //East
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(10/16f, 0, 6/16f, 1f, 2/16f, 10/16f));
            }
            else if (d == Direction.WEST) {
                //West
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 6/16f, 6/16f, 2/16f, 10/16f));
                //South
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(6/16f, 0, 10/16f, 10/16f, 2/16f, 1f));
            } else {
                //East
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(10/16f, 0, 6/16f, 1f, 2/16f, 10/16f));
                //North
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(6/16f, 0, 0, 10/16f, 2/16f, 6/16f));
            }
            return shape;

        }
        else if (state.get(FACING) == Direction.NORTH || state.get(FACING) == Direction.SOUTH)
            return VoxelShapes.cuboid(6/16f, 0.0f, 0f, 10/16f, 2/16f, 1f);
        else
            return VoxelShapes.cuboid(0, 0.0f, 6/16f, 1f, 2/16f, 10/16f);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TURN, POSITIVE, UP, FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
    }

    static {
        TURN = BooleanProperty.of("turn");
        POSITIVE = BooleanProperty.of("positive");
        UP = EnumProperty.of("up", UpValues.class);
    }

    @Override
    public Direction nextDirection(World world, BlockPos pos, Direction facingBefore) {
        if (true)
            throw new UnsupportedOperationException("USE A OUTRA FUNÇÃO AINDA INCOMPLETA");
        BlockState bs = world.getBlockState(pos);
        boolean turned = bs.get(TURN);
        Direction facingNow = bs.get(FACING);

        int fbVal = HORIZONTAL_DIRECTIONS.indexOf(facingBefore);
        int fnVal = HORIZONTAL_DIRECTIONS.indexOf(facingNow);

        if (!turned) {
            if (!facingBefore.equals(facingNow) && !facingBefore.equals(facingNow.getOpposite())) {
                throw new RuntimeException("Fio não está alinhado! (turn=false)");
            }
            return facingBefore;
        } else {
            if (!facingBefore.equals(facingNow.getOpposite()) && fbVal != (fnVal + 1) % 4) {
                throw new RuntimeException("Fio não está alinhado! (turn=true)");
            }
            if (facingBefore.equals(facingNow.getOpposite())) {
                System.out.println("Proximo fio O: " + HORIZONTAL_DIRECTIONS.get((fnVal + 3) % 4).asString());
                return HORIZONTAL_DIRECTIONS.get((fnVal + 3) % 4);
            }
            else
                System.out.println("Proximo fio NO: " + facingNow.asString());
                return facingNow;
        }
    }
}
