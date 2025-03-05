package nl.openminetopia.utils;

import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.color.enums.OwnableColorType;
import nl.openminetopia.modules.police.utils.BalaclavaUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

@UtilityClass
public class ChatUtils {

    public Component color(String message) {
        return MiniMessage.miniMessage().deserialize(message);
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public Component format(MinetopiaPlayer minetopiaPlayer, String message) {
        Player player = minetopiaPlayer.getBukkit().getPlayer();
        if (player == null) return Component.empty();

        int levelUps = minetopiaPlayer.getCalculatedLevel() - minetopiaPlayer.getLevel();

        message = message
                .replace("<player>", BalaclavaUtils.isWearingBalaclava(player) ? "<obf>Balaclava</obf><reset>" : player.getName())
                .replace("<level_color>", minetopiaPlayer.getActiveColor(OwnableColorType.LEVEL).color())
                .replace("<level>", minetopiaPlayer.getLevel() + "")
                .replace("<calculated_level>", minetopiaPlayer.getCalculatedLevel() + "")
                .replace("<levelups>", levelUps == 0 ? "<gold>0" : (levelUps > 0 ? "<green>+" + levelUps : "<red>" + levelUps))
                .replace("<prefix_color>", minetopiaPlayer.getActiveColor(OwnableColorType.PREFIX).color())
                .replace("<prefix>", minetopiaPlayer.getActivePrefix().getPrefix())
                .replace("<name_color>", minetopiaPlayer.getActiveColor(OwnableColorType.NAME).color())
                .replace("<display_name>", BalaclavaUtils.isWearingBalaclava(player) ? "<obf>Balaclava</obf><reset>" : ChatUtils.stripMiniMessage(player.displayName()))
                .replace("<chat_color>", minetopiaPlayer.getActiveColor(OwnableColorType.CHAT).color())
                .replace("<date>", dateFormat.format(new Date()))
                .replace("<time>", timeFormat.format(new Date()))
                .replace("<new_line>", "\n");

        if (minetopiaPlayer.isInPlace()) {
            message = message
                    .replace("<world_title>", minetopiaPlayer.getWorld().getTitle())
                    .replace("<world_loadingname>", minetopiaPlayer.getWorld().getLoadingName())
                    .replace("<world_name>", minetopiaPlayer.getWorld().getName())
                    .replace("<world_color>", minetopiaPlayer.getWorld().getColor())
                    .replace("<city_title>", minetopiaPlayer.getPlace().getTitle()) // Defaults to the world name if the player is not in a city
                    .replace("<city_loadingname>", minetopiaPlayer.getPlace().getLoadingName()) // Defaults to the world loading name if the player is not in a city
                    .replace("<city_name>", minetopiaPlayer.getPlace().getName()) // Defaults to the world name if the player is not in a city
                    .replace("<temperature>", minetopiaPlayer.getPlace().getTemperature() + "") // Defaults to the world temperature if the player is not in a city
                    .replace("<city_color>", minetopiaPlayer.getPlace().getColor()); // Defaults to the world color if the player is not in a city
        }

        if (minetopiaPlayer.getFitness().getStatistics() != null && !minetopiaPlayer.getFitness().getStatistics().isEmpty()) {
            message = message
                    .replace("<fitness>", minetopiaPlayer.getFitness().getTotalFitness() + "")
                    .replace("<max_fitness>", OpenMinetopia.getFitnessConfiguration().getMaxFitnessLevel() + "");
        }

        BankingModule bankingModule = OpenMinetopia.getModuleManager().get(BankingModule.class);
        BankAccountModel accountModel = bankingModule.getAccountById(player.getUniqueId());
        if (accountModel != null) {
            message = message.replace("<balance_formatted>", bankingModule.format(accountModel.getBalance())
                             .replace("<balance>", String.valueOf(accountModel.getBalance())));
        }

        if (OpenMinetopia.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }

        return MiniMessage.miniMessage().deserialize(message);
    }

    public void sendMessage(Player player, String message) {
        Component component = color(message.replaceFirst("\\[(title|action)]", ""));
        decideMessage(player, component, message);
    }

    public void sendMessage(CommandSender sender, String message) {
        Component component = color(message.replaceFirst("\\[(title|action)]", ""));
        sender.sendMessage(component);
    }

    public void sendFormattedMessage(MinetopiaPlayer minetopiaPlayer, String message) {
        Component component = format(minetopiaPlayer,
                message.replaceFirst("\\[(title|action)]", ""));

        Player player = minetopiaPlayer.getBukkit().getPlayer();
        if (player == null) return;

        decideMessage(player, component, message);
    }

    public String stripMiniMessage(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    public String rawMiniMessage(Component component) {
        String message = stripMiniMessage(component);
        return MiniMessage.miniMessage().stripTags(message);
    }

    private void decideMessage(Player player, Component component, String message) {
        if (message.startsWith("[title]")) {
            player.showTitle(Title.title(component, Component.empty()));
        } else if (message.startsWith("[action]")) {
            player.sendActionBar(component);
        } else {
            player.sendMessage(component);
        }
    }

    public String componentToHex(Component component) {
        TextColor color = component.color();
        if (color == null) return "#FFFFFF";

        return color.asHexString();
    }
}
