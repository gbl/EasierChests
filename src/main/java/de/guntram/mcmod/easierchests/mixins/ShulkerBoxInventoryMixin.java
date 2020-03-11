package de.guntram.mcmod.easierchests.mixins;


import net.minecraft.inventory.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import de.guntram.mcmod.easierchests.InventoryExporter;
import net.minecraft.screen.ShulkerBoxScreenHandler;

@Mixin(ShulkerBoxScreenHandler.class)
public class ShulkerBoxInventoryMixin implements InventoryExporter {

    @Shadow @Final private Inventory inventory;
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
