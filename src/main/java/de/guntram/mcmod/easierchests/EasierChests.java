package de.guntram.mcmod.easierchests;

import java.io.File;
import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

public class EasierChests implements InitializationListener 
{
    static final String MODID="easierchests";
    static final String VERSION="@VERSION@";
    
    @Override
    public void onInitialization() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.easierchests.json");        
        ConfigurationHandler confHandler = ConfigurationHandler.getInstance();
        confHandler.load(new File("easierchests.json"));         // TODO
        FrozenSlotDatabase.init(new File("."));
    }
}
