package io.github.marcuscastelo.quartus.block;

import io.github.marcuscastelo.quartus.Quartus;
import io.github.marcuscastelo.quartus.blockentity.CompilerBlockEntity;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.swing.*;

public class CompilerBlock extends HorizontalFacingBlock implements BlockEntityProvider {
    public CompilerBlock() {
        super(Settings.copy(Blocks.ANVIL));
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new CompilerBlockEntity();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.PASS;

        ContainerProviderRegistry.INSTANCE.openContainer(Quartus.id("compiler"), player, packetByteBuf -> packetByteBuf.writeBlockPos(pos));

        return ActionResult.SUCCESS;
    }
}
