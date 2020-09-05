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
    private String matchHighlightColor;

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
        
        config.migrate("Allow extra large chests", "easierchests.config.largechests");
        config.migrate("Half button size", "easierchests.config.halfsize");
        config.migrate("Transparent buttons", "easierchests.config.transparent");
        extraLargeChests=config.getBoolean("easierchests.config.largechests", Configuration.CATEGORY_CLIENT, false, "easierchests.config.tt.largechests");
        halfSizeButtons=config.getBoolean("easierchests.config.halfsize", Configuration.CATEGORY_CLIENT, false, "easierchests.config.tt.halfsize");
        toneDownButtons=config.getBoolean("easierchests.config.transparent", Configuration.CATEGORY_CLIENT, true, "easierchests.config.tt.transparent");
        matchHighlightColor=config.getString("easierchests.config.highlight", Configuration.CATEGORY_CLIENT, "4000ff00", "easierchests.config.tt.highlight");
        
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
    
    public static String getHighlightColor() {
        return getInstance().matchHighlightColor;
    }
}