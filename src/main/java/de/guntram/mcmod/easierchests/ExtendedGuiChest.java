package de.guntram.mcmod.easierchests;

import java.io.IOException;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/*
 * This code is copied from GuiChest.java, renamed to ExtendedGuiChest,and expanded.
 * It probably needs to be adjusted to new Minecraft versions.
 * Unfortunately, to much of the original is protected/private so I can't just
 * write a wrapper / subclass.
 */

@SideOnly(Side.CLIENT)
public class ExtendedGuiChest extends GuiContainer
{
    /** The ResourceLocation containing the chest GUI texture. */
    private final ResourceLocation CHEST_GUI_TEXTURE;
    private static final ResourceLocation ICONS=new ResourceLocation(EasierChests.MODID, "textures/icons.png");
    private final IInventory upperChestInventory;
    private final IInventory lowerChestInventory;
    /** window height is calculated with these values; the more rows, the heigher */
    private final int inventoryRows;
    private final int playerStartInTexture;

    public ExtendedGuiChest(IInventory upperInv, IInventory lowerInv, String texture, int playerStartInTexture)
    {
        super(new ContainerChest(upperInv, lowerInv, Minecraft.getMinecraft().player));
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
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRenderer.drawString(this.lowerChestInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
        this.fontRenderer.drawString(this.upperChestInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(x, y + this.inventoryRows * 18 + 17, 0, playerStartInTexture, this.xSize, 96);
        

        this.mc.getTextureManager().bindTexture(ICONS);
        
        for (int i=0; i<9; i++) {
            this.drawTexturedModalRect(x+7+i*18,    y+-18,                          1*18, 2*18, 18, 18);       // arrow down
            this.drawTexturedModalRect(x+7+i*18,    y+40+(this.inventoryRows+4)*18, 9*18, 2*18, 18, 18);       // arrow up
        }
        for (int i=0; i<inventoryRows; i++) {
            this.drawTexturedModalRect(x+ -18,      y+17+i*18,                      1*18, 2*18, 18, 18);       // arrow down
        }
        for (int i=0; i<4; i++) {
            this.drawTexturedModalRect(x+ -18,      y+28+(i+this.inventoryRows)*18, 9*18, 2*18, 18, 18);       // arrow up
        }
        
        this.drawTexturedModalRect(x+this.xSize,    y+17,                           11*18, 0*18, 18, 18);       // broom chest
        this.drawTexturedModalRect(x+this.xSize,    y+28+(this.inventoryRows)*18,   11*18, 0*18, 18, 18);       // broom inventory
        this.drawTexturedModalRect(x+this.xSize+18, y+17,                           0 *18, 2*18, 18, 18);       // all down chest
        this.drawTexturedModalRect(x+this.xSize+18, y+28+(this.inventoryRows)*18,   8 *18, 2*18, 18, 18);       // all up inventory
        
        for (int i=0; i<36; i++) {
            if (FrozenSlotDatabase.isSlotFrozen(i)) {
                Slot slot=this.inventorySlots.inventorySlots.get(slotIndexFromPlayerInventoryIndex(i));
                this.drawTexturedModalRect(x+slot.xPos, y+slot.yPos, 7*18+1, 3*18+1, 16, 16);               // stop sign
            }
        }
    }
    
    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton==0) {
            checkForMyButtons(mouseX, mouseY);
        }
        
        if (mouseButton==2) {
            checkForToggleFrozen(mouseX, mouseY);
        }
    }
        
    void checkForMyButtons(final int mouseX, final int mouseY) {
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        
        if (mouseX>=x-18 && mouseX<=x) {                                        // left buttons
            int deltay = mouseY-y;
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
            int column=(mouseX-x-7)/18;
            clickSlotsInColumn(column, isChest);
        }
    }
    
    void checkForToggleFrozen(int mouseX, int mouseY) {
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
        System.out.println("clicking slots in row "+row);
        for (int slot=row*9; slot<=row*9+8; slot++)
            if (!FrozenSlotDatabase.isSlotFrozen(playerInventoryIndexFromSlotIndex(slot)))
            slotClick(slot, 0, ClickType.QUICK_MOVE);
    }

    private void clickSlotsInColumn(int column, boolean isChest) {
        System.out.println("clicking slots in column "+column+ " of "+(isChest ? "chest" : "player"));
        int first=(isChest ? column : inventoryRows*9+column);
        int count=(isChest ? inventoryRows : 4);
        for (int i=0; i<count; i++) {
            int slot=first+i*9;
            if (!FrozenSlotDatabase.isSlotFrozen(playerInventoryIndexFromSlotIndex(slot)))
                slotClick(slot, 0, ClickType.QUICK_MOVE);
        }
    }
    
    private void sortInventory(boolean isChest) {
        System.out.println("sorting "+(isChest ? "chest" : "player"));
        IInventory inv=(isChest ? lowerChestInventory : upperChestInventory);
        
        int size=isChest ? inv.getSizeInventory() : 36;     // player's Inventory has 41 items which includes armor and left hand, but we don't want these.
        for (int toSlot=0; toSlot<size; toSlot++) {
            System.out.println("sorting: looking for item for slot "+toSlot);
            ItemStack targetStack=inv.getStackInSlot(toSlot);
            String targetItemName=inv.getStackInSlot(toSlot).getDisplayName();
            if (targetStack.getItem() == Items.AIR) {
                if (!isChest && toSlot<9)
                    continue;                   // Don't move stuff into empty player hotbar slots
                targetItemName="ZZZ";           // make sure it is highest so gets sorted last
            }
            if (isChest || toSlot>=9 && !FrozenSlotDatabase.isSlotFrozen(toSlot)) {         // Search for a better item, but don't replace hotbar things with different stuff
                for (int fromSlot=toSlot+1; fromSlot<size; fromSlot++) {
                    if (!isChest && FrozenSlotDatabase.isSlotFrozen(fromSlot))
                        continue;
                    ItemStack slotStack=inv.getStackInSlot(fromSlot);
                    if (slotStack.getItem()==Items.AIR)
                        continue;
                    String slotItem=inv.getStackInSlot(fromSlot).getDisplayName();
                    if (slotItem.compareToIgnoreCase(targetItemName)<0)
                        targetItemName=slotItem;
                }
            }
            System.out.println("sorting: decided on item "+targetItemName+" for slot "+toSlot);
            for (int fromSlot=toSlot+1; fromSlot<size; fromSlot++) {
                if (!isChest && FrozenSlotDatabase.isSlotFrozen(fromSlot))
                    continue;
                targetStack=inv.getStackInSlot(toSlot);
                if (targetStack.getDisplayName().equals(targetItemName)         // @TODO mit Items arbeiten nicht mit Names
                &&  targetStack.getCount() == targetStack.getMaxStackSize())
                    break;
                ItemStack slotStack=inv.getStackInSlot(fromSlot);
                System.out.println("sorting: slot "+fromSlot+" has "+slotStack.getDisplayName()+" and we want "+targetItemName);
                if (slotStack.getDisplayName().equals(targetItemName)) {
                    System.out.println("sorting: swapping slot "+fromSlot+" which has "+slotStack.getDisplayName()+" with target");
                    slotClick (isChest ? fromSlot : slotIndexFromPlayerInventoryIndex(fromSlot), 0, ClickType.PICKUP);
                    slotClick (isChest ? toSlot   : slotIndexFromPlayerInventoryIndex(toSlot)  , 0, ClickType.PICKUP);
                    slotClick (isChest ? fromSlot : slotIndexFromPlayerInventoryIndex(fromSlot), 0, ClickType.PICKUP);                    
                }
            }
        }
    }
    
    private void moveMatchingItems(boolean isChest) {
        System.out.println("move matching from "+(isChest ? "chest" : "player"));
        IInventory from, to;
        if (isChest) {
            from=lowerChestInventory;
            to  =upperChestInventory;
        } else {
            from=upperChestInventory;
            to  =lowerChestInventory;
        }
        for (int i=0; i<from.getSizeInventory(); i++) {
            if (!isChest && FrozenSlotDatabase.isSlotFrozen(i))
                continue;
            ItemStack fromStack = from.getStackInSlot(i);
            int slot;
            if (isChest)
                slot=i;
            else
                slot=slotIndexFromPlayerInventoryIndex(i);
            for (int j=0; j<to.getSizeInventory(); j++) {
                ItemStack toStack = to.getStackInSlot(j);
                if (fromStack.isItemEqual(toStack)
                &&  ItemStack.areItemStackTagsEqual(fromStack, toStack)) {
                    System.out.println("  from["+i+"] is same as to["+j+"] ("+toStack.getDisplayName()+"), clicking "+slot);
                    slotClick(slot, 0, ClickType.QUICK_MOVE);
                }
            }
        }
    }
    
    private void slotClick(int slot, int mouseButton, ClickType clickType) {
        System.out.println("Clicking slot "+slot+" "+(mouseButton==0 ? "left" : "right")+" type:"+clickType.toString());
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