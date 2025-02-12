package nl.openminetopia.modules.misc;

import nl.openminetopia.modules.Module;
import nl.openminetopia.modules.misc.commands.HeadCommand;
import nl.openminetopia.modules.misc.listeners.PlayerInteractListener;

public class MiscModule extends Module {

    @Override
    public void enable() {
        registerCommand(new HeadCommand());

        registerListener(new PlayerInteractListener());
    }

    @Override
    public void disable() {

    }
}
