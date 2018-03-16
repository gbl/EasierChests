package de.guntram.mcmod.easierchests;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = EasierChests.MODID, 
        version = EasierChests.VERSION,
	clientSideOnly = true, 
	guiFactory = "de.guntram.mcmod.easierchests.GuiFactory",
	acceptedMinecraftVersions = "[1.12]",
        updateJSON = "https://raw.githubusercontent.com/gbl/EasierChests/master/versioncheck.json"
)

public class EasierChests
{
    static final String MODID="easierchests";
    static final String VERSION="@VERSION@";
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(OpenChestEventHandler.getInstance());
    }

    @EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        ConfigurationHandler confHandler = ConfigurationHandler.getInstance();
        confHandler.load(event.getSuggestedConfigurationFile());
        FrozenSlotDatabase.init(event.getModConfigurationDirectory());
        MinecraftForge.EVENT_BUS.register(confHandler);
    }
}
