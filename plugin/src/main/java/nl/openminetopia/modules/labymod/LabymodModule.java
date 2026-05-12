package nl.openminetopia.modules.labymod;

import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import lombok.Setter;
import net.labymod.serverapi.core.LabyModProtocol;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.modules.labymod.configuration.LabymodConfiguration;
import nl.openminetopia.modules.labymod.listeners.LabyPlayerListener;
import org.jetbrains.annotations.NotNull;


@Setter
@Getter
public class LabymodModule extends ExtendedSpigotModule {

	public LabymodModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager) {
		super(moduleManager);
	}

	private LabymodConfiguration configuration;
	private LabyModProtocol labyModProtocol;

	public void onEnable() {
		configuration = new LabymodConfiguration(OpenMinetopia.getInstance().getDataFolder());
		configuration.saveConfiguration();

		if (!configuration.isEnabled()) return;
		if (!OpenMinetopia.getInstance().isLabymodSupport()) {
			getLogger().warn("labymod support is enabled in labymod.yml but the Labymod server API is not installed. Disabling labymod support.");
			return;
		}

		registerComponent(new LabyPlayerListener());


	}

}
