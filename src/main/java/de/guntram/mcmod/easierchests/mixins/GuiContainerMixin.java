package de.guntram.mcmod.easierchests.mixins;

import com.mojang.authlib.GameProfile;
import de.guntram.mcmod.easierchests.ExtendedGuiChest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public class GuiContainerMixin extends AbstractClientPlayer {
    
    public GuiContainerMixin(World p_i45074_1_, GameProfile p_i45074_2_) {
        super(p_i45074_1_, p_i45074_2_);
    }
    @Shadow protected Minecraft mc;
    
    @Inject(method="displayGUIChest", at=@At("HEAD"), cancellable = true) 
    public void displayExtendedChestGUI(IInventory lowerChestInventory, CallbackInfo ci) {
        if (!(lowerChestInventory instanceof IInteractionObject))
            return;
        String type=((IInteractionObject)lowerChestInventory).getGuiID();
        if ("minecraft:chest".equals(type)) {
            this.mc.displayGuiScreen(new ExtendedGuiChest(this.inventory, lowerChestInventory, "generic_54", 126));
            ci.cancel();
        }
        else if ("minecraft:shulker_box".equals(type)) {
            this.mc.displayGuiScreen(new ExtendedGuiChest(this.inventory, lowerChestInventory, "shulker_box", 70));
            ci.cancel();
        }
    }
}
