package dev.booky.cmdscheduler;
// Created by booky10 in CommandScheduler (15:33 25.10.2025)

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public final class CmdSchedulerTask implements Runnable {

    private final List<String> commands;

    public CmdSchedulerTask(List<String> commands) {
        this.commands = commands;
    }

    @Override
    public void run() {
        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        for (String command : this.commands) {
            Bukkit.dispatchCommand(sender, command);
        }
    }
}
