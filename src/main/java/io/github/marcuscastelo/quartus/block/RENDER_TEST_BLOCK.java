package io.github.marcuscastelo.quartus.block;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlimeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.GlowstoneBlobFeature;

public class RENDER_TEST_BLOCK extends Block {
    public RENDER_TEST_BLOCK() {
        super(Settings.copy(Blocks.BLACK_WOOL));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        int size = 10;
        net.minecraft.util.math.Direction direction = player.getHorizontalFacing();

        BlockPos oPos = pos.offset(direction,10);
        world.setBlockState(oPos, Blocks.DIRT.getDefaultState());
        for (int s = 1; s <= size/2; s++) {
            world.setBlockState(pos.offset(direction.rotateYClockwise(), s), Blocks.DIRT.getDefaultState());
            world.setBlockState(pos.offset(direction.rotateYCounterclockwise(), s), Blocks.DIRT.getDefaultState());
            world.setBlockState(oPos.offset(direction.rotateYClockwise(), s), Blocks.DIRT.getDefaultState());
            world.setBlockState(oPos.offset(direction.rotateYCounterclockwise(), s), Blocks.DIRT.getDefaultState());
        }

        BlockPos l, r;
        l = pos.offset(direction.rotateYCounterclockwise(), size/2);
        r = pos.offset(direction.rotateYClockwise(), size/2);
        for (int d = 1; d < size; d++) {
            world.setBlockState(l.offset(direction, d), Blocks.DIRT.getDefaultState());
            world.setBlockState(r.offset(direction, d), Blocks.DIRT.getDefaultState());
        }

        return ActionResult.SUCCESS;
    }
}
