package de.guntram.mcmod.easierchests;

import io.github.prospector.modmenu.api.ModMenuApi;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.gui.screen.Screen;

public class MMConfigurationHandler implements ModMenuApi
{
    @Override
    public String getModId() {
        return EasierChests.MODID;
    }

    @Override
    public Optional<Supplier<Screen>> getConfigScreen(Screen screen) {
        // return Optional.of(new GuiModOptions(screen, EasierChests.MODNAME, ConfigurationProvider.getHandler(EasierChests.MODNAME)));
        return Optional.empty();
    }
}
