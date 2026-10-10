package nl.openminetopia.modules.stattop.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.stattop.StatTopEntry;
import nl.openminetopia.api.stattop.StatTopType;
import nl.openminetopia.api.stattop.StatTopTypes;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.stattop.StatTopModule;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("stattop")
public class StatTopCommand extends BaseCommand {

    @Default
    @CommandCompletion("@statTopTypes")
    @Description("Bekijk de toplijst van een statistiek.")
    public void stattop(CommandSender sender, @Optional String stat) {
        StatTopModule module = OpenMinetopia.getModuleManager().get(StatTopModule.class);

        if (stat == null) {
            List<StatTopType> types = module.getViewableTypes(sender);
            if (types.isEmpty()) {
                sender.sendMessage(MessageConfiguration.component("stattop_no_permission"));
                return;
            }

            String stats = types.stream().map(StatTopType::key).collect(Collectors.joining(", "));
            ChatUtils.sendMessage(sender, MessageConfiguration.message("stattop_available").replace("<stats>", stats));
            return;
        }

        StatTopType type = StatTopTypes.byKey(stat);
        if (type == null || !module.getConfiguration().isEnabled(type.key())) {
            sender.sendMessage(MessageConfiguration.component("stattop_unknown"));
            return;
        }

        if (!module.isViewable(sender, type)) {
            sender.sendMessage(MessageConfiguration.component("stattop_no_permission"));
            return;
        }

        module.getService().getTop(type).whenComplete((top, throwable) -> {
            if (throwable != null) {
                OpenMinetopia.getInstance().getLogger().warning("Failed to load stattop '" + type.key() + "': " + throwable.getMessage());
                sender.sendMessage(MessageConfiguration.component("database_read_error"));
                return;
            }

            if (top.isEmpty()) {
                sender.sendMessage(MessageConfiguration.component("stattop_empty"));
                return;
            }

            ChatUtils.sendMessage(sender, MessageConfiguration.message("stattop_header")
                    .replace("<stat>", module.getConfiguration().getDisplayName(type))
                    .replace("<amount>", String.valueOf(top.size())));

            for (int i = 0; i < top.size(); i++) {
                StatTopEntry entry = top.get(i);
                String name = Bukkit.getOfflinePlayer(entry.uuid()).getName();

                ChatUtils.sendMessage(sender, MessageConfiguration.message("stattop_entry")
                        .replace("<rank>", String.valueOf(i + 1))
                        .replace("<player>", name == null ? "Onbekend" : name)
                        .replace("<value>", type.format(entry.value())));
            }
        });
    }
}
