package de.guntram.mcmod.easierchests;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/*
 * This code is copied from GuiChest.java, renamed to ExtendedGuiChest,and expanded.
 * It probably needs to be adjusted to new Minecraft versions.
 * Unfortunately, to much of the original is protected/private so I can't just
 * write a wrapper / subclass.
 */

public class ExtendedGuiChest extends GuiContainer
{
    /** The ResourceLocation containing the chest GUI texture. */
    private final ResourceLocation CHEST_GUI_TEXTURE;
    private static final ResourceLocation ICONS=new ResourceLocation(EasierChests.MODID, "textures/icons.png");
    private final IInventory upperChestInventory;
    private final IInventory lowerChestInventory;
    /** window height is calculated with these values; the more rows, the higher */
    private final int inventoryRows;
    private final int playerStartInTexture;

    public ExtendedGuiChest(IInventory upperInv, IInventory lowerInv, String texture, int playerStartInTexture)
    {
        super(new ContainerChest(upperInv, lowerInv, Minecraft.getInstance().player));
        this.upperChestInventory = upperInv;
        this.lowerChestInventory = lowerInv;
        this.allowUserInput = false;
        int i = 222;
        int j = 114;
        this.inventoryRows = lowerInv.getSizeInventory() / 9;
        this.ySize = 114 + this.inventoryRows * 18;
        this.playerStartInTexture=playerStartInTexture;
        CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/"+texture+".png");
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRenderer.drawString(this.lowerChestInventory.getDisplayName().getUnformattedComponentText(), 8, 6, 4210752);
        this.fontRenderer.drawString(this.upperChestInventory.getDisplayName().getUnformattedComponentText(), 8, this.ySize - 96 + 2, 4210752);
    }

    /*
     * Draws the background layer of this container (behind the items).
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.inventoryRows * 18 + 17);                       // upper chest gui
        this.drawTexturedModalRect(x, y + this.inventoryRows * 18 + 17, 0, playerStartInTexture, this.xSize, 96);   // lower chest gui
        
        GlStateManager.enableBlend();
        // GlStateManager.blendFunc(1, 2);
        GlStateManager.pushMatrix();
        if (ConfigurationHandler.toneDownButtons())
            GlStateManager.color4f(1.0f, 1.0F, 1.0F, 0.3F);
        if (ConfigurationHandler.halfSizeButtons())
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);

        this.mc.getTextureManager().bindTexture(ICONS);
        
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
        
        this.drawTexturedModalRectWithMouseHighlight(x+this.xSize,    y+17,                           11*18, 0*18, 18, 18, mouseX, mouseY);       // broom chest
        this.drawTexturedModalRectWithMouseHighlight(x+this.xSize,    y+28+(this.inventoryRows)*18,   11*18, 0*18, 18, 18, mouseX, mouseY);       // broom inventory
        this.drawTexturedModalRectWithMouseHighlight(x+this.xSize+18, y+17,                           0 *18, 2*18, 18, 18, mouseX, mouseY);       // all down chest
        this.drawTexturedModalRectWithMouseHighlight(x+this.xSize+18, y+28+(this.inventoryRows)*18,   8 *18, 2*18, 18, 18, mouseX, mouseY);       // all up inventory
        
        // GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        for (int i=0; i<36; i++) {
            if (!isShiftKeyDown() && FrozenSlotDatabase.isSlotFrozen(i)) {
                Slot slot=this.inventorySlots.inventorySlots.get(slotIndexFromPlayerInventoryIndex(i));
                this.drawTexturedModalRect(x+slot.xPos, y+slot.yPos, 7*18+1, 3*18+1, 16, 16);               // stop sign
            }
        }
    }
    
    private void drawTexturedModalRectWithMouseHighlight(int screenx, int screeny, int textx, int texty, int sizex, int sizey, int mousex, int mousey) {
        if (mousex >= screenx && mousex <= screenx+sizex && mousey >= screeny && mousey <= screeny+sizey) {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.popMatrix();
            drawTexturedModalRect(screenx, screeny, textx, texty, sizex, sizey);
            GlStateManager.pushMatrix();
            if (ConfigurationHandler.toneDownButtons())
                GlStateManager.color4f(1.0f, 1.0F, 1.0F, 0.3F);
            if (ConfigurationHandler.halfSizeButtons())
                GlStateManager.scalef(0.5f, 0.5f, 0.5f);
        } else {
            if (ConfigurationHandler.halfSizeButtons())
                drawTexturedModalRect(screenx*2+sizex/2, screeny*2+sizey/2, textx, texty, sizex, sizey);
            else
                drawTexturedModalRect(screenx, screeny, textx, texty, sizex, sizey);
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
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        
        if (mouseX>=x-18 && mouseX<=x) {                                        // left buttons
            int deltay = (int)mouseY-y;
            if (deltay < this.inventoryRows*18+17)
                clickSlotsInRow((deltay-17)/18);
            else {
                clickSlotsInRow((deltay-28)/18);
            }
        } else if (mouseX>x+this.xSize) {                                       // right buttons
            boolean isChest;
            if (mouseY>y+17 && mouseY<y+17+18)
                isChest=true;
            else if (mouseY>y+28+(this.inventoryRows)*18 && mouseY<y+28+(this.inventoryRows)*18+18)
                isChest=false;
            else
                return;
            if (mouseX<=x+this.xSize+18)
                sortInventory(isChest);
            else if (mouseX<=x+this.xSize+36) {
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
        for (int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i) {
            int invIndex=this.playerInventoryIndexFromSlotIndex(i);
            if (invIndex==-1)
                continue;
            Slot slot = this.inventorySlots.inventorySlots.get(i);
            if (isPointInRegion(slot.xPos, slot.yPos, 16, 16, mouseX, mouseY)) {
                FrozenSlotDatabase.setSlotFrozen(invIndex, !FrozenSlotDatabase.isSlotFrozen(invIndex));
            }
        }
    }
    
    private void clickSlotsInRow(int row) {
        //System.out.println("clicking slots in row "+row);
        for (int slot=row*9; slot<=row*9+8; slot++)
            if (isShiftKeyDown() || !FrozenSlotDatabase.isSlotFrozen(playerInventoryIndexFromSlotIndex(slot)))
            slotClick(slot, 0, ClickType.QUICK_MOVE);
    }

    private void clickSlotsInColumn(int column, boolean isChest) {
        //System.out.println("clicking slots in column "+column+ " of "+(isChest ? "chest" : "player"));
        int first=(isChest ? column : inventoryRows*9+column);
        int count=(isChest ? inventoryRows : 4);
        for (int i=0; i<count; i++) {
            int slot=first+i*9;
            if (isShiftKeyDown() || !FrozenSlotDatabase.isSlotFrozen(playerInventoryIndexFromSlotIndex(slot)))
                slotClick(slot, 0, ClickType.QUICK_MOVE);
        }
    }
    
    private void sortInventory(boolean isChest) {
        //System.out.println("sorting "+(isChest ? "chest" : "player"));
        IInventory inv=(isChest ? lowerChestInventory : upperChestInventory);
        
        int size=isChest ? inv.getSizeInventory() : 36;     // player's Inventory has 41 items which includes armor and left hand, but we don't want these.
        if (size>9*6 && !ConfigurationHandler.allowExtraLargeChests())
            size=9*6;
        for (int toSlot=0; toSlot<size; toSlot++) {
            ItemStack targetStack=inv.getStackInSlot(toSlot);
            String targetItemName=targetStack.getTranslationKey();
            if (targetStack.getItem() == Items.AIR) {
                if (!isChest && toSlot<9)
                    continue;                   // Don't move stuff into empty player hotbar slots
                targetItemName="§§§";           // make sure it is highest so gets sorted last
            }
            if (isChest || toSlot>=9 && (isShiftKeyDown() || !FrozenSlotDatabase.isSlotFrozen(toSlot))) {         // Search for a better item, but don't replace hotbar things with different stuff
                for (int fromSlot=toSlot+1; fromSlot<size; fromSlot++) {
                    if (!isChest && !isShiftKeyDown() && FrozenSlotDatabase.isSlotFrozen(fromSlot))
                        continue;
                    ItemStack slotStack=inv.getStackInSlot(fromSlot);
                    if (slotStack.getItem()==Items.AIR)
                        continue;
                    String slotItem=inv.getStackInSlot(fromSlot).getTranslationKey();
                    if (slotItem.compareToIgnoreCase(targetItemName)<0)
                        targetItemName=slotItem;
                }
            }
            for (int fromSlot=toSlot+1; fromSlot<size; fromSlot++) {
                if (!isChest && isShiftKeyDown() && FrozenSlotDatabase.isSlotFrozen(fromSlot))
                    continue;
                targetStack=inv.getStackInSlot(toSlot);
                if (targetStack.getTranslationKey().equals(targetItemName)         // @TODO mit Items arbeiten nicht mit Names
                &&  targetStack.getCount() == targetStack.getMaxStackSize())
                    break;
                ItemStack slotStack=inv.getStackInSlot(fromSlot);
                if (slotStack.getTranslationKey().equals(targetItemName)) {
                    slotClick (isChest ? fromSlot : slotIndexFromPlayerInventoryIndex(fromSlot), 0, ClickType.PICKUP);
                    slotClick (isChest ? toSlot   : slotIndexFromPlayerInventoryIndex(toSlot)  , 0, ClickType.PICKUP);
                    slotClick (isChest ? fromSlot : slotIndexFromPlayerInventoryIndex(fromSlot), 0, ClickType.PICKUP);                    
                }
            }
        }
    }
    
    private void moveMatchingItems(boolean isChest) {
        // System.out.println("move matching from "+(isChest ? "chest" : "player"));
        IInventory from, to;
        int fromSize, toSize;
        // use 36 for player inventory size so we won't use armor/2h slots
        if (isChest) {
            from=lowerChestInventory; fromSize=from.getSizeInventory();
            to  =upperChestInventory; toSize  =36;
        } else {
            from=upperChestInventory; fromSize=36;
            to  =lowerChestInventory; toSize  =to.getSizeInventory();
        }
        if (!ConfigurationHandler.allowExtraLargeChests()) {
            if (fromSize>9*6)   fromSize=9*6;
            if (toSize  >9*6)   toSize=9*6;
        }
        for (int i=0; i<fromSize; i++) {
            if (!isChest && !isShiftKeyDown() && FrozenSlotDatabase.isSlotFrozen(i))
                continue;
            ItemStack fromStack = from.getStackInSlot(i);
            int slot;
            if (isChest)
                slot=i;
            else
                slot=slotIndexFromPlayerInventoryIndex(i);
            for (int j=0; j<toSize; j++) {
                ItemStack toStack = to.getStackInSlot(j);
                if (fromStack.isItemEqual(toStack)
                &&  ItemStack.areItemStackTagsEqual(fromStack, toStack)) {
                    // System.out.println("  from["+i+"] is same as to["+j+"] ("+toStack.getDisplayName()+"), clicking "+slot);
                    slotClick(slot, 0, ClickType.QUICK_MOVE);
                }
            }
        }
    }
    
    private void slotClick(int slot, int mouseButton, ClickType clickType) {
        // System.out.println("Clicking slot "+slot+" "+(mouseButton==0 ? "left" : "right")+" type:"+clickType.toString());
        mc.playerController.windowClick(mc.player.openContainer.windowId, slot, mouseButton, clickType, mc.player);
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