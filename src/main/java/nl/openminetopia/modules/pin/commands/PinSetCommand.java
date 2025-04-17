package nl.openminetopia.modules.pin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import lombok.RequiredArgsConstructor;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.pin.PinModule;
import nl.openminetopia.modules.pin.enums.PinState;
import nl.openminetopia.modules.pin.objects.PinSession;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;

@CommandAlias("pin")
@RequiredArgsConstructor
public class PinSetCommand extends BaseCommand {

    private final PinModule pinModule;

    @Subcommand("set")
    @Syntax("<speler> <bedrag>")
    public void setPin(Player player, OnlinePlayer targetPlayer, double amount) {
        Player target = targetPlayer.getPlayer();

        if (target == null) {
            player.sendMessage(MessageConfiguration.component("player_not_found"));
            return;
        }

        if (amount <= 0) {
            player.sendMessage(ChatUtils.color("<red>Je pin bedrag moet minimaal 1 euro zijn."));
            return;
        }

        PinSession session = pinModule.createPinSession(player, target, amount);
        session.setState(PinState.CONSOLE);

        player.sendMessage(ChatUtils.color("<gold>Je hebt een betaalverzoek aangemaakt, klik op de pin console om deze te versturen naar <red>" + target.getName() + "<gold>."));
    }

}
