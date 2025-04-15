package nl.openminetopia.modules.pin.listeners;

import lombok.RequiredArgsConstructor;
import nl.openminetopia.modules.pin.PinModule;
import nl.openminetopia.modules.pin.enums.PinState;
import nl.openminetopia.modules.pin.menu.PinSelectAccountMenu;
import nl.openminetopia.modules.pin.objects.PinSession;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

@RequiredArgsConstructor
public class PinInteractionListener implements Listener {

    private final PinModule pinModule;

    @EventHandler
    public void onPinInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        PinSession session = pinModule.getPinSessionFromPlayer(player);
        if (session == null) return;

        if (session.getState() != PinState.CONSOLE) return;

        if (!session.getTarget().isOnline()) {
            player.sendMessage("Target no longer online, aborting.");
            pinModule.getPinSessions().remove(session);
            return;
        }

        session.setConsole(event.getInteractionPoint());
        session.setState(PinState.ACCOUNT);

        player.sendMessage("Pin console geselecteerd, kies nu een rekening.");
        new PinSelectAccountMenu(player, session).open(player);
    }

}
