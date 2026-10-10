package nl.openminetopia.modules.stattop;

import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import lombok.Setter;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.stattop.StatTopType;
import nl.openminetopia.api.stattop.StatTopTypes;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.player.PlayerModule;
import nl.openminetopia.modules.stattop.commands.StatTopCommand;
import nl.openminetopia.modules.stattop.configuration.StatTopConfiguration;
import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class StatTopModule extends ExtendedSpigotModule {

    public StatTopModule(SpigotModuleManager<@NotNull OpenMinetopia> moduleManager, DataModule dataModule, PlayerModule playerModule) {
        super(moduleManager);
    }

    @Setter
    private StatTopConfiguration configuration;
    private StatTopService service;

    @Override
    public void onEnable() {
        configuration = new StatTopConfiguration(OpenMinetopia.getInstance().getDataFolder());
        configuration.saveConfiguration();

        service = new StatTopService(this);

        registerComponent(new StatTopCommand());

        OpenMinetopia.getCommandManager().getCommandCompletions().registerAsyncCompletion("statTopTypes", context ->
                getViewableTypes(context.getSender()).stream().map(StatTopType::key).toList());
    }

    public boolean isViewable(CommandSender sender, StatTopType type) {
        return configuration.isEnabled(type.key()) && sender.hasPermission("openminetopia.stattop." + type.key());
    }

    public List<StatTopType> getViewableTypes(CommandSender sender) {
        return StatTopTypes.all().stream().filter(type -> isViewable(sender, type)).toList();
    }
}
