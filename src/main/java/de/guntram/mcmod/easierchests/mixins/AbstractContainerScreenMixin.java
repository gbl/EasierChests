package de.guntram.mcmod.easierchests.mixins;

import de.guntram.mcmod.easierchests.EasierChests;
import de.guntram.mcmod.easierchests.ExtendedGuiChest;
import de.guntram.mcmod.easierchests.interfaces.SlotClicker;
import net.minecraft.client.gui.screen.Screen;
import static net.minecraft.client.gui.screen.Screen.hasAltDown;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.container.Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.ShulkerBoxContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen implements SlotClicker {

    @Shadow protected void onMouseClick(Slot slot, int invSlot, int button, SlotActionType slotActionType) {}
    @Shadow @Final protected Container container;
    @Shadow protected int left, top, containerWidth, containerHeight;
    @Shadow @Final protected PlayerInventory playerInventory;

    protected AbstractContainerScreenMixin() { super(null); }

    @Override
    public void EasierChests$onMouseClick(Slot slot, int invSlot, int button, SlotActionType slotActionType) {
        this.onMouseClick(slot, invSlot, button, slotActionType);
    }
    
    @Override
    public int EasierChests$getPlayerInventoryStartIndex() {
        if (container instanceof PlayerContainer) {
            return 9;
        } else {
            return this.container.slotList.size()-36;
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
            this.font.draw(Integer.toString(slot.id), slot.xPosition, slot.yPosition, 0x808090);
        }
    }
    
    @Inject(method="render", at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/platform/GlStateManager;disableRescaleNormal()V"))
    public void EasierChests$renderSpecialButtons(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Screen me = this;       // work around Java compiler ...
        AbstractContainerScreen acScreen = (AbstractContainerScreen) me;
        ExtendedGuiChest.drawPlayerInventoryBroom(acScreen, left+containerWidth, top+containerHeight-30-3*18, mouseX, mouseY);
        if (container instanceof GenericContainer || container instanceof ShulkerBoxContainer) {
            ExtendedGuiChest.drawPlayerInventoryAllUp(acScreen, left+containerWidth, top+containerHeight-30-2*18, mouseX, mouseY);
        }
    }
    
    @Inject(method="mouseClicked", at=@At("HEAD"), cancellable=true)
    public void EasierChests$checkMyButtons(double mouseX, double mouseY, int button, CallbackInfoReturnable cir) {
        if (mouseX >= left+containerWidth && mouseX <= left+containerWidth+18) {
            Screen me = this;       // work around Java compiler ...
            AbstractContainerScreen acScreen = (AbstractContainerScreen) me;
            if (mouseY >= top+containerHeight-30-3*18 && mouseY < top+containerHeight-30-2*18) {
                ExtendedGuiChest.sortInventory(this, false, this.playerInventory);
                cir.setReturnValue(true);
            } else if (mouseY >= top+containerHeight-30-3*18 && mouseY < top+containerHeight-30-1*18
                    && ( container instanceof GenericContainer || container instanceof ShulkerBoxContainer)) {
                ExtendedGuiChest.moveMatchingItems(acScreen, false);
                cir.setReturnValue(true);
            }
        }
    }
    
    @Inject(method="keyPressed", at=@At("HEAD"), cancellable=true)
    public void EasierChests$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable cir) {
        Screen me = this;       // work around Java compiler ...
        AbstractContainerScreen acScreen = (AbstractContainerScreen) me;
        if (EasierChests.keySortPlInv.matchesKey(keyCode, scanCode)) {
            ExtendedGuiChest.sortInventory(this, false, this.playerInventory);
            cir.setReturnValue(true);
        }
        if (EasierChests.keyMoveToChest.matchesKey(keyCode, scanCode)
                && ( container instanceof GenericContainer || container instanceof ShulkerBoxContainer)) {
                    ExtendedGuiChest.moveMatchingItems(acScreen, false);
                    cir.setReturnValue(true);
        }
    }
}
