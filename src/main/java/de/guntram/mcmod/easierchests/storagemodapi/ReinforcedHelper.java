package de.guntram.mcmod.easierchests.storagemodapi;

import atonkish.reinfcore.screen.ReinforcedStorageScreenHandler;
import net.minecraft.screen.ScreenHandler;
import org.apache.logging.log4j.LogManager;

public class ReinforcedHelper implements ChestGuiInfo {

    @Override
    public int getRows(ScreenHandler handler) {
        try {
            return ((ReinforcedStorageScreenHandler)handler).getRows();
        } catch (NoSuchMethodError ex) {
            warnOutdated("Reinforced");
            return -1;
        }        
    }

    @Override
    public int getColumns(ScreenHandler handler) {
        try {
            return ((ReinforcedStorageScreenHandler)handler).getColumns();
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
