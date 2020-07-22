package io.github.marcuscastelo.quartus.block.circuit_parts;

import io.github.marcuscastelo.quartus.block.QuartusTransportInfoProvider;
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

        nextWirePos = nextWirePos.offset(Direction.DOWN);
        nextWireBlockState = world.getBlockState(nextWirePos);

        if (nextWireBlockState.getBlock() != this) {
            System.out.println("O de baixo não é wire, é " + nextWireBlockState.getBlock());
            return null;
        };
        return nextWirePos;

    }

    private boolean isWireNotFull(World world, BlockPos analyzedPos, Direction cameFromDirection) {
        BlockState wireBlockState = world.getBlockState(analyzedPos);
        if (wireBlockState.getBlock() != this) {
            System.out.println("ERROR: Trying to check if non-wire is full or not");
            return false;
        }

        boolean turned = wireBlockState.get(TURN);
        boolean positive = wireBlockState.get(POSITIVE);
        UpValues upValues = wireBlockState.get(UP);

        Direction facingDir = wireBlockState.get(FACING);
        Direction auxDir = getAuxDirection(facingDir, turned, positive);

        if (cameFromDirection != null)
            if (facingDir == cameFromDirection.getOpposite() || auxDir == cameFromDirection.getOpposite()) return true;

        //Wire at facingDir
        BlockPos facingDirWirePos = getNextWirePos(world, analyzedPos, facingDir, true, upValues);
        BlockPos auxDirWirePos = getNextWirePos(world, analyzedPos, auxDir, false, upValues);

        if (facingDirWirePos == null || auxDirWirePos == null) return true;

        BlockState facingDirWireBs = world.getBlockState(facingDirWirePos);
        BlockState auxDirWireBs = world.getBlockState(auxDirWirePos);

        Direction facingDirWireFacingDir = facingDirWireBs.get(FACING);
        Direction auxDirWireFacingDir = auxDirWireBs.get(FACING);

        boolean facingDirWireTurn = facingDirWireBs.get(TURN);
        boolean auxDirWireTurn = auxDirWireBs.get(TURN);

        boolean facingDirWirePositive = facingDirWireBs.get(POSITIVE);
        boolean auxDirWirePositive = auxDirWireBs.get(POSITIVE);

        Direction facingDirWireAuxDir = getAuxDirection(facingDirWireFacingDir, facingDirWireTurn, facingDirWirePositive);
        Direction auxDirWireAuxDir = getAuxDirection(auxDirWireFacingDir, auxDirWireTurn, auxDirWirePositive);

        boolean isFull = (facingDirWireAuxDir == facingDir.getOpposite() || facingDirWireFacingDir == facingDir.getOpposite()) &&
                (auxDirWireFacingDir == auxDir.getOpposite() || auxDirWireAuxDir == auxDir.getOpposite() );
        System.out.println("auxDirFacingDir = " + auxDirWireFacingDir );
        System.out.println("auxDirAuxDir = " + auxDirWireAuxDir );
        System.out.println("auxDir = " + auxDir);

        return !isFull;
    }

    @Nullable
    private Direction getHorizontalDirectionAtoB(BlockPos posA, BlockPos posB) {
        BlockPos difference = posB.subtract(posA);

        if (difference.getX() == 0 && difference.getZ() == 0) return null;

        Direction i = Direction.EAST;
        Direction j = Direction.SOUTH;

        if (difference.getZ() == 0) return (difference.getX()>0)?i:i.getOpposite();
        if (difference.getX() == 0) return (difference.getZ()>0)?j:j.getOpposite();

        return null;
    }

    @Nullable
    private Direction getDirectionAtoB(BlockPos posA, BlockPos posB) {
        BlockPos difference = posB.subtract(posA);
        if (difference.equals(new BlockPos(0,0,0))) return null;

        Direction i = Direction.EAST;
        Direction j = Direction.SOUTH;
        Direction k = Direction.UP;

        if (difference.getX() == 0 && difference.getZ() == 0) return (difference.getY()>0)?k:k.getOpposite();
        if (difference.getY() == 0 && difference.getZ() == 0) return (difference.getX()>0)?i:i.getOpposite();
        if (difference.getX() == 0 && difference.getY() == 0) return (difference.getZ()>0)?j:j.getOpposite();

        return null;
    }

    private boolean isConnectionTurned(Direction facingDirection, @Nullable Direction auxDirection) {
        return auxDirection != null && !facingDirection.getOpposite().equals(auxDirection);
    }

    private boolean isConnectionPositive(Direction facingDirection, @Nullable Direction auxDirection) {
        if (auxDirection == null) return false;
        return facingDirection.rotateYCounterclockwise().equals(auxDirection);
    }

    private boolean wireBlockStateEquals(BlockState bs1, BlockState bs2) {
        return bs1.get(FACING) == bs2.get(FACING) &&
                bs1.get(TURN) == bs2.get(TURN) &&
                bs1.get(POSITIVE) == bs2.get(POSITIVE) &&
                bs1.get(UP) == bs2.get(UP);
    }

    private void connectWireTo(World world, BlockPos mainWirePos, List<BlockPos> otherWiresPos, boolean shouldUpdateNeighors) {
        if (otherWiresPos.size() > 2) throw new UnsupportedOperationException("Não é possível conectar um fio a mais de 2");

        if (otherWiresPos.size() == 0) {
            world.setBlockState(mainWirePos, getDefaultState().with(FACING, world.getBlockState(mainWirePos).get(FACING)));
            return;
        }

        Direction mainWireAuxDirection = null;

        //Propriedades a serem atualizadas no blockstate
        Direction mainWireFacingDir;
        boolean mainWireTurn = false;
        boolean mainWirePositive = false;
        UpValues mainWireUpvalues = UpValues.NONE;

        //0 se nenhum, 1 se sobe só no facing, 2 se sobe só no aux e 3 se sobe nos dois
        int upConnectionInfo = 0;


        //Posição dos outros wires a se conectar
        BlockPos otherWire1Pos, otherWire2Pos = null;

        //OtherWire1 logic
        otherWire1Pos = otherWiresPos.get(0);

        System.out.println("Trying to connect " + mainWirePos.toShortString() + " to " + otherWire1Pos.toShortString());

        mainWireFacingDir = getHorizontalDirectionAtoB(mainWirePos, otherWire1Pos);
        assert mainWireFacingDir != null;

        BlockPos offsetOtherWire1Pos = otherWire1Pos.offset(mainWireFacingDir.getOpposite());

        //Informações para a atualização de vizinhos em altura diferente
        Direction verticalOffset2 = null;
        Direction verticalOffset1 = getDirectionAtoB(mainWirePos, offsetOtherWire1Pos);



        if (otherWiresPos.size() == 2) {
            //OtherWire2 logic
            otherWire2Pos = otherWiresPos.get(1);
            System.out.println("Trying to connect " + mainWirePos.toShortString() + " to " + otherWire2Pos.toShortString());

            mainWireAuxDirection = getHorizontalDirectionAtoB(mainWirePos, otherWire2Pos);
            System.out.println(mainWireAuxDirection);

            assert mainWireAuxDirection != null;
            BlockPos offsetOtherWire2Pos = otherWire2Pos.offset(mainWireAuxDirection.getOpposite());
            verticalOffset2 = getDirectionAtoB(mainWirePos, offsetOtherWire2Pos);


            mainWireTurn = isConnectionTurned(mainWireFacingDir, mainWireAuxDirection);
            mainWirePositive = isConnectionPositive(mainWireFacingDir, mainWireAuxDirection);
            System.out.println(mainWireFacingDir + " " + mainWireAuxDirection + " are pos? " + mainWirePositive );
        }

        if (verticalOffset1 == Direction.UP) upConnectionInfo |= 1;
        if (verticalOffset2 == Direction.UP) upConnectionInfo |= 2;

        if (upConnectionInfo == 1) mainWireUpvalues = UpValues.FACING;
        else if (upConnectionInfo == 2) {
            //Troca o aux pelo facing
            if (mainWireTurn)
                mainWirePositive = !mainWirePositive;
            mainWireFacingDir = mainWireAuxDirection;
            mainWireUpvalues = UpValues.FACING;
        }
        else if (upConnectionInfo == 3) mainWireUpvalues = UpValues.BOTH;

        BlockState oldState = world.getBlockState(mainWirePos);
        BlockState newState = this.getDefaultState().with(FACING, mainWireFacingDir).with(TURN, mainWireTurn).with(POSITIVE, mainWirePositive).with(UP, mainWireUpvalues);

        if (!wireBlockStateEquals(oldState, newState) && shouldUpdateNeighors) {
            if (verticalOffset1 != null) world.updateNeighbor(otherWire1Pos, Blocks.END_PORTAL, mainWirePos);
            if (verticalOffset2 != null) world.updateNeighbor(otherWire2Pos, Blocks.END_PORTAL, mainWirePos);
        }

        world.setBlockState(mainWirePos, newState);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos currPos, Block block, BlockPos neighborPos, boolean moved) {
        if (!isWireNotFull(world, currPos, null)) return;
        System.out.println("Updating " + currPos.toShortString());

        if (world.isClient) return;
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

            boolean isNextWireConnectable = isWireNotFull(world, nextWirePos, horizDir);
            if (isNextWireConnectable) {
                connectableWiresPosition.add(nextWirePos);
            } else {
                System.out.println("Wire at " + nextWirePos.toShortString() + " is full");
            }
            if (connectableWiresPosition.size() >= 2) break;
        }

        connectWireTo(world, currPos, connectableWiresPosition, block != Blocks.END_PORTAL);
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
            if (true) return shape;

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
    public void onBlockRemoved(BlockState oldState, World world, BlockPos currPos, BlockState newState, boolean moved) {
        if (oldState.getBlock() == newState.getBlock()) return;

        Direction facingDirection = oldState.get(FACING);
        boolean turn = oldState.get(TURN);
        boolean positive = oldState.get(POSITIVE);
        UpValues upValues = oldState.get(UP);
        Direction auxDirection = getAuxDirection(facingDirection, turn, positive);

        BlockPos neighborToUpdate1Pos = getNextWirePos(world, currPos, facingDirection, true, upValues);
        BlockPos neighborToUpdate2Pos = getNextWirePos(world, currPos, auxDirection, false, upValues);

        if (neighborToUpdate1Pos != null)
            world.updateNeighbor(neighborToUpdate1Pos, world.getBlockState(neighborToUpdate1Pos).getBlock(), currPos);
        if (neighborToUpdate2Pos != null)
            world.updateNeighbor(neighborToUpdate2Pos, world.getBlockState(neighborToUpdate2Pos).getBlock(), currPos);
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
