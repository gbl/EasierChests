package de.guntram.mcmod.easierchests;

import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class OpenChestEventHandler {
    
    static private OpenChestEventHandler instance;
    private Minecraft mc;
    
    private OpenChestEventHandler() {
        // do not call, use getInstance()
    }
    
    public static OpenChestEventHandler getInstance() {
        if (instance==null) {
            instance=new OpenChestEventHandler();
            instance.mc=Minecraft.getMinecraft();
        }
        return instance;
    }

    @SubscribeEvent
    public void guiOpenEvent(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiChest) {
            GuiChest originalChest=(GuiChest) event.getGui();
            ContainerChest originalContainer=(ContainerChest) originalChest.inventorySlots;
            IInventory lowerChestInventory = originalContainer.getLowerChestInventory();
            InventoryPlayer upperChestInventory = Minecraft.getMinecraft().player.inventory;
            ExtendedGuiChest egc=new ExtendedGuiChest(upperChestInventory, lowerChestInventory, "generic_54", 126);
            event.setGui(egc);
        }
        if (event.getGui() instanceof GuiShulkerBox) {
            GuiShulkerBox originalChest=(GuiShulkerBox) event.getGui();
            ContainerShulkerBox originalContainer=(ContainerShulkerBox) originalChest.inventorySlots;
            Field field = ReflectionHelper.findField(ContainerShulkerBox.class, "inventory", "field_190899_a");
            field.setAccessible(true);
            IInventory lowerChestInventory;
            try {
                lowerChestInventory=(IInventory) field.get(originalContainer);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                System.out.println(ex.getMessage());
                return;
            }
            InventoryPlayer upperChestInventory = Minecraft.getMinecraft().player.inventory;
            ExtendedGuiChest egc=new ExtendedGuiChest(upperChestInventory, lowerChestInventory, "shulker_box", 70);
            event.setGui(egc);
        }
    }
}
