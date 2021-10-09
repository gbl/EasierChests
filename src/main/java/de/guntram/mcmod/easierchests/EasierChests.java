package de.guntram.mcmod.easierchests;

import de.guntram.mcmod.crowdintranslate.CrowdinTranslate;
import de.guntram.mcmod.fabrictools.ConfigurationProvider;
import java.io.File;
import java.util.HashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.screen.ScreenHandler;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.glfw.GLFW;
import de.guntram.mcmod.easierchests.storagemodapi.ChestGuiInfo;
import org.apache.logging.log4j.Logger;

public class EasierChests implements ClientModInitializer 
{
    static final String MODID="easierchests";
    static final String MODNAME="EasierChests";
    
    private static final String category = "key.categories.easierchests";
    
    public static KeyBinding keySortChest, keyMoveToChest, 
                             keySortPlInv, keyMoveToPlInv,
                             keySearchBox;
    
    private static HashMap<String, ChestGuiInfo> modHelpers = new HashMap<>();
    private static Logger LOGGER = LogManager.getLogger(EasierChests.class);
    
    @Override
    public void onInitializeClient() {
        CrowdinTranslate.downloadTranslations(MODID);
        ConfigurationHandler confHandler = ConfigurationHandler.getInstance();
        ConfigurationProvider.register(MODNAME, confHandler);
        confHandler.load(ConfigurationProvider.getSuggestedFile(MODID));
        confHandler.load(null);
        FrozenSlotDatabase.init(new File("config"));
        
        keySortChest = registerKey("sortchest", GLFW.GLFW_KEY_KP_7);
        keyMoveToChest = registerKey("matchup", GLFW.GLFW_KEY_KP_8);
        keySortPlInv = registerKey("sortplayer", GLFW.GLFW_KEY_KP_1);
        keyMoveToPlInv = registerKey("matchdown", GLFW.GLFW_KEY_KP_2);
        keySearchBox = registerKey("searchbox", GLFW.GLFW_KEY_UNKNOWN);
        
        registerMod("inmis", "draylar.inmis.ui.BackpackScreenHandler", "de.guntram.mcmod.easierchests.storagemodapi.InmisHelper");
        registerMod("Reinforced", "atonkish.reinfcore.screen.ReinforcedStorageScreenHandler", "de.guntram.mcmod.easierchests.storagemodapi.ReinforcedHelper");
        registerMod("Expanded Storage", "ninjaphenix.container_library.api.inventory.AbstractHandler", "de.guntram.mcmod.easierchests.storagemodapi.ExpandedStorageHelper");
    }
    
    public static void registerMod(String screenHandlerClassName, ChestGuiInfo helper) {
        modHelpers.put(screenHandlerClassName, helper);
    }
    
    public static void registerMod(String modName, String screenHandlerClassName, String helperClassName) {
        try {
            Class.forName(screenHandlerClassName);
            ChestGuiInfo helper = (ChestGuiInfo) Class.forName(helperClassName).getDeclaredConstructor().newInstance();
            registerMod(screenHandlerClassName, helper);
            LOGGER.info("EasierChests enabling support for "+modName);
        } catch (Exception ex) {
            LOGGER.info("EasierChests did not find mod "+modName+", not enabling support");
        }
    }
    
    public static ChestGuiInfo getHelperForHandler(ScreenHandler handler) {
        return modHelpers.get(handler.getClass().getCanonicalName());
    }
    
    private KeyBinding registerKey(String key, int code) {
        KeyBinding result = new KeyBinding("key.easierchests."+key, code, category);
        KeyBindingHelper.registerKeyBinding(result);
        return result;
    }
}
