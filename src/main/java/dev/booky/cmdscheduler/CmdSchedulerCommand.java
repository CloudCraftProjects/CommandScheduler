package dev.booky.cmdscheduler;
// Created by booky10 in CommandScheduler (15:40 25.10.2025)

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CmdSchedulerCommand {

    private static final String LABEL = "cmdsched";

    private final CmdSchedulerMain plugin;

    public CmdSchedulerCommand(CmdSchedulerMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        new CommandTree(LABEL)
                .withPermission("commandscheduler.command")
                .then(new LiteralArgument("reload")
                        .withPermission("commandscheduler.command.reload")
                        .executesNative((sender, args) -> {
                            this.plugin.reloadTasks();
                            sender.sendPlainMessage("CommandScheduler has been reloaded from file");
                        }))
                .register(this.plugin);
    }

    public void unregister() {
        CommandAPI.unregister(LABEL, true);
    }
}
