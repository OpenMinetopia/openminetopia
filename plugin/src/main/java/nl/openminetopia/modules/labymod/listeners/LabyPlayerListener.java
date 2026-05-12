package nl.openminetopia.modules.labymod.listeners;

import net.kyori.adventure.text.Component;
import net.labymod.serverapi.server.bukkit.LabyModPlayer;
import net.labymod.serverapi.server.bukkit.event.LabyModPlayerJoinEvent;
import net.labymod.serverapi.api.model.component.ServerAPIComponent;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.labymod.LabymodModule;
import nl.openminetopia.modules.labymod.configuration.LabymodConfiguration;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LabyPlayerListener implements Listener {

	private final LabymodConfiguration configuration;
	private final BankingModule bankingModule;
	private final Map<UUID, BukkitTask> playerTasks = new HashMap<>();

	public LabyPlayerListener() {
		LabymodModule labymodModule = OpenMinetopia.getModuleManager().get(LabymodModule.class);
		this.configuration = labymodModule.getConfiguration();
		this.bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
	}

	@EventHandler
	public void onJoin(LabyModPlayerJoinEvent event) {
		LabyModPlayer labyPlayer = event.labyModPlayer();
		Player player = labyPlayer.getPlayer();
		UUID playerId = player.getUniqueId();

		boolean economyEnabled = configuration.isEconomyDisplayEnabled();
		boolean subtitleEnabled = configuration.isSubtitleEnabled();

		if (!economyEnabled && !subtitleEnabled) {
			return;
		}

		BukkitTask task = Bukkit.getScheduler().runTaskTimer(
				OpenMinetopia.getInstance(),
				() -> {

					if (!player.isOnline()) {
						BukkitTask currentTask = playerTasks.remove(playerId);
						if (currentTask != null) {
							currentTask.cancel();
						}
						return;
					}

					BankAccountModel accountModel = economyEnabled ? bankingModule.getAccountById(playerId) : null;
					updateLabyModFeatures(labyPlayer, accountModel, economyEnabled, subtitleEnabled);
				},
				20L,
				20L
		);

		playerTasks.put(playerId, task);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		UUID playerId = event.getPlayer().getUniqueId();
		BukkitTask task = playerTasks.remove(playerId);
		if (task != null) {
			task.cancel();
		}
	}

	private void updateLabyModFeatures(LabyModPlayer labyPlayer, BankAccountModel accountModel, boolean economyEnabled, boolean subtitleEnabled) {

		if (economyEnabled && accountModel != null) {
			labyPlayer.updateBankEconomy(economy -> {
				economy.visible(true);
				economy.balance(accountModel.getBalance());

				String iconUrl = configuration.getEconomyIconUrl();
				if (iconUrl != null && !iconUrl.isEmpty()) {
					economy.iconUrl(iconUrl);
				}
			});
		}

		if (subtitleEnabled) {
			labyPlayer.updateSubtitle(subtitle -> {
				Component formattedText = ChatUtils.format(
						PlayerManager.getInstance().getOnlineMinetopiaPlayer(labyPlayer.getPlayer()),
						configuration.getSubtitleDisplay()
				);
				subtitle.text(ServerAPIComponent.text(ChatUtils.stripMiniMessage(formattedText)));
			});
		}
	}
}