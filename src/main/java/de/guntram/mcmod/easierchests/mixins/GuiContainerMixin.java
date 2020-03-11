package de.guntram.mcmod.easierchests.mixins;

import de.guntram.mcmod.easierchests.ExtendedGuiChest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screens;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screens.class)
public class GuiContainerMixin {
    
    private static final Logger LOGGER = LogManager.getLogger();

    @Inject(method="open", at=@At("HEAD"), cancellable = true)
    private static void checkChestScreen(ScreenHandlerType type, MinecraftClient client, 
            int any, Text component, CallbackInfo ci) {
        LOGGER.debug("Trying to open container: "+type+" with name "+component.asFormattedString());
        if (type == ScreenHandlerType.GENERIC_9X1 
        ||  type == ScreenHandlerType.GENERIC_9X2
        ||  type == ScreenHandlerType.GENERIC_9X3
        ||  type == ScreenHandlerType.GENERIC_9X4
        ||  type == ScreenHandlerType.GENERIC_9X5
        ||  type == ScreenHandlerType.GENERIC_9X6) {
            GenericContainerScreenHandler container = (GenericContainerScreenHandler) type.create(any, client.player.inventory);
            ExtendedGuiChest screen = new ExtendedGuiChest(container, 
                    client.player.inventory, component,
                    container.getRows());
            client.player.currentScreenHandler = container;
            client.openScreen(screen);
            LOGGER.debug("(my chest)");
            ci.cancel();
        } else if (type == ScreenHandlerType.SHULKER_BOX) {
            ShulkerBoxScreenHandler container = ScreenHandlerType.SHULKER_BOX.create(any, client.player.inventory);
            ExtendedGuiChest screen = new ExtendedGuiChest(container,
                    client.player.inventory, component);
            client.player.currentScreenHandler = container;
            client.openScreen(screen);
            LOGGER.debug("(my shulker)");
            ci.cancel();
        } else {
            LOGGER.debug("(not me)");
        }
    }
}
