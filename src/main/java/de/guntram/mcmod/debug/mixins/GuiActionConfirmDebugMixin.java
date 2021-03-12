package de.guntram.mcmod.debug.mixins;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayNetworkHandler.class)
public class GuiActionConfirmDebugMixin {
    
    static private final Logger LOGGER = LogManager.getLogger();
/*
    @Inject(method="onConfirmScreenAction", at=@At(value="INVOKE", target="Lnet/minecraft/network/packet/s2c/play/ConfirmScreenActionS2CPacket;getSyncId()I"))
    private void dumpActionConfirmInfo(ConfirmScreenActionS2CPacket packet, CallbackInfo ci) {
        LOGGER.debug(() -> "confirm: id="+packet.getSyncId()+", action="+packet.getActionId()+", accepted="+packet.wasAccepted());
    }
    @Inject(method="onInventory", at=@At("HEAD"))
    private void dumpInventoryInfo(InventoryS2CPacket packet, CallbackInfo ci) {
        LOGGER.debug(() -> "inventory: syncid="+packet.getSyncId()+", slotcount="+packet.getContents().size());
    }
*/
}
