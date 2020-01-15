package de.guntram.mcmod.easierchests;

import de.guntram.mcmod.fabrictools.ConfigurationProvider;
import java.io.File;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_8;

public class EasierChests implements ClientModInitializer 
{
    static final String MODID="easierchests";
    static final String MODNAME="EasierChests";
    static final String VERSION="1.15-fabric0.4.23-1.4";
    
    private static final String category = "key.categories.easierchests";
    
    public static FabricKeyBinding keySortChest, keyMoveToChest, keySortPlInv, keyMoveToPlInv;
    
    @Override
    public void onInitializeClient() {
        ConfigurationHandler confHandler = ConfigurationHandler.getInstance();
        ConfigurationProvider.register(MODNAME, confHandler);
        confHandler.load(ConfigurationProvider.getSuggestedFile(MODID));
        confHandler.load(null);
        FrozenSlotDatabase.init(new File("config"));
        
        KeyBindingRegistry.INSTANCE.addCategory(category);
        keySortChest = registerKey("sortchest", GLFW_KEY_KP_7);
        keyMoveToChest = registerKey("matchup", GLFW_KEY_KP_8);
        keySortPlInv = registerKey("sortplayer", GLFW_KEY_KP_1);
        keyMoveToPlInv = registerKey("matchdown", GLFW_KEY_KP_2);
    }
    
    private FabricKeyBinding registerKey(String key, int code) {
        FabricKeyBinding result = FabricKeyBinding.Builder.create(new Identifier("easierchests", key), InputUtil.Type.KEYSYM, code, category).build();
        KeyBindingRegistry.INSTANCE.register(result);
        return result;
    }
}
