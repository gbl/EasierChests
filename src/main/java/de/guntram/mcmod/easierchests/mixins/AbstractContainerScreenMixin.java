package de.guntram.mcmod.easierchests.mixins;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.guntram.mcmod.easierchests.ConfigurationHandler;
import de.guntram.mcmod.easierchests.EasierChests;
import de.guntram.mcmod.easierchests.ExtendedGuiChest;
import de.guntram.mcmod.easierchests.FrozenSlotDatabase;
import de.guntram.mcmod.easierchests.interfaces.SlotClicker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import static net.minecraft.client.gui.screen.Screen.hasAltDown;
import static net.minecraft.client.gui.screen.Screen.hasShiftDown;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import de.guntram.mcmod.easierchests.storagemodapi.ChestGuiInfo;
import net.minecraft.text.Text;

@Mixin(HandledScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen implements SlotClicker {

    private static final int PLAYERSLOTS = 36;      // # of slots in player inventory -> so not in container
    private static int PLAYERINVCOLS = 9;           // let's not make those final; maybe we'll need compatibility
    private static int PLAYERINVROWS = 4;           // with some mod at some point which changes these.

    private static TextFieldWidget searchWidget;
    
    @Shadow protected void onMouseClick(Slot slot, int invSlot, int button, SlotActionType slotActionType) {}
    @Shadow protected void drawMouseoverTooltip(MatrixStack matrices, int x, int y) {}
    @Shadow protected boolean isPointWithinBounds(int x, int y, int w, int h, double pX, double pY) {return true;}
    @Shadow @Final protected ScreenHandler handler;
    @Shadow protected int x, y, backgroundWidth, backgroundHeight;

    protected AbstractContainerScreenMixin() { super(null); }

    @Override
    public void EasierChests$onMouseClick(Slot slot, int invSlot, int button, SlotActionType slotActionType) {
        this.onMouseClick(slot, invSlot, button, slotActionType);
    }
    
    @Override
    public int EasierChests$getPlayerInventoryStartIndex() {
        if (handler instanceof PlayerScreenHandler) {
            return PLAYERINVCOLS;
        } else {
            return this.handler.slots.size()-PLAYERSLOTS;
        }
    }
    
    @Override
    public int EasierChests$playerInventoryIndexFromSlotIndex(int slot) {
        int firstSlot = EasierChests$getPlayerInventoryStartIndex();
        if (slot < firstSlot) {
            return -1;
        } else if (slot < firstSlot + (PLAYERSLOTS-PLAYERINVCOLS)) {
            return slot - firstSlot + PLAYERINVCOLS;
        } else {
            return slot - firstSlot - (PLAYERSLOTS-PLAYERINVCOLS);
        }
    }
    
    @Override
    public int EasierChests$slotIndexfromPlayerInventoryIndex(int slot) {
        int firstSlot = EasierChests$getPlayerInventoryStartIndex();
        if (slot < PLAYERINVCOLS) {
            return slot + firstSlot + (PLAYERSLOTS-PLAYERINVCOLS);
        } else {
            return slot + firstSlot - PLAYERINVCOLS;
        }
    }
    
    @Inject(method="drawSlot", at=@At("RETURN"))
    public void EasierChests$DrawSlotIndex(MatrixStack stack, Slot slot, CallbackInfo ci) {
        if (hasAltDown()) {
            this.textRenderer.draw(stack, Integer.toString(slot.id), slot.x, slot.y, 0x808090);
        }
    }

    @Inject(method="render", at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/systems/RenderSystem;disableDepthTest()V", remap = false))
    public void EasierChests$renderSpecialButtons(MatrixStack stack, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        HandledScreen hScreen = (HandledScreen) (Object) this;
        
        ExtendedGuiChest.drawPlayerInventoryBroom(stack, hScreen, x+backgroundWidth, y+backgroundHeight-30-3*18, mouseX, mouseY);
        if (isSupportedScreenHandler(handler)) {
            
            int cols = getSlotColumnCount();
            int rows = getSlotRowCount();

            GlStateManager._enableBlend();
            RenderSystem.setShaderTexture(0, ExtendedGuiChest.ICONS);
            
            if (ConfigurationHandler.enableColumnButtons()) {
                int startx = (x + backgroundWidth/2) - (18/2) * cols;
                for (int i=0; i<cols; i++) {
                    ExtendedGuiChest.drawTexturedModalRectWithMouseHighlight(hScreen, stack, startx+i*18, y+-18,            1*18, 2*18, 18, 18, mouseX, mouseY);       // arrow down above chests
                }
                startx = (x + backgroundWidth/2) - 9*PLAYERINVCOLS;
                for (int i=0; i<PLAYERINVCOLS; i++) {
                    ExtendedGuiChest.drawTexturedModalRectWithMouseHighlight(hScreen, stack, startx+i*18, y+40+(rows+4)*18, 9*18, 2*18, 18, 18, mouseX, mouseY);       // arrow up below player inv
                }
            }
            
            if (ConfigurationHandler.enableRowButtons()) {
                for (int i=0; i<rows; i++) {
                    ExtendedGuiChest.drawTexturedModalRectWithMouseHighlight(hScreen, stack, x+ -18,   y+17+i*18,        1*18, 2*18, 18, 18, mouseX, mouseY);       // arrow down left of chest
                }
                for (int i=0; i<PLAYERINVROWS; i++) {
                    ExtendedGuiChest.drawTexturedModalRectWithMouseHighlight(hScreen, stack, x+ -18,   y+28+(i+rows)*18, 9*18, 2*18, 18, 18, mouseX, mouseY);       // arrow up left of player inv
                }
            }
            
            GlStateManager._disableBlend();
            RenderSystem.setShaderTexture(0, ExtendedGuiChest.ICONS);      // because tooltip rendering will have changed the texture to letters
            for (int i=0; i<PLAYERSLOTS; i++) {
                if (!hasShiftDown() && FrozenSlotDatabase.isSlotFrozen(i)) {
                    Slot slot = this.handler.slots.get(EasierChests$slotIndexfromPlayerInventoryIndex(i));
                    this.drawTexture(stack, x+slot.x, y+slot.y, 7*18+1, 3*18+1, 16, 16);               // stop sign
                }
            }
            
            if (ConfigurationHandler.enableSearch()) {
                if (searchWidget == null) {
                    searchWidget = new TextFieldWidget(textRenderer, x+backgroundWidth-85, y+3, 80, 12, Text.literal("Search"));
                } else {
                    searchWidget.x = x+backgroundWidth-85;
                    searchWidget.y = y+3;
                }
                searchWidget.render(stack, mouseX, mouseY, delta);
                
                String search = searchWidget.getText().toLowerCase();
                if (!search.isEmpty()) {
                    int highlight = (int) Long.parseLong(ConfigurationHandler.getHighlightColor().toUpperCase(), 16);
                    for (int i=0; i<this.handler.slots.size(); i++) {
                        Slot slot = this.handler.slots.get(i);
                        Item item = slot.getStack().getItem();
                        if (item == Items.AIR) {
                            continue;
                        }
                        if (I18n.translate(item.getTranslationKey()).toLowerCase().contains(search)) {
                            DrawableHelper.fill(stack, x+slot.x-1, y+slot.y-1, x+slot.x+18-1, y+slot.y+18-1, highlight);
                        }
                    }
                }
                
            }
            ExtendedGuiChest.drawPlayerInventoryAllUp(stack, hScreen, x+backgroundWidth, y+backgroundHeight-30-2*18, mouseX, mouseY);
            ExtendedGuiChest.drawChestInventoryBroom(stack, hScreen, x+backgroundWidth, y+17, mouseX, mouseY);
            ExtendedGuiChest.drawChestInventoryAllDown(stack, hScreen, x+this.backgroundWidth, y+17+18, mouseX, mouseY);
        }
    }
    
    @Inject(method="mouseClicked", at=@At("HEAD"), cancellable=true)
    public void EasierChests$checkMyButtons(double mouseX, double mouseY, int mouseButton, CallbackInfoReturnable cir) {
        
        if (isSupportedScreenHandler(handler)
        && ConfigurationHandler.enableSearch() 
        && searchWidget.mouseClicked(mouseX, mouseY, mouseButton)) {
            cir.setReturnValue(true);
            cir.cancel();
            return;
        }
        
        if (mouseX >= x+backgroundWidth && mouseX <= x+backgroundWidth+18) {
            HandledScreen hScreen = (HandledScreen) (Screen) this;
            if (mouseY >= y+backgroundHeight-30-3*18 && mouseY < y+backgroundHeight-30-2*18) {
                ExtendedGuiChest.sortInventory(this, false, MinecraftClient.getInstance().player.getInventory());
                cir.setReturnValue(true);
                cir.cancel();
                return;
            } 
            else if (!isSupportedScreenHandler(handler)) {
                return;
            } else if (mouseY >= y+backgroundHeight-30-3*18 && mouseY < y+backgroundHeight-30-1*18) {
                ExtendedGuiChest.moveMatchingItems(hScreen, false);
                cir.setReturnValue(true);
                cir.cancel();
                return;
            } else if (mouseY > y+17 && mouseY < y+17+18) {
                ExtendedGuiChest.sortInventory(this, true, handler.getSlot(0).inventory);
                cir.setReturnValue(true);
                cir.cancel();
                return;
            } else if (mouseY > y+17+18 && mouseY < y+17+36) {
                ExtendedGuiChest.moveMatchingItems(hScreen, true);
                cir.setReturnValue(true);
                cir.cancel();
                return;
            }
        }
        if (!isSupportedScreenHandler(handler)) {
            return;
        }
        if (mouseButton == 0 && checkForMyButtons(mouseX, mouseY)) {
            cir.setReturnValue(true);
            cir.cancel();
            return;
        }
        if (mouseButton==2 &&  checkForToggleFrozen(mouseX, mouseY)) {
            cir.setReturnValue(true);
            cir.cancel();
            return;
        }
    }
    
    private boolean checkForMyButtons(double mouseX, double mouseY) {
        int rows = getSlotRowCount();
        int cols = getSlotColumnCount();

        if (ConfigurationHandler.enableRowButtons() && mouseX>=x-18 && mouseX<=x) { // left buttons
            int deltay = (int)mouseY-y;
            if (deltay < rows*18+17) {                                          // chest -> down
                clickSlotsInRow((deltay-17)/18);
                return true;
            }
            else if (deltay < (rows + PLAYERINVROWS ) * 18 + 28) {              // inv -> up
                clickSlotsInRow((deltay-28)/18);
                return true;
            }
        } 
        if (ConfigurationHandler.enableColumnButtons() && mouseX>x+7 && mouseX<x+backgroundWidth) { // top/bottom buttons
            boolean isChest;
            int column;
            if (mouseY>y-18 && mouseY<y) {                                      // top -> down
                int startx = x + backgroundWidth / 2 - (18/2) * cols;
                isChest=true;
                column=((int)mouseX-startx)/18;
                if (column < 0 || column >= cols) {
                    return false;
                }
            } else if (mouseY>y+40+(rows+PLAYERINVROWS)*18 && mouseY<y+40+(rows+PLAYERINVROWS)*18+18) {
                int startx = x + backgroundWidth / 2 - (18/2) * PLAYERINVCOLS;
                isChest=false;
                column=((int)mouseX-startx)/18;
                if (column < 0 || column > PLAYERINVCOLS) {
                    return false;
                }
            } else {
                return false;
            }
            clickSlotsInColumn(column, isChest);
            return true;
        }
        return false;
    }
    
    private boolean checkForToggleFrozen(double mouseX, double mouseY) {
        for (int i = 0; i < this.handler.slots.size(); ++i) {
            int invIndex=this.EasierChests$playerInventoryIndexFromSlotIndex(i);
            if (invIndex==-1)
                continue;
            Slot slot = this.handler.slots.get(i);
            if (isPointWithinBounds(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
                FrozenSlotDatabase.setSlotFrozen(invIndex, !FrozenSlotDatabase.isSlotFrozen(invIndex));
                return true;
            }
        }
        return false;
    }
    
    private void clickSlotsInRow(int row) {
        int rows = getSlotRowCount();
        int cols = getSlotColumnCount();
        int firstSlot;
        
        if (row <= rows) {      // we're in the chest
            firstSlot = row * cols;
        } else {
            firstSlot = rows * cols + (row-rows)*PLAYERINVCOLS;
            cols = PLAYERINVCOLS;
        }

        for (int slot=firstSlot; slot<firstSlot+cols; slot++)
            if (hasShiftDown() || !FrozenSlotDatabase.isSlotFrozen(EasierChests$playerInventoryIndexFromSlotIndex(slot))) {
                slotClick(slot, 0, SlotActionType.QUICK_MOVE);
            }
            
    }

    private void clickSlotsInColumn(int column, boolean isChest) {
        int cols = getSlotColumnCount();
        int rows = getSlotRowCount();
        int first, count;

        if (isChest) {
            first = column;
            count = rows;
        } else {
            first = rows * cols + column;
            count = PLAYERINVROWS;
            cols = PLAYERINVCOLS;
        }
        for (int i=0; i<count; i++) {
            int slot=first+i*cols;
            if (hasShiftDown() || !FrozenSlotDatabase.isSlotFrozen(EasierChests$playerInventoryIndexFromSlotIndex(slot)))
                slotClick(slot, 0, SlotActionType.QUICK_MOVE);
        }
    }

    private void slotClick(int slot, int mouseButton, SlotActionType clickType) {
        ((SlotClicker)this).EasierChests$onMouseClick(null, slot, mouseButton, clickType);
    }

    @Inject(method="keyPressed", at=@At("HEAD"), cancellable=true)
    public void EasierChests$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable cir) {
        HandledScreen hScreen = (HandledScreen)(Screen)this;
        
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            return;
        }
        
        if (isSupportedScreenHandler(handler)
        && ConfigurationHandler.enableSearch()
        && searchWidget.isActive()) {
            boolean value = searchWidget.keyPressed(keyCode, scanCode, modifiers);
            cir.setReturnValue(value);
            cir.cancel();
            return;
        }

        if (EasierChests.keySortPlInv.matchesKey(keyCode, scanCode)) {
            ExtendedGuiChest.sortInventory(this, false, MinecraftClient.getInstance().player.getInventory());
            cir.setReturnValue(true);
            cir.cancel();
        } else if (!isSupportedScreenHandler(handler)) {
            return;
        } else if (EasierChests.keyMoveToChest.matchesKey(keyCode, scanCode)) {
            ExtendedGuiChest.moveMatchingItems(hScreen, false);
            cir.setReturnValue(true);
            cir.cancel();
        } else if (EasierChests.keySortChest.matchesKey(keyCode, scanCode)) {
            ExtendedGuiChest.sortInventory(this, true, handler.getSlot(0).inventory);
            cir.setReturnValue(true);
            cir.cancel();
        } else if (EasierChests.keyMoveToPlInv.matchesKey(keyCode, scanCode)) {
            ExtendedGuiChest.moveMatchingItems(hScreen, true);
            cir.setReturnValue(true);
            cir.cancel();
        } else if (EasierChests.keySearchBox.matchesKey(keyCode, scanCode)) {
            ConfigurationHandler.toggleSearchBox();
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
    
    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (isSupportedScreenHandler(handler)
        && ConfigurationHandler.enableSearch()
        && searchWidget.isActive()) {
            return searchWidget.charTyped(chr, keyCode);
        }
        return super.charTyped(chr, keyCode);
    }
    
    private boolean loggedScreenHandlerClass = false;
    public boolean isSupportedScreenHandler(ScreenHandler handler) {
        if (handler == null) {      // can this happen? Make IDE happy
            return false;
        }

        if (handler instanceof GenericContainerScreenHandler || handler instanceof ShulkerBoxScreenHandler) {
            return true;
        }
        
        if (EasierChests.getHelperForHandler(handler) != null) {
            return true;
        }
        
        if (!loggedScreenHandlerClass && !handler.getClass().getSimpleName().startsWith("class_")) {    // don't log MC internal classes
            LogManager.getLogger(this.getClass()).info("opening class "+handler.getClass().getSimpleName() + "/" + handler.getClass().getCanonicalName());
            loggedScreenHandlerClass = true;
        }
        return false;
    }
    
    /**
     * Gets the number of inventory rows in the Chest inventory. 
     * This does not include the PLAYERINVROWS rows in the player inventory.
     * @return the number of inventory rows
     */
    
    public int getSlotRowCount() {
        int size = handler.slots.size() - PLAYERSLOTS;
        if (ConfigurationHandler.allowExtraLargeChests()) {
            ChestGuiInfo helper = EasierChests.getHelperForHandler(handler);
            if (helper != null) {
                int cols = helper.getRows(handler);
                if (cols != -1) {
                    return cols;
                }
            }
            return size / getSlotColumnCount();
        }
        return Math.min(6, size/PLAYERINVCOLS);
    }
    
    public int getSlotColumnCount() {
        int size = handler.slots.size() - PLAYERSLOTS;
        if (ConfigurationHandler.allowExtraLargeChests()) {
            ChestGuiInfo helper = EasierChests.getHelperForHandler(handler);
            if (helper != null) {
                int rows = helper.getColumns(handler);
                if (rows != -1) {
                    return rows;
                }
            }
            return (size <= 81 ? PLAYERINVCOLS : size/PLAYERINVCOLS);
        }
        return PLAYERINVCOLS;
    }
}
