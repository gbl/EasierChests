package de.guntram.mcmod.easierchests.mixins;

import de.guntram.mcmod.easierchests.EasierChests;
import de.guntram.mcmod.easierchests.ExtendedGuiChest;
import de.guntram.mcmod.easierchests.interfaces.SlotClicker;
import net.minecraft.client.gui.screen.Screen;
import static net.minecraft.client.gui.screen.Screen.hasAltDown;
import net.minecraft.client.gui.screen.ingame.ScreenWithHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenWithHandler.class)
public abstract class AbstractContainerScreenMixin extends Screen implements SlotClicker {

    @Shadow protected void onMouseClick(Slot slot, int invSlot, int button, SlotActionType slotActionType) {}
    @Shadow @Final protected ScreenHandler handler;
    @Shadow protected int x, y, backgroundWidth, backgroundHeight;
    @Shadow @Final protected PlayerInventory playerInventory;

    protected AbstractContainerScreenMixin() { super(null); }

    @Override
    public void EasierChests$onMouseClick(Slot slot, int invSlot, int button, SlotActionType slotActionType) {
        this.onMouseClick(slot, invSlot, button, slotActionType);
    }
    
    @Override
    public int EasierChests$getPlayerInventoryStartIndex() {
        if (handler instanceof PlayerScreenHandler) {
            return 9;
        } else {
            return this.handler.slots.size()-36;
        }
    }
    
    @Override
    public int EasierChests$playerInventoryIndexFromSlotIndex(int slot) {
        int firstSlot = EasierChests$getPlayerInventoryStartIndex();
        if (slot < firstSlot) {
            return -1;
        } else if (slot < firstSlot + 27) {
            return slot - firstSlot + 9;
        } else {
            return slot - firstSlot - 27;
        }
    }
    
    @Override
    public int EasierChests$slotIndexfromPlayerInventoryIndex(int slot) {
        int firstSlot = EasierChests$getPlayerInventoryStartIndex();
        if (slot < 9) {
            return slot + firstSlot + 27;
        } else {
            return slot + firstSlot - 9;
        }
    }
    
    @Inject(method="drawSlot", at=@At("RETURN"))
    public void EasierChests$DrawSlotIndex(Slot slot, CallbackInfo ci) {
        if (hasAltDown()) {
            this.textRenderer.draw(Integer.toString(slot.id), slot.xPosition, slot.yPosition, 0x808090);
        }
    }
    
    @Inject(method="render", at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/systems/RenderSystem;disableRescaleNormal()V"))
    public void EasierChests$renderSpecialButtons(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Screen me = this;       // work around Java compiler ...
        ScreenWithHandler acScreen = (ScreenWithHandler) me;
        ExtendedGuiChest.drawPlayerInventoryBroom(acScreen, x+backgroundWidth, y+backgroundHeight-30-3*18, mouseX, mouseY);
        if (handler instanceof GenericContainerScreenHandler || handler instanceof ShulkerBoxScreenHandler) {
            ExtendedGuiChest.drawPlayerInventoryAllUp(acScreen, x+backgroundWidth, y+backgroundHeight-30-2*18, mouseX, mouseY);
        }
    }
    
    @Inject(method="mouseClicked", at=@At("HEAD"), cancellable=true)
    public void EasierChests$checkMyButtons(double mouseX, double mouseY, int button, CallbackInfoReturnable cir) {
        if (mouseX >= x+backgroundWidth && mouseX <= x+backgroundWidth+18) {
            Screen me = this;       // work around Java compiler ...
            ScreenWithHandler acScreen = (ScreenWithHandler) me;
            if (mouseY >= y+backgroundHeight-30-3*18 && mouseY < y+backgroundHeight-30-2*18) {
                ExtendedGuiChest.sortInventory(this, false, this.playerInventory);
                cir.setReturnValue(true);
            } else if (mouseY >= y+backgroundHeight-30-3*18 && mouseY < y+backgroundHeight-30-1*18
                    && ( handler instanceof GenericContainerScreenHandler || handler instanceof ShulkerBoxScreenHandler)) {
                ExtendedGuiChest.moveMatchingItems(acScreen, false);
                cir.setReturnValue(true);
            }
        }
    }
    
    @Inject(method="keyPressed", at=@At("HEAD"), cancellable=true)
    public void EasierChests$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable cir) {
        Screen me = this;       // work around Java compiler ...
        ScreenWithHandler acScreen = (ScreenWithHandler) me;
        if (EasierChests.keySortPlInv.matchesKey(keyCode, scanCode)) {
            ExtendedGuiChest.sortInventory(this, false, this.playerInventory);
            cir.setReturnValue(true);
        }
        if (EasierChests.keyMoveToChest.matchesKey(keyCode, scanCode)
                && ( handler instanceof GenericContainerScreenHandler || handler instanceof ShulkerBoxScreenHandler)) {
                    ExtendedGuiChest.moveMatchingItems(acScreen, false);
                    cir.setReturnValue(true);
        }
    }
}