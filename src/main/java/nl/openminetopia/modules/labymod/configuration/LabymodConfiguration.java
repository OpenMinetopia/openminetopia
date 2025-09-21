package nl.openminetopia.modules.labymod.configuration;

import lombok.Getter;
import nl.openminetopia.utils.config.ConfigurateConfig;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;

@Getter
public class LabymodConfiguration extends ConfigurateConfig {

	private final boolean enabled;

	private final boolean economyDisplayEnabled;
	private final String economyIconUrl;

	private final boolean subtitleEnabled;
	private final String subtitleDisplay;

	public LabymodConfiguration(File file) {
		super(file, "labymod.yml", "default/labymod.yml", true);

		this.enabled = rootNode.node("enabled").getBoolean();

		ConfigurationNode economyNode = rootNode.node("economy-display");

		this.economyDisplayEnabled = economyNode.node( "enabled").getBoolean();
		this.economyIconUrl = economyNode.node("icon-url").getString();

		ConfigurationNode subtitleNode = rootNode.node("subtitle");

		this.subtitleEnabled = subtitleNode.node("enabled").getBoolean();
		this.subtitleDisplay = subtitleNode.node("display").getString();


	}


}
