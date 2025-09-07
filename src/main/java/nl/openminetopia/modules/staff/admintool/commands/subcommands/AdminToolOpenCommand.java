package nl.openminetopia.modules.staff.admintool.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.staff.admintool.menus.AdminToolMenu;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@CommandAlias("admintool")
public class AdminToolOpenCommand extends BaseCommand {

    @Subcommand("open")
    @CommandCompletion("@players")
    @CommandPermission("openminetopia.admintool.open")
    public void open(Player player, OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
            player.sendMessage(ChatUtils.color("<red>Deze speler heeft nog nooit gespeeld."));
            return;
        }

        CompletableFuture<MinetopiaPlayer> future = PlayerManager.getInstance().getMinetopiaPlayer(offlinePlayer);

        future.whenComplete((minetopiaPlayer, throwable) -> {
            if (throwable != null) {
                player.sendMessage(ChatUtils.color("<red>Er is een fout opgetreden bij het openen van de AdminTool."));
                throwable.printStackTrace();
                return;
            }

            if (minetopiaPlayer == null) return;
            if (!offlinePlayer.isOnline()) minetopiaPlayer.getFitness().getFitnessModule().getFitnessRunnable().forceMarkDirty(offlinePlayer.getUniqueId());

            BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
            BankAccountModel bankAccountModel = bankingModule.getAccountByIdAsync(offlinePlayer.getUniqueId()).join();

            Bukkit.getScheduler().runTask(OpenMinetopia.getInstance(), () -> {
                new AdminToolMenu(player, offlinePlayer, minetopiaPlayer, bankAccountModel).open(player);
            });
        });
    }
}
