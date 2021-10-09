package de.guntram.mcmod.easierchests.storagemodapi;

import draylar.inmis.ui.BackpackScreenHandler;
import net.minecraft.screen.ScreenHandler;

public class InmisHelper implements ChestGuiInfo {

    @Override
    public int getRows(ScreenHandler handler) {
        return ((BackpackScreenHandler) handler).getItem().getTier().getNumberOfRows();
    }

    @Override
    public int getColumns(ScreenHandler handler) {
        return ((BackpackScreenHandler) handler).getItem().getTier().getRowWidth();
    }
    
}
