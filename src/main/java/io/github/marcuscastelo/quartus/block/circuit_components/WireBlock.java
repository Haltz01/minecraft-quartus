package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusTransportInfoProvider;
import io.github.marcuscastelo.quartus.registry.QuartusBlocks;
import net.minecraft.block.*;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
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

public class WireBlock extends HorizontalFacingBlock implements QuartusTransportInfoProvider {
    public static final BooleanProperty TURN;

    private static final List<Direction> HORIZONTAL_DIRECTIONS = Arrays.asList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    public WireBlock() {
        super(Settings.copy(Blocks.REPEATER));
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(TURN, false));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        this.neighborUpdate(state,world,pos,state.getBlock(),null,false);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
        List<Direction> foundDirs = new ArrayList<>();
        for (Direction direction : HORIZONTAL_DIRECTIONS) {
            Block aroundBlock = world.getBlockState(pos.offset(direction)).getBlock();
            if (    aroundBlock == QuartusBlocks.WIRE ||
                    aroundBlock instanceof AbstractNodeBlock &&
                            (((AbstractNodeBlock) aroundBlock).getPossibleInputDirections(world, pos.offset(direction)).contains(direction.getOpposite())
                            || ((AbstractNodeBlock) aroundBlock).getPossibleOutputDirections(world, pos.offset(direction)).contains(direction.getOpposite()))) {
                foundDirs.add(direction);
            }
        }

        boolean turned = state.get(TURN);

        if (foundDirs.size() == 1) {
            world.setBlockState(pos, state.with(FACING, foundDirs.get(0)).with(TURN, false));
            return;
        }

        if (foundDirs.size() == 2 && foundDirs.get(0).getOpposite()==foundDirs.get(1)) {
            Direction newDir = HORIZONTAL_DIRECTIONS.get(Math.min(HORIZONTAL_DIRECTIONS.indexOf(foundDirs.get(0)), HORIZONTAL_DIRECTIONS.indexOf(foundDirs.get(1))));
            world.setBlockState(pos, state.with(FACING, newDir).with(TURN, false));
            return;
        }


        if (foundDirs.size() != 2) {
            if (turned)
                world.setBlockState(pos, state.with(TURN, false));
            return;
        }


        Direction facingDir = state.get(FACING);
        int facingDirVal = HORIZONTAL_DIRECTIONS.indexOf(facingDir);

        int foundDirVal1 = HORIZONTAL_DIRECTIONS.indexOf(foundDirs.get(0));
        int foundDirVal2 = HORIZONTAL_DIRECTIONS.indexOf(foundDirs.get(1));

        int targetDirVal;

        if (foundDirVal1 * foundDirVal2 == 0) { //Special case with north
            targetDirVal = (foundDirVal1 + foundDirVal2 == 1)?1:0;
        } else {
            targetDirVal = Math.max(foundDirVal1, foundDirVal2);
        }

        int rotationsNeeded = (facingDirVal - targetDirVal);

        Quartus.LOGGER.info("Trying to rotate to " + HORIZONTAL_DIRECTIONS.get(targetDirVal));

        Direction newDir;
        switch (rotationsNeeded) {
            case 1:
            case -3:
                newDir = facingDir.rotateYCounterclockwise();
                break;
            case 3:
            case -1:
                newDir = facingDir.rotateYClockwise();
                break;
            case 2:
            case -2:
                newDir = facingDir.rotateYClockwise().rotateYClockwise();
                break;
            case 4:
            case -4:
            case 0:
                newDir = facingDir;
                break;
            default:
                newDir = Direction.NORTH;
        }

        if (!turned)
            world.setBlockState(pos, state.with(TURN, true).with(FACING, newDir));
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
        builder.add(FACING, TURN);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
    }

    static {
        TURN = BooleanProperty.of("turn");
    }

    @Override
    public Direction nextDirection(World world, BlockPos pos, Direction facingBefore) {
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
