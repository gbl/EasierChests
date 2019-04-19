package de.guntram.mcmod.easierchests;

import net.minecraft.client.gui.container.ContainerScreen54;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.container.Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

/*
 * Warning - this code should extend ContainerScreen54 AND ShulkerBoxScreen,
 * which it can't. So we extend the superclass, and implement the few methods
 * that are in those classes (and are identical ...) ourselves. Doh.
 */

public class ExtendedGuiChest extends ContainerScreen
{
    private final int inventoryRows;
    private static final Identifier ICONS=new Identifier(EasierChests.MODID, "textures/icons.png");
    private final Identifier background;
    private final Inventory containerInventory;

    public ExtendedGuiChest(Container container, PlayerInventory lowerInv, TextComponent title,
            String backgroundPNG, int rows)
    {
        super(container, lowerInv, title);
        // ToDo: make container a Container again; can only
        // use getInventory() on GenericContainer though. Need to
        // find out how to access the inventory in the shulker box case.
        containerInventory = ((GenericContainer) container).getInventory();
        this.inventoryRows=rows;
        containerHeight = 114 + rows * 18;
        background = new Identifier("minecraft", "textures/gui/container/"+backgroundPNG+".png");
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY)
    {
        this.font.draw(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
        this.font.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
    }

    /*
     * Draws the background layer of this container (behind the items).
     */
    @Override
    protected void drawBackground(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(background);
        int int_3 = (this.width - this.containerWidth) / 2;
        int int_4 = (this.height - this.containerHeight) / 2;
        this.blit(int_3, int_4, 0, 0, this.containerWidth, this.inventoryRows * 18 + 17);
        this.blit(int_3, int_4 + this.inventoryRows * 18 + 17, 0, 126, this.containerWidth, 96);
        
        
        int x = (this.width - this.containerWidth ) / 2;
        int y = (this.height - this.containerHeight ) / 2;
        GlStateManager.enableBlend();
        // GlStateManager.blendFunc(1, 2);
        GlStateManager.pushMatrix();
        if (ConfigurationHandler.toneDownButtons())
            GlStateManager.color4f(1.0f, 1.0F, 1.0F, 0.3F);
        if (ConfigurationHandler.halfSizeButtons())
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
        this.minecraft.getTextureManager().bindTexture(ICONS);

        for (int i=0; i<9; i++) {
            this.drawTexturedModalRectWithMouseHighlight(x+7+i*18,    y+-18,                          1*18, 2*18, 18, 18, mouseX, mouseY);       // arrow down above chests
            this.drawTexturedModalRectWithMouseHighlight(x+7+i*18,    y+40+(this.inventoryRows+4)*18, 9*18, 2*18, 18, 18, mouseX, mouseY);       // arrow up below player inv
        }
        int rowsToDrawDownArrow=inventoryRows;
        if (inventoryRows>6 && !ConfigurationHandler.allowExtraLargeChests())
            rowsToDrawDownArrow=6;
        for (int i=0; i<rowsToDrawDownArrow; i++) {
            this.drawTexturedModalRectWithMouseHighlight(x+ -18,      y+17+i*18,                      1*18, 2*18, 18, 18, mouseX, mouseY);       // arrow down left of chest
        }
        for (int i=0; i<4; i++) {
            this.drawTexturedModalRectWithMouseHighlight(x+ -18,      y+28+(i+this.inventoryRows)*18, 9*18, 2*18, 18, 18, mouseX, mouseY);       // arrow up left of player inv
        }
        
        this.drawTexturedModalRectWithMouseHighlight(x+this.containerWidth,    y+17,                           11*18, 0*18, 18, 18, mouseX, mouseY);       // broom chest
        this.drawTexturedModalRectWithMouseHighlight(x+this.containerWidth,    y+28+(this.inventoryRows)*18,   11*18, 0*18, 18, 18, mouseX, mouseY);       // broom inventory
        this.drawTexturedModalRectWithMouseHighlight(x+this.containerWidth+18, y+17,                           0 *18, 2*18, 18, 18, mouseX, mouseY);       // all down chest
        this.drawTexturedModalRectWithMouseHighlight(x+this.containerWidth+18, y+28+(this.inventoryRows)*18,   8 *18, 2*18, 18, 18, mouseX, mouseY);       // all up inventory
        
        // GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        for (int i=0; i<36; i++) {
            if (!hasShiftDown() && FrozenSlotDatabase.isSlotFrozen(i)) {
                Slot slot = this.container.slotList.get(slotIndexFromPlayerInventoryIndex(i));
                this.blit(x+slot.xPosition, y+slot.yPosition, 7*18+1, 3*18+1, 16, 16);               // stop sign
            }
        }
    }
    
    private void drawTexturedModalRectWithMouseHighlight(int screenx, int screeny, int textx, int texty, int sizex, int sizey, int mousex, int mousey) {
        if (mousex >= screenx && mousex < screenx+sizex && mousey >= screeny && mousey < screeny+sizey) {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.popMatrix();
            blit(screenx, screeny, textx, texty, sizex, sizey);
            GlStateManager.pushMatrix();
            if (ConfigurationHandler.toneDownButtons())
                GlStateManager.color4f(1.0f, 1.0F, 1.0F, 0.3F);
            if (ConfigurationHandler.halfSizeButtons())
                GlStateManager.scalef(0.5f, 0.5f, 0.5f);
        } else {
            if (ConfigurationHandler.halfSizeButtons())
                blit(screenx*2+sizex/2, screeny*2+sizey/2, textx, texty, sizex, sizey);
            else
                blit(screenx, screeny, textx, texty, sizex, sizey);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton==0) {
            checkForMyButtons(mouseX, mouseY);
        }
        
        if (mouseButton==2) {
            checkForToggleFrozen(mouseX, mouseY);
        }
        return true;
    }
        
    void checkForMyButtons(double mouseX, double mouseY) {
        int x = (this.width - this.containerWidth) / 2;
        int y = (this.height - this.containerHeight) / 2;
        
        if (mouseX>=x-18 && mouseX<=x) {                                        // left buttons
            int deltay = (int)mouseY-y;
            if (deltay < this.inventoryRows*18+17)
                clickSlotsInRow((deltay-17)/18);
            else if (deltay < (this.inventoryRows + 4 ) * 18 + 28) {
                clickSlotsInRow((deltay-28)/18);
            }
        } else if (mouseX>x+this.containerWidth) {                                       // right buttons
            boolean isChest;
            if (mouseY>y+17 && mouseY<y+17+18)
                isChest=true;
            else if (mouseY>y+28+(this.inventoryRows)*18 && mouseY<y+28+(this.inventoryRows)*18+18)
                isChest=false;
            else
                return;
            if (mouseX<=x+this.containerWidth+18)
                sortInventory(isChest);
            else if (mouseX<=x+this.containerWidth+36) {
                moveMatchingItems(isChest);
            }
        } else if (mouseX>x+7 && mouseX<x+7+9*18) {                             // top/bottom buttons
            boolean isChest;
            if (mouseY>y-18 && mouseY<y)
                isChest=true;
            else if (mouseY>y+40+(this.inventoryRows+4)*18 && mouseY<y+40+(this.inventoryRows+4)*18+18)
                isChest=false;
            else
                return;
            int column=((int)mouseX-x-7)/18;
            clickSlotsInColumn(column, isChest);
        }
    }
    
    void checkForToggleFrozen(double mouseX, double mouseY) {
        for (int i = 0; i < this.container.slotList.size(); ++i) {
            int invIndex=this.playerInventoryIndexFromSlotIndex(i);
            if (invIndex==-1)
                continue;
            Slot slot = this.container.slotList.get(i);
            if (isPointWithinBounds(slot.xPosition, slot.yPosition, 16, 16, mouseX, mouseY)) {
                FrozenSlotDatabase.setSlotFrozen(invIndex, !FrozenSlotDatabase.isSlotFrozen(invIndex));
            }
        }
    }
    
    private void clickSlotsInRow(int row) {
        //System.out.println("clicking slots in row "+row);
        for (int slot=row*9; slot<=row*9+8; slot++)
            if (hasShiftDown()|| !FrozenSlotDatabase.isSlotFrozen(playerInventoryIndexFromSlotIndex(slot)))
            slotClick(slot, 0, SlotActionType.QUICK_MOVE);
    }

    private void clickSlotsInColumn(int column, boolean isChest) {
        //System.out.println("clicking slots in column "+column+ " of "+(isChest ? "chest" : "player"));
        int first=(isChest ? column : inventoryRows*9+column);
        int count=(isChest ? inventoryRows : 4);
        for (int i=0; i<count; i++) {
            int slot=first+i*9;
            if (hasShiftDown() || !FrozenSlotDatabase.isSlotFrozen(playerInventoryIndexFromSlotIndex(slot)))
                slotClick(slot, 0, SlotActionType.QUICK_MOVE);
        }
    }
    
    private void sortInventory(boolean isChest) {
        
        //System.out.println("sorting "+(isChest ? "chest" : "player"));
        Inventory inv=(isChest ? containerInventory : minecraft.player.inventory);
        
        int size=isChest ? inv.getInvSize() : 36;     // player's Inventory has 41 items which includes armor and left hand, but we don't want these.
        if (size>9*6 && !ConfigurationHandler.allowExtraLargeChests())
            size=9*6;
        for (int toSlot=0; toSlot<size; toSlot++) {
            ItemStack targetStack=inv.getInvStack(toSlot);
            String targetItemName=targetStack.getTranslationKey();
            System.out.println("slot "+toSlot+" has "+targetStack.getItem().getTranslationKey());
            if (targetStack.getItem() == Items.AIR) {
                if (!isChest && toSlot<9)
                    continue;                   // Don't move stuff into empty player hotbar slots
                targetItemName="§§§";           // make sure it is highest so gets sorted last
            }
            if (isChest || toSlot>=9 && (hasShiftDown()|| !FrozenSlotDatabase.isSlotFrozen(toSlot))) {         // Search for a better item, but don't replace hotbar things with different stuff
                for (int fromSlot=toSlot+1; fromSlot<size; fromSlot++) {
                    if (!isChest && !hasShiftDown()&& FrozenSlotDatabase.isSlotFrozen(fromSlot))
                        continue;
                    ItemStack slotStack=inv.getInvStack(fromSlot);
                    if (slotStack.getItem()==Items.AIR)
                        continue;
                    String slotItem=inv.getInvStack(fromSlot).getTranslationKey();
                    if (slotItem.compareToIgnoreCase(targetItemName)<0)
                        targetItemName=slotItem;
                }
            }
            for (int fromSlot=toSlot+1; fromSlot<size; fromSlot++) {
                if (!isChest && hasShiftDown() && FrozenSlotDatabase.isSlotFrozen(fromSlot))
                    continue;
                targetStack=inv.getInvStack(toSlot);
                if (targetStack.getTranslationKey().equals(targetItemName)         // @TODO mit Items arbeiten nicht mit Names
                &&  targetStack.getAmount()== targetStack.getMaxAmount())
                    break;
                ItemStack slotStack=inv.getInvStack(fromSlot);
                if (slotStack.getTranslationKey().equals(targetItemName)) {
                    slotClick (isChest ? fromSlot : slotIndexFromPlayerInventoryIndex(fromSlot), 0, SlotActionType.PICKUP);
                    slotClick (isChest ? toSlot   : slotIndexFromPlayerInventoryIndex(toSlot)  , 0, SlotActionType.PICKUP);
                    slotClick (isChest ? fromSlot : slotIndexFromPlayerInventoryIndex(fromSlot), 0, SlotActionType.PICKUP);                    
                }
            }
        }
    }
    
    private void moveMatchingItems(boolean isChest) {
        // System.out.println("move matching from "+(isChest ? "chest" : "player"));
        Inventory from, to;
        int fromSize, toSize;
        // use 36 for player inventory size so we won't use armor/2h slots
        if (isChest) {
            from = containerInventory;            fromSize=from.getInvSize();
            to   = minecraft.player.inventory;    toSize  =36;
        } else {
            from = minecraft.player.inventory;    fromSize=36;
            to   = containerInventory;            toSize  =to.getInvSize();
        }
        if (!ConfigurationHandler.allowExtraLargeChests()) {
            if (fromSize>9*6)   fromSize=9*6;
            if (toSize  >9*6)   toSize=9*6;
        }
        for (int i=0; i<fromSize; i++) {
            if (!isChest && !hasShiftDown() && FrozenSlotDatabase.isSlotFrozen(i))
                continue;
            ItemStack fromStack = from.getInvStack(i);
            int slot;
            if (isChest)
                slot=i;
            else
                slot=slotIndexFromPlayerInventoryIndex(i);
            for (int j=0; j<toSize; j++) {
                ItemStack toStack = to.getInvStack(j);
                if (fromStack.isEqualIgnoreTags(toStack)
                &&  ItemStack.areTagsEqual(fromStack, toStack)) {
                    // System.out.println("  from["+i+"] is same as to["+j+"] ("+toStack.getDisplayName()+"), clicking "+slot);
                    slotClick(slot, 0, SlotActionType.QUICK_MOVE);
                }
            }
        }
    }
    
    private void slotClick(int slot, int mouseButton, SlotActionType clickType) {
        // System.out.println("Clicking slot "+slot+" "+(mouseButton==0 ? "left" : "right")+" type:"+clickType.toString());
        this.onMouseClick(null, slot, mouseButton, clickType);
        // mc.playerController.windowClick(mc.player.openContainer.windowId, slot, mouseButton, clickType, mc.player);
    }
    
    private int playerInventoryIndexFromSlotIndex(int slot) {
        if (slot < inventoryRows*9)
            return -1;
        else if (slot < (inventoryRows+3)*9)
            return slot - (inventoryRows)*9 + 9;
        else
            return slot - (inventoryRows+3)*9;
    }
    
    private int slotIndexFromPlayerInventoryIndex(int idx) {
        if (idx<9)
            return inventoryRows*9+27+idx;
        else
            return inventoryRows*9-9+idx;
    }
}