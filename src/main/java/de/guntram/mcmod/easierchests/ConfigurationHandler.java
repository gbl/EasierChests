package de.guntram.mcmod.easierchests;

import de.guntram.mcmod.fabrictools.ConfigChangedEvent;
import de.guntram.mcmod.fabrictools.Configuration;
import de.guntram.mcmod.fabrictools.ModConfigurationHandler;
import java.io.File;

public class ConfigurationHandler implements ModConfigurationHandler {

    private static ConfigurationHandler instance;

    private Configuration config;
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
        if (config == null) {
            config = new Configuration(configFile);
            configFileName=configFile.getPath();
            loadConfig();
        }
    }

    @Override
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        // System.out.println("OnConfigChanged for "+event.getModID());
        if (event.getModID().equalsIgnoreCase(EasierChests.MODID)) {
            loadConfig();
        }
    }
    
    private void loadConfig() {
        extraLargeChests=config.getBoolean("Allow extra large chests", Configuration.CATEGORY_CLIENT, false, "Allow chests to have more than 54 entries");
        halfSizeButtons=config.getBoolean("Half button size", Configuration.CATEGORY_CLIENT, false, "Half button size unless mouse hovers over them");
        toneDownButtons=config.getBoolean("Transparent buttons", Configuration.CATEGORY_CLIENT, true, "Make buttons transparent unless mouse hovers over them");
        
        if (config.hasChanged())
            config.save();
    }
    
    @Override
    public Configuration getConfig() {
        return config;
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