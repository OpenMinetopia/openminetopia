package nl.openminetopia.modules.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.core.playerdata.PlayerDataCategory;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("openminetopia|sdb|minetopia|omt")
public class PlayerDataCommand extends BaseCommand {

    @Subcommand("playerdata")
    @Syntax("<speler> remove <categorie|all> [confirm]")
    @CommandCompletion("@players remove @playerDataCategories confirm")
    @CommandPermission("openminetopia.playerdata.remove")
    @Description("Verwijder gegevens van een speler.")
    public void playerData(CommandSender sender, OfflinePlayer target, String action, String category, @Optional String confirm) {
        if (!action.equalsIgnoreCase("remove")) throw new InvalidCommandArgument(true);

        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            ChatUtils.sendMessage(sender, MessageConfiguration.message("player_not_found"));
            return;
        }

        boolean all = category.equalsIgnoreCase("all");
        PlayerDataCategory single = PlayerDataCategory.byKey(category);
        if (!all && (single == null || !single.isAvailable())) {
            ChatUtils.sendMessage(sender, MessageConfiguration.message("core_playerdata_unknown_category")
                    .replace("<categories>", PlayerDataCategory.available().stream().map(PlayerDataCategory::key).collect(Collectors.joining(", "))));
            return;
        }

        List<PlayerDataCategory> categories = all ? PlayerDataCategory.available() : List.of(single);
        String name = String.valueOf(target.getName());
        String categoryKey = category.toLowerCase();

        if (!"confirm".equalsIgnoreCase(confirm)) {
            ChatUtils.sendMessage(sender, MessageConfiguration.message("core_playerdata_confirm")
                    .replace("<player>", name)
                    .replace("<category>", categoryKey)
                    .replace("<command>", "/omt playerdata " + name + " remove " + categoryKey + " confirm"));
            return;
        }

        PlayerManager.getInstance().getMinetopiaPlayer(target).whenComplete((minetopiaPlayer, throwable) -> {
            if (throwable != null || minetopiaPlayer == null) {
                ChatUtils.sendMessage(sender, MessageConfiguration.message("player_data_not_loaded"));
                return;
            }

            Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () -> {
                categories.forEach(dataCategory -> dataCategory.reset(minetopiaPlayer));
                minetopiaPlayer.save();

                // Kicking after the reset makes the quit listener persist the cleared state instead of stale in-memory data.
                Player online = target.getPlayer();
                if (all && online != null) online.kick(MessageConfiguration.component("core_playerdata_kick"));

                ChatUtils.sendMessage(sender, MessageConfiguration.message("core_playerdata_removed")
                        .replace("<player>", name)
                        .replace("<category>", categoryKey));
            });
        });
    }
}
