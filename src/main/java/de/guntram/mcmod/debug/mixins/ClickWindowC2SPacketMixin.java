package de.guntram.mcmod.debug.mixins;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClickSlotC2SPacket.class)
public class ClickWindowC2SPacketMixin {
    
    static private final Logger LOGGER = LogManager.getLogger();

    @Inject(method="<init>(IIILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/item/ItemStack;S)V", at=@At("RETURN"))
    private void dumpC2SNewInfo(int syncid, int slot, int button, SlotActionType actionType, ItemStack stack,
            short transaction, CallbackInfo ci) {
        LOGGER.debug(() -> "new ClickWindow C2S: syncid="+syncid+", slot="+slot+", button="+button+
                ", action="+actionType.toString()+", item="+stack.getCount()+" of "+stack.getName().asString()+
                ", transaction="+transaction);
    }
}
