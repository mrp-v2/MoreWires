package com.morewires.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InfiniwireItem extends AdjustedRedstoneItem{
    public InfiniwireItem(Block blockIn, Settings builder, Item dye)
    {
        super(blockIn, builder, dye);
    }

    @Override
    public boolean hasGlint(ItemStack stack)
    {
        return true;
    }
}
