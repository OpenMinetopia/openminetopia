package nl.openminetopia.registry;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import com.jazzkuh.modulemanager.common.ModuleManager;
import com.jazzkuh.modulemanager.common.modules.components.IComponentHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandComponentRegistry implements IComponentHandler<BaseCommand> {

    private final PaperCommandManager commandManager;

    @Override
    public void onLoad(ModuleManager moduleManager, BaseCommand baseCommand) {

    }

    @Override
    public void onEnable(ModuleManager moduleManager, BaseCommand baseCommand) {
        commandManager.registerCommand(baseCommand);
    }

    @Override
    public void onDisable(ModuleManager moduleManager, BaseCommand baseCommand) {

    }
}