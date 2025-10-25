package dev.booky.cmdscheduler;
// Created by booky10 in CommandScheduler (15:40 25.10.2025)

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;

@NullMarked
public final class CmdSchedulerCommand {

    private static final String[] ALIASES = {"commandscheduler", "cmdscheduler", "cmdsched"};

    private final CmdSchedulerMain plugin;

    public CmdSchedulerCommand(CmdSchedulerMain plugin) {
        this.plugin = plugin;
    }

    public void register() {
        this.unregister();
        new CommandTree(ALIASES[0])
                .withAliases(Arrays.copyOfRange(ALIASES, 1, ALIASES.length))
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
        for (String alias : ALIASES) {
            CommandAPI.unregister(alias, true);
        }
    }
}
