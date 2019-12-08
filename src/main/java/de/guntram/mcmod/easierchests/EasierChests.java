package de.guntram.mcmod.easierchests;

import de.guntram.mcmod.fabrictools.ConfigurationProvider;
import java.io.File;
import net.fabricmc.api.ClientModInitializer;

public class EasierChests implements ClientModInitializer 
{
    static final String MODID="easierchests";
    static final String MODNAME="EasierChests";
    static final String VERSION="@VERSION@";
    
    @Override
    public void onInitializeClient() {
        ConfigurationHandler confHandler = ConfigurationHandler.getInstance();
        ConfigurationProvider.register(MODNAME, confHandler);
        confHandler.load(ConfigurationProvider.getSuggestedFile(MODID));
        confHandler.load(null);
        FrozenSlotDatabase.init(new File("config"));
    }
}
