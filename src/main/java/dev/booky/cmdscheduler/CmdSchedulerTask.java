package dev.booky.cmdscheduler;
// Created by booky10 in CommandScheduler (15:33 25.10.2025)

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;

import java.util.List;

@NullMarked
public final class CmdSchedulerTask implements Runnable {

    private final Logger logger;
    private final List<String> commands;

    public CmdSchedulerTask(Logger logger, List<String> commands) {
        this.logger = logger;
        this.commands = commands;
    }

    @Override
    public void run() {
        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        for (String command : this.commands) {
            this.logger.info("Executing scheduled command: {}", command);
            Bukkit.dispatchCommand(sender, command);
        }
    }
}
