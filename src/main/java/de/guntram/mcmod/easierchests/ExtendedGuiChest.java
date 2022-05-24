package de.guntram.mcmod.easierchests;

import com.mojang.blaze3d.systems.RenderSystem;
import de.guntram.mcmod.easierchests.interfaces.SlotClicker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/*
 * Warning - this code should extend ContainerScreen54 AND ShulkerBoxScreen,
 * which it can't. So we extend the superclass, and implement the few methods
 * that are in those classes (and are identical ...) ourselves. Doh.
 */

public class ExtendedGuiChest extends HandledScreen
{
    private final int inventoryRows;
    public static final Identifier ICONS=new Identifier(EasierChests.MODID, "textures/icons.png");
    private final Identifier background;
    private final boolean separateBlits;
    
    public ExtendedGuiChest(GenericContainerScreenHandler container, PlayerInventory lowerInv, Text title,
            int rows)
    {
        super(container, lowerInv, title);
        this.inventoryRows=rows;
        backgroundHeight = 114 + rows * 18;
        background = new Identifier("minecraft", "textures/gui/container/generic_54.png");
        separateBlits=true;
    }
    
    public ExtendedGuiChest(ShulkerBoxScreenHandler container, PlayerInventory lowerInv, Text title) {
        super(container, lowerInv, title);
        inventoryRows = 3;
        background = new Identifier("minecraft", "textures/gui/container/shulker_box.png");
        separateBlits=false;
    }
    
    @Override
    public void init() {
        super.init();
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        drawMouseoverTooltip(stack, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(MatrixStack stack, int mouseX, int mouseY)
    {
        this.textRenderer.draw(stack, this.title.getString(), 8.0F, 6.0F, 4210752);
        this.textRenderer.draw(stack, this.playerInventoryTitle, 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
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
    }
    
    public static void drawChestInventoryBroom(MatrixStack stack, HandledScreen screen, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, ICONS);
        drawTexturedModalRectWithMouseHighlight(screen, stack, x, y, 11*18, 0*18, 18, 18, mouseX, mouseY);
        myTooltip(screen, stack, x, y,  18, 18, mouseX, mouseY, Text.translatable("easierchests.sortchest"));
    }
    
    public static void drawChestInventoryAllDown(MatrixStack stack, HandledScreen screen, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, ICONS);
        drawTexturedModalRectWithMouseHighlight(screen, stack, x, y, 0 *18, 2*18, 18, 18, mouseX, mouseY);
        myTooltip(screen, stack, x, y, 18, 18, mouseX, mouseY, Text.translatable("easierchests.matchdown"));
    }

    public static void drawPlayerInventoryBroom(MatrixStack stack, HandledScreen screen, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, ICONS);
        drawTexturedModalRectWithMouseHighlight(screen, stack, x, y, 11*18, 0*18, 18, 18, mouseX, mouseY);
        myTooltip(screen, stack, x, y, 18, 18, mouseX, mouseY, Text.translatable("easierchests.sortplayer"));
    }
    
    public static void drawPlayerInventoryAllUp(MatrixStack stack, HandledScreen screen, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, ICONS);
        drawTexturedModalRectWithMouseHighlight(screen, stack, x, y,  8*18, 2*18, 18, 18, mouseX, mouseY);
        myTooltip(screen, stack, x, y, 18, 18, mouseX, mouseY, Text.translatable("easierchests.matchup"));
    }

    public static void drawTexturedModalRectWithMouseHighlight(HandledScreen screen, MatrixStack stack, int screenx, int screeny, int textx, int texty, int sizex, int sizey, int mousex, int mousey) {
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

    private static void myTooltip(HandledScreen screen, MatrixStack stack, int screenx, int screeny, int sizex, int sizey, int mousex, int mousey, Text tooltip) {
        if (tooltip!=null && mousex>=screenx && mousex<=screenx+sizex && mousey>=screeny && mousey <= screeny+sizey) {
            screen.renderTooltip(stack, tooltip, mousex, mousey);
        }
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
                &&  ItemStack.areNbtEqual(fromStack, toStack)) {
                    // System.out.println("  from["+i+"] is same as to["+j+"] ("+toStack.getDisplayName()+"), clicking "+slot);
                    ((SlotClicker)screen).EasierChests$onMouseClick(null, slot, 0, SlotActionType.QUICK_MOVE);
                }
            }
        }
    }
}