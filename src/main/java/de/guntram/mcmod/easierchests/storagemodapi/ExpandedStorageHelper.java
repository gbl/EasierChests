package de.guntram.mcmod.easierchests.storagemodapi;

import net.minecraft.screen.ScreenHandler;
import ninjaphenix.container_library.api.inventory.AbstractHandler;
import org.apache.logging.log4j.LogManager;

public class ExpandedStorageHelper implements ChestGuiInfo {

    @Override
    public int getRows(ScreenHandler handler) {
        try {
            return ((AbstractHandler) handler).getMenuHeight();
        } catch (NoSuchMethodError ex) {
            warnOutdated("Reinforced");            
            return -1;
        }
    }

    @Override
    public int getColumns(ScreenHandler handler) {
        try {
            return ((AbstractHandler) handler).getMenuWidth();
        } catch (NoSuchMethodError ex) {
            warnOutdated("Reinforced");
            return -1;
        }
    }

    private boolean warnedOutdated = false;
    private void warnOutdated(String what) {
        if (!warnedOutdated) {
            LogManager.getLogger(this.getClass()).warn("You need a current version of "+what+", trying to fall back");
            warnedOutdated = true;
        }
    }
}
