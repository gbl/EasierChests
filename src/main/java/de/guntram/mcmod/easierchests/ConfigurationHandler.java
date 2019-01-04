package de.guntram.mcmod.easierchests;

import java.io.File;

public class ConfigurationHandler {

    private static ConfigurationHandler instance;

    private String configFileName;

    public static ConfigurationHandler getInstance() {
        if (instance==null)
            instance=new ConfigurationHandler();
        return instance;
    }
    private boolean extraLargeChests;
    private boolean halfSizeButtons;
    private boolean toneDownButtons;

    public void load(final File configFile) {
        loadConfig();
    }

    private void loadConfig() {
        extraLargeChests=false;
        halfSizeButtons=false;
        toneDownButtons=true;
    }
    
    public static String getConfigFileName() {
        return getInstance().configFileName;
    }
    
    public static boolean allowExtraLargeChests() {
        return getInstance().extraLargeChests;
    }
    
    public static boolean toneDownButtons() {
        return getInstance().toneDownButtons;
    }
    
    public static boolean halfSizeButtons() {
        return getInstance().halfSizeButtons;
    }
}
