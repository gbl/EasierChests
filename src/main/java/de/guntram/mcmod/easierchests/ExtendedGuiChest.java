package de.guntram.mcmod.easierchests;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.guntram.mcmod.easierchests.interfaces.SlotClicker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/*
 * Warning - this code should extend ContainerScreen54 AND ShulkerBoxScreen,
 * which it can't. So we extend the superclass, and implement the few methods
 * that are in those classes (and are identical ...) ourselves. Doh.
 */

public class ExtendedGuiChest extends HandledScreen
{
    private final int inventoryRows;
    private static final Identifier ICONS=new Identifier(EasierChests.MODID, "textures/icons.png");
    private final Identifier background;
    private final Inventory containerInventory;
    private final boolean separateBlits;
    private TextFieldWidget searchWidget;
    private static String searchText;
    
    // temp kludge -- field_2776 and field_2800 seem to have been renamed with 21w13a
    private int x, y;
    
    public ExtendedGuiChest(GenericContainerScreenHandler container, PlayerInventory lowerInv, Text title,
            int rows)
    {
        super(container, lowerInv, title);
        containerInventory = container.getInventory();
        this.inventoryRows=rows;
        backgroundHeight = 114 + rows * 18;
        background = new Identifier("minecraft", "textures/gui/container/generic_54.png");
        separateBlits=true;
    }
    
    public ExtendedGuiChest(ShulkerBoxScreenHandler container, PlayerInventory lowerInv, Text title) {
        super(container, lowerInv, title);
        containerInventory = ((InventoryExporter)container).getInventory();
        inventoryRows = 3;
        background = new Identifier("minecraft", "textures/gui/container/shulker_box.png");
        separateBlits=false;
    }
    
    @Override
    public void init() {
        super.init();
        x = field_2776;
        y = field_2800;

        searchWidget = new TextFieldWidget(textRenderer, x+80, y+3, 80, 12, new LiteralText("Search"));
        searchWidget.setText(searchText);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        if (ConfigurationHandler.enableSearch()) {
            searchWidget.render(stack, mouseX, mouseY, 0);
        }
        drawMouseoverTooltip(stack, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(MatrixStack stack, int mouseX, int mouseY)
    {
        this.textRenderer.draw(stack, this.title.getString(), 8.0F, 6.0F, 4210752);
        this.textRenderer.draw(stack, this.displayName, 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
    }

    /*
     * Draws the background layer of this container (behind the items).
     */
    @Override
    protected void drawBackground(MatrixStack stack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, background);
        if (separateBlits) {
            this.drawTexture(stack, x, y, 0, 0, this.backgroundWidth, this.inventoryRows * 18 + 17);
            this.drawTexture(stack, x, y + this.inventoryRows * 18 + 17, 0, 126, this.backgroundWidth, 96);
        } else {
            this.drawTexture(stack, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        }

        GlStateManager._enableBlend();
        RenderSystem.setShaderTexture(0, ICONS);

        for (int i=0; i<9; i++) {
            this.drawTexturedModalRectWithMouseHighlight(stack, x+7+i*18,    y+-18,                          1*18, 2*18, 18, 18, mouseX, mouseY);       // arrow down above chests
            this.drawTexturedModalRectWithMouseHighlight(stack, x+7+i*18,    y+40+(this.inventoryRows+4)*18, 9*18, 2*18, 18, 18, mouseX, mouseY);       // arrow up below player inv
        }
        int rowsToDrawDownArrow=inventoryRows;
        if (inventoryRows>6 && !ConfigurationHandler.allowExtraLargeChests())
            rowsToDrawDownArrow=6;
        for (int i=0; i<rowsToDrawDownArrow; i++) {
            this.drawTexturedModalRectWithMouseHighlight(stack, x+ -18,      y+17+i*18,                      1*18, 2*18, 18, 18, mouseX, mouseY);       // arrow down left of chest
        }
        for (int i=0; i<4; i++) {
            this.drawTexturedModalRectWithMouseHighlight(stack, x+ -18,      y+28+(i+this.inventoryRows)*18, 9*18, 2*18, 18, 18, mouseX, mouseY);       // arrow up left of player inv
        }

        GlStateManager._disableBlend();
        RenderSystem.setShaderTexture(0, ICONS);      // because tooltip rendering will have changed the texture to letters
        for (int i=0; i<36; i++) {
            if (!hasShiftDown() && FrozenSlotDatabase.isSlotFrozen(i)) {
                Slot slot = this.handler.slots.get(slotIndexFromPlayerInventoryIndex(i));
                this.drawTexture(stack, x+slot.x, y+slot.y, 7*18+1, 3*18+1, 16, 16);               // stop sign
            }
        }
        
        if (ConfigurationHandler.enableSearch()) {
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
    }
    
    public static void drawChestInventoryBroom(MatrixStack stack, HandledScreen screen, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, ICONS);
        drawTexturedModalRectWithMouseHighlight(screen, stack, x, y, 11*18, 0*18, 18, 18, mouseX, mouseY);
        myTooltip(screen, stack, x, y,  18, 18, mouseX, mouseY, new TranslatableText("easierchests.sortchest"));
    }
    
    public static void drawChestInventoryAllDown(MatrixStack stack, HandledScreen screen, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, ICONS);
        drawTexturedModalRectWithMouseHighlight(screen, stack, x, y, 0 *18, 2*18, 18, 18, mouseX, mouseY);
        myTooltip(screen, stack, x, y, 18, 18, mouseX, mouseY, new TranslatableText("easierchests.matchdown"));
    }

    public static void drawPlayerInventoryBroom(MatrixStack stack, HandledScreen screen, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, ICONS);
        drawTexturedModalRectWithMouseHighlight(screen, stack, x, y, 11*18, 0*18, 18, 18, mouseX, mouseY);
        myTooltip(screen, stack, x, y, 18, 18, mouseX, mouseY, new TranslatableText("easierchests.sortplayer"));
    }
    
    public static void drawPlayerInventoryAllUp(MatrixStack stack, HandledScreen screen, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, ICONS);
        drawTexturedModalRectWithMouseHighlight(screen, stack, x, y,  8*18, 2*18, 18, 18, mouseX, mouseY);
        myTooltip(screen, stack, x, y, 18, 18, mouseX, mouseY, new TranslatableText("easierchests.matchup"));
    }

    private void drawTexturedModalRectWithMouseHighlight(MatrixStack stack, int screenx, int screeny, int textx, int texty, int sizex, int sizey, int mousex, int mousey) {
        drawTexturedModalRectWithMouseHighlight(this, stack, screenx, screeny, textx, texty, sizex, sizey, mousex, mousey);
    }
    
    private static void drawTexturedModalRectWithMouseHighlight(HandledScreen screen, MatrixStack stack, int screenx, int screeny, int textx, int texty, int sizex, int sizey, int mousex, int mousey) {
        if (mousex >= screenx && mousex < screenx+sizex && mousey >= screeny && mousey < screeny+sizey) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            screen.drawTexture(stack, screenx, screeny, textx, texty, sizex, sizey);
        } else {
            if (ConfigurationHandler.toneDownButtons()) {
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.3f);
            }
            if (ConfigurationHandler.halfSizeButtons()) {
                MatrixStack stack2 = RenderSystem.getModelViewStack();
                stack2.push();
                stack2.scale(0.5f, 0.5f, 0.5f);
                RenderSystem.applyModelViewMatrix();
                screen.drawTexture(stack, screenx*2+sizex/2, screeny*2+sizey/2, textx, texty, sizex, sizey);
                stack2.pop();
                RenderSystem.applyModelViewMatrix();
            }
            else {
                screen.drawTexture(stack, screenx, screeny, textx, texty, sizex, sizey);
            }
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void myTooltip(MatrixStack stack, int screenx, int screeny, int sizex, int sizey, int mousex, int mousey, Text tooltip) {
        myTooltip(this, stack, screenx, screeny, sizex, sizey, mousex, mousey, tooltip);
    }

    private static void myTooltip(HandledScreen screen, MatrixStack stack, int screenx, int screeny, int sizex, int sizey, int mousex, int mousey, Text tooltip) {
        if (tooltip!=null && mousex>=screenx && mousex<=screenx+sizex && mousey>=screeny && mousey <= screeny+sizey) {
            screen.renderTooltip(stack, tooltip, mousex, mousey);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, final int mouseButton) {
        if (ConfigurationHandler.enableSearch() && searchWidget.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
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
        if (mouseX>=x-18 && mouseX<=x) {                                        // left buttons
            int deltay = (int)mouseY-y;
            if (deltay < this.inventoryRows*18+17)
                clickSlotsInRow((deltay-17)/18);
            else if (deltay < (this.inventoryRows + 4 ) * 18 + 28) {
                clickSlotsInRow((deltay-28)/18);
            }
        } else if (mouseX>x+this.backgroundWidth && mouseX <= x+this.backgroundWidth+18) {   // right buttons
            /* if (mouseY>y+17 && mouseY<y+17+18)
                sortInventory(true);
            else if (mouseY > y+17+18 && mouseY < y+17+36)
                moveMatchingItems(true);
            else if (mouseY>y+28+(this.inventoryRows)*18 && mouseY<y+28+(this.inventoryRows)*18+18)
                sortInventory(false);
            else if (mouseY>y+28+(this.inventoryRows)*18+18 && mouseY<y+28+(this.inventoryRows)*18+36)
                moveMatchingItems(false);                       */
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
    
    @Override
    public boolean keyPressed(int keycode, int scancode, int modifiers) {
        if (keycode == GLFW.GLFW_KEY_ESCAPE) {
            return super.keyPressed(keycode, scancode, modifiers);
        }
        if (ConfigurationHandler.enableSearch() && searchWidget.isActive()) {
            return searchWidget.keyPressed(keycode, scancode, modifiers);
        }
        return super.keyPressed(keycode, scancode, modifiers);
    }
    
    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (ConfigurationHandler.enableSearch() && searchWidget.isActive()) {
            return searchWidget.charTyped(chr, keyCode);
        }
        return super.charTyped(chr, keyCode);
    }
    
    @Override
    public void onClose() {
        searchText=searchWidget.getText();
        super.onClose();
    }
    
    void checkForToggleFrozen(double mouseX, double mouseY) {
        for (int i = 0; i < this.handler.slots.size(); ++i) {
            int invIndex=this.playerInventoryIndexFromSlotIndex(i);
            if (invIndex==-1)
                continue;
            Slot slot = this.handler.slots.get(i);
            if (isPointWithinBounds(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
                FrozenSlotDatabase.setSlotFrozen(invIndex, !FrozenSlotDatabase.isSlotFrozen(invIndex));
            }
        }
    }
    
    private void clickSlotsInRow(int row) {
        for (int slot=row*9; slot<=row*9+8; slot++)
            if (hasShiftDown()|| !FrozenSlotDatabase.isSlotFrozen(playerInventoryIndexFromSlotIndex(slot)))
            slotClick(slot, 0, SlotActionType.QUICK_MOVE);
    }

    private void clickSlotsInColumn(int column, boolean isChest) {
        int first=(isChest ? column : inventoryRows*9+column);
        int count=(isChest ? inventoryRows : 4);
        for (int i=0; i<count; i++) {
            int slot=first+i*9;
            if (hasShiftDown() || !FrozenSlotDatabase.isSlotFrozen(playerInventoryIndexFromSlotIndex(slot)))
                slotClick(slot, 0, SlotActionType.QUICK_MOVE);
        }
    }
    
    private void sortInventory(boolean isChest) {
        Inventory inv=(isChest ? containerInventory : client.player.getInventory());
        sortInventory((SlotClicker) this, isChest, inv);
    }

    public static void sortInventory(SlotClicker screen, boolean isChest, Inventory inv) {
        int size=isChest ? inv.size() : 36;     // player's Inventory has 41 items which includes armor and left hand, but we don't want these.
        if (size>9*6 && !ConfigurationHandler.allowExtraLargeChests())
            size=9*6;
        for (int toSlot=0; toSlot<size; toSlot++) {
            ItemStack toStack=inv.getStack(toSlot);
            String targetItemName=toStack.getTranslationKey();
            if (toStack.getItem() == Items.AIR) {
                if (!isChest && toSlot<9)
                    continue;                   // Don't move stuff into empty player hotbar slots
                targetItemName="§§§";           // make sure it is highest so gets sorted last
            }
            
            // First, find an item that fits better into the current slot, but
            // don't remove hotbar things and don't
            // pull things from frozen slots unless Shift is pressed
            if (isChest || toSlot>=9 && (hasShiftDown() || !FrozenSlotDatabase.isSlotFrozen(toSlot))) {
                for (int fromSlot=toSlot+1; fromSlot<size; fromSlot++) {
                    if (!isChest && !hasShiftDown()&& FrozenSlotDatabase.isSlotFrozen(fromSlot))
                        continue;
                    ItemStack slotStack=inv.getStack(fromSlot);
                    if (slotStack.getItem()==Items.AIR)
                        continue;
                    String slotItem=inv.getStack(fromSlot).getTranslationKey();
                    if (slotItem.compareToIgnoreCase(targetItemName)<0) {
                        targetItemName=slotItem;
                    }
                }
            } else {
                // Hotbar slots: allow filling them up but not replacing armor/weapon
                if (toStack.getCount() >= toStack.getMaxCount()) {
                    continue;
                }
            }
            
            // Next, check for items that we can merge into the current item,
            // or that have the same name but are lower in some respect
            // ( display name, number of enchantments, name of first enchantment, damage ...)

            for (int fromSlot=toSlot+1; fromSlot<size; fromSlot++) {
                if (!isChest && !hasShiftDown()) {
                    if (FrozenSlotDatabase.isSlotFrozen(fromSlot)) {
                        continue;
                    }
                }
                toStack=inv.getStack(toSlot);
                ItemStack fromStack=inv.getStack(fromSlot);
                if (fromStack.getTranslationKey().equals(targetItemName)
                &&  (!toStack.getTranslationKey().equals(targetItemName)
                    ||    stackShouldGoBefore(fromStack, toStack))) {
                    screen.EasierChests$onMouseClick (null, isChest ? fromSlot : screen.EasierChests$slotIndexfromPlayerInventoryIndex(fromSlot), 0, SlotActionType.PICKUP);
                    screen.EasierChests$onMouseClick (null, isChest ? toSlot   : screen.EasierChests$slotIndexfromPlayerInventoryIndex(toSlot)  , 0, SlotActionType.PICKUP);
                    screen.EasierChests$onMouseClick (null, isChest ? fromSlot : screen.EasierChests$slotIndexfromPlayerInventoryIndex(fromSlot), 0, SlotActionType.PICKUP);                    
                }
            }
        }
    }
    
    private static boolean stackShouldGoBefore(ItemStack replacement, ItemStack original) {
        String replacementName = replacement.getName().getString();
        String originalName    = original.getName().getString();
        // alphabetically by display name
        
        if (replacementName.compareToIgnoreCase(originalName) > 0) {
            return false;
        }
        // if both damageable (same item name ...) then less damage before more damage
        if (replacement.isDamageable() && original.isDamageable()
        &&  replacement.getDamage() > original.getDamage()) {
            return false;
        }
        // less enchantments before more enchantments
        NbtList originalEnchantments = (original.getItem() == Items.ENCHANTED_BOOK) ? EnchantedBookItem.getEnchantmentNbt(original) : original.getEnchantments();
        NbtList replacementEnchantments = (replacement.getItem() == Items.ENCHANTED_BOOK) ? EnchantedBookItem.getEnchantmentNbt(replacement) : replacement.getEnchantments();
        if (replacementEnchantments == null || replacementEnchantments.isEmpty()) {
            if (originalEnchantments == null || originalEnchantments.isEmpty()) {
                // Items are equal - same item type, same display name, no enchantments.
                // Try to merge them, but only if the original ItemStack isn't full.
                return original.getCount() != original.getMaxCount();
            }
            return true;
        }
        if (originalEnchantments == null || originalEnchantments.isEmpty()) {
            return false;
        }
        if (replacementEnchantments.size() < originalEnchantments.size()) {
            return true;
        } else if (replacementEnchantments.size() == originalEnchantments.size()) {
            for (int i=0; i<replacementEnchantments.size(); i++) {
                String originalId = ((NbtCompound)originalEnchantments.get(i)).getString("id");
                String replacementId = ((NbtCompound)replacementEnchantments.get(i)).getString("id");
                int compared = originalId.compareTo(replacementId);

                if (compared < 0) {
                    return false;
                } else if (compared > 0) {
                    return true;
                }
                int originalLevel = ((NbtCompound)originalEnchantments.get(i)).getInt("lvl");
                int replacementLevel = ((NbtCompound)replacementEnchantments.get(i)).getInt("lvl");
                if (originalLevel == replacementLevel) {
                    continue;
                }
                return replacementLevel < originalLevel;
            }
            return false;           // all enchantments identical
        } else {
            return false;
        }
    }
    
    private void moveMatchingItems(boolean isChest) {
        moveMatchingItems(this, isChest);
    }
    
    public static void moveMatchingItems(HandledScreen screen, boolean isChestToPlayer) {
        // System.out.println("move matching from "+(isChest ? "chest" : "player"));
        Inventory from, to;
        int fromSize, toSize;
        MinecraftClient minecraft = MinecraftClient.getInstance();
        Inventory containerInventory = screen.getScreenHandler().getSlot(0).inventory;

        // use 36 for player inventory size so we won't use armor/2h slots
        if (isChestToPlayer) {
            from = containerInventory;                  fromSize=from.size();
            to   = minecraft.player.getInventory();     toSize  =36;
        } else {
            from = minecraft.player.getInventory();     fromSize=36;
            to   = containerInventory;                  toSize  =to.size();
        }
        if (!ConfigurationHandler.allowExtraLargeChests()) {
            if (fromSize>9*6)   fromSize=9*6;
            if (toSize  >9*6)   toSize=9*6;
        }
        for (int i=0; i<fromSize; i++) {
            if (!isChestToPlayer && !hasShiftDown() && FrozenSlotDatabase.isSlotFrozen(i))
                continue;
            ItemStack fromStack = from.getStack(i);
            int slot;
            if (isChestToPlayer) {
                slot=i;
            } else  {
                slot=((SlotClicker)screen).EasierChests$slotIndexfromPlayerInventoryIndex(i);
            }
            for (int j=0; j<toSize; j++) {
                ItemStack toStack = to.getStack(j);
                if (fromStack.isItemEqual(toStack)
                &&  ItemStack.areTagsEqual(fromStack, toStack)) {
                    // System.out.println("  from["+i+"] is same as to["+j+"] ("+toStack.getDisplayName()+"), clicking "+slot);
                    ((SlotClicker)screen).EasierChests$onMouseClick(null, slot, 0, SlotActionType.QUICK_MOVE);
                }
            }
        }
    }
    
    private void slotClick(int slot, int mouseButton, SlotActionType clickType) {
        ((SlotClicker)this).EasierChests$onMouseClick(null, slot, mouseButton, clickType);
    }
    
    private int playerInventoryIndexFromSlotIndex(int slot) {
        return ((SlotClicker)this).EasierChests$playerInventoryIndexFromSlotIndex(slot);
    }
    
    private int slotIndexFromPlayerInventoryIndex(int idx) {
        return ((SlotClicker)this).EasierChests$slotIndexfromPlayerInventoryIndex(idx);
    }
}