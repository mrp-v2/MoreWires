package com.morewires.mixin;

import com.morewires.block.AdjustedRedstoneWireBlock;
import com.morewires.util.Color;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public abstract class MixinBlocks {
    @Redirect(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args = "stringValue=redstone_wire"
                    )
            ),
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/block/RedstoneWireBlock",
                    ordinal = 0
            ),
            require = 0
    )
    private static RedstoneWireBlock RedstoneWireBlock(AbstractBlock.Settings settings){
        return new AdjustedRedstoneWireBlock(settings ,Color.rgbToRgbInt(new Vec3d(255,0,0)));
    }
}
