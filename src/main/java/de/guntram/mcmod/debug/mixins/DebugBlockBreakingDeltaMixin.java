/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.debug.mixins;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 *
 * @author gbl
 */

@Mixin(AbstractBlock.class)

public class DebugBlockBreakingDeltaMixin {
    @Inject(method="calcBlockBreakingDelta", at=@At(value="HEAD"))
    public void debugBlockBreakDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable ci)
    {
        float f = state.getHardness(world, pos);
        int i = player.canHarvest(state) ? 30 : 100;
        float result = player.getBlockBreakingSpeed(state) / f / (float)i;
        int ticksToBreak = (int) Math.ceil(1.0f / result);
        
        System.out.println("Player "+player.getEntityName()
                +" is breaking a block, tool effectivity "+ i
                +" block breaking speed "+result
                +" hardness "+f
                +" final result "+player.getBlockBreakingSpeed(state) / f / (float)i
                +" expect break after "+ticksToBreak+" ticks"
        );
    }
}
