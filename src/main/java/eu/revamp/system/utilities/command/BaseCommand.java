package eu.revamp.system.utilities.command;


import eu.revamp.system.plugin.RevampSystem;

public abstract class BaseCommand {
    public RevampSystem plugin = RevampSystem.INSTANCE;

    public BaseCommand() {
        plugin.getCommandFramework().registerCommands(this, null);
    }

    public abstract void onCommand(CommandArgs command);
}
