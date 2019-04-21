package de.guntram.mcmod.easierchests.mixins;

import net.minecraft.container.ShulkerBoxContainer;
import net.minecraft.inventory.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import de.guntram.mcmod.easierchests.InventoryExporter;

@Mixin(ShulkerBoxContainer.class)
public class ShulkerBoxInventoryMixin implements InventoryExporter {

    @Shadow @Final private Inventory inventory;
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
