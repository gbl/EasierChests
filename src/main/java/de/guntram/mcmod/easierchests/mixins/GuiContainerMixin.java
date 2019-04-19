package de.guntram.mcmod.easierchests.mixins;

import de.guntram.mcmod.easierchests.ExtendedGuiChest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerScreenRegistry;
import net.minecraft.container.ContainerType;
import net.minecraft.container.GenericContainer;
import net.minecraft.container.ShulkerBoxContainer;
import net.minecraft.text.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContainerScreenRegistry.class)
public class GuiContainerMixin {

    @Inject(method="openScreen", at=@At("HEAD"), cancellable = true)
    private static void checkChestScreen(ContainerType type, MinecraftClient client, 
            int any, TextComponent component, CallbackInfo ci) {
        System.out.println("Trying to open container: "+type+" with name "+component.getFormattedText());
        if (type == ContainerType.GENERIC_9X1 
        ||  type == ContainerType.GENERIC_9X2
        ||  type == ContainerType.GENERIC_9X3
        ||  type == ContainerType.GENERIC_9X4
        ||  type == ContainerType.GENERIC_9X5
        ||  type == ContainerType.GENERIC_9X6) {
            GenericContainer container = (GenericContainer) type.create(any, client.player.inventory);
            ExtendedGuiChest screen = new ExtendedGuiChest(container, 
                    client.player.inventory, component,
                    "generic_54", container.getRows());
            client.player.container = container;
            client.openScreen(screen);
            System.out.println("(my chest)");
            ci.cancel();
/* This does not currently work, as I don't really know how to access
 * the private "inventory" member of ShulkerBoxContainer.
        } else if (type == ContainerType.SHULKER_BOX) {
            ShulkerBoxContainer container = ContainerType.SHULKER_BOX.create(any, client.player.inventory);
            ExtendedGuiChest screen = new ExtendedGuiChest(container, 
                    client.player.inventory, component,
                    "shulker_box", 3);
            client.player.container = screen.getContainer();
            client.openScreen(screen);
            System.out.println("(my shulker)");
*/
        } else {
            System.out.println("(not me)");
        }
    }

/*
    @Inject(method="getFactory", at=@At("HEAD"), cancellable = true) 
    public static void patchFactory(ContainerType type, CallbackInfoReturnable cir) {
        if (type == ContainerType.GENERIC_9X1 
        ||  type == ContainerType.GENERIC_9X2
        ||  type == ContainerType.GENERIC_9X3
        ||  type == ContainerType.GENERIC_9X4
        ||  type == ContainerType.GENERIC_9X5
        ||  type == ContainerType.GENERIC_9X6) {
            cir.setReturnValue(ExtendedGuiChest::new);
            cir.cancel();
        }
    }
*/
        
/*        
        String type;
        if (!(lowerChestInventory instanceof IInteractionObject)) {
            type="minecraft:container";
        } else {
            type=((IInteractionObject)lowerChestInventory).getGuiID();
        }
        if ("minecraft:chest".equals(type) || "minecraft:container".equals(type)) {
            this.mc.displayGuiScreen(new ExtendedGuiChest(this.inventory, lowerChestInventory, "generic_54", 126));
            ci.cancel();
        }
        else if ("minecraft:shulker_box".equals(type)) {
            this.mc.displayGuiScreen(new ExtendedGuiChest(this.inventory, lowerChestInventory, "shulker_box", 70));
            ci.cancel();
        }
*/
}
