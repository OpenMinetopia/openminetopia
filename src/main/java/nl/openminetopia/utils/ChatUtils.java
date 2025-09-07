package nl.openminetopia.utils;

import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import nl.openminetopia.OpenMinetopia;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.color.enums.OwnableColorType;
import nl.openminetopia.modules.currencies.CurrencyModule;
import nl.openminetopia.modules.currencies.models.CurrencyModel;
import nl.openminetopia.modules.fitness.FitnessModule;
import nl.openminetopia.modules.police.utils.BalaclavaUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@UtilityClass
public class ChatUtils {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private static String stripSectionCodes(String s) {
        int n = s.length();
        StringBuilder out = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (c == '§' && i + 1 < n) { i++; continue; } // skip code
            out.append(c);
        }
        return out.toString();
    }

    public Component color(String message) {
        String noLegacy = stripSectionCodes(message);
        return MM.deserialize(noLegacy).decoration(TextDecoration.ITALIC, false);
    }

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static String nowDate() { return LocalDate.now().format(DATE_FMT); }
    private static String nowTime() { return LocalTime.now().format(TIME_FMT); }

    public Component format(MinetopiaPlayer mtp, String template) {
        Player player = mtp.getBukkit().getPlayer();
        if (player == null) return Component.empty();

        final var modules = OpenMinetopia.getModuleManager();
        final CurrencyModule currencyModule = modules.get(CurrencyModule.class);
        final FitnessModule fitnessModule   = modules.get(FitnessModule.class);
        final BankingModule bankingModule   = modules.get(BankingModule.class);

        boolean balaclava = BalaclavaUtils.isWearingBalaclava(player);
        String displayName = balaclava ? "<obf>Balaclava</obf><reset>" : stripMiniMessage(player.displayName());
        String namePlain   = balaclava ? "<obf>Balaclava</obf><reset>" : player.getName();

        int levelUps = mtp.getCalculatedLevel() - mtp.getLevel();

        String msg = template;

        // vaste placeholders
        msg = msg
                .replace("<player>", namePlain)
                .replace("<level_color>", mtp.getActiveColor(OwnableColorType.LEVEL).color())
                .replace("<level>", Integer.toString(mtp.getLevel()))
                .replace("<calculated_level>", Integer.toString(mtp.getCalculatedLevel()))
                .replace("<levelups>", levelUps == 0 ? "<gold>0" : (levelUps > 0 ? "<green>+" + levelUps : "<red>" + levelUps))
                .replace("<prefix_color>", mtp.getActiveColor(OwnableColorType.PREFIX).color())
                .replace("<prefix>", mtp.getActivePrefix().getPrefix())
                .replace("<name_color>", mtp.getActiveColor(OwnableColorType.NAME).color())
                .replace("<display_name>", displayName)
                .replace("<chat_color>", mtp.getActiveColor(OwnableColorType.CHAT).color())
                .replace("<date>", nowDate())
                .replace("<time>", nowTime())
                .replace("<new_line>", "\n");

        // currencies (null-safe)
        var list = currencyModule.getCurrencyModels().get(player.getUniqueId());
        if (list != null) {
            for (CurrencyModel c : list) {
                msg = msg.replace("<currency_" + c.getName() + ">", String.valueOf(c.getBalance()));
            }
        }

        // place/world (alleen als inPlace)
        if (mtp.isInPlace()) {
            var w = mtp.getWorld();
            var p = mtp.getPlace();
            msg = msg
                    .replace("<world_title>", w.getTitle())
                    .replace("<world_loadingname>", w.getLoadingName())
                    .replace("<world_name>", w.getName())
                    .replace("<world_color>", w.getColor())
                    .replace("<city_title>", p.getTitle())
                    .replace("<city_loadingname>", p.getLoadingName())
                    .replace("<city_name>", p.getName())
                    .replace("<temperature>", Double.toString(p.getTemperature()))
                    .replace("<city_color>", p.getColor());
        }

        // fitness
        var stats = mtp.getFitness().getStatistics();
        if (stats != null && !stats.isEmpty()) {
            msg = msg
                    .replace("<fitness>", Integer.toString(mtp.getFitness().getTotalFitness()))
                    .replace("<max_fitness>", Integer.toString(fitnessModule.getConfiguration().getMaxFitnessLevel()));
        }

        // banking
        var account = bankingModule.getAccountById(player.getUniqueId());
        if (account != null) {
            String formatted = bankingModule.format(account.getBalance());
            msg = msg
                    .replace("<balance_formatted>", formatted)
                    .replace("<balance>", String.valueOf(account.getBalance()));
        }

        // PlaceholderAPI alleen indien aanwezig en nodig
        if (hasPlaceholderAPI() && msg.indexOf('%') >= 0) {
            msg = PlaceholderAPI.setPlaceholders(player, msg);
        }

        return color(msg); // gebruikt MM (single) en snelle stripSectionCodes
    }


    private static boolean hasPlaceholderAPI() {
        return OpenMinetopia.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
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
        return MM.serialize(component);
    }

    public String stripMiniMessage(String message) {
        return MM.stripTags(message);
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
