package dev.booky.cmdscheduler;
// Created by booky10 in CommandScheduler (15:33 25.10.2025)

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public final class CmdSchedulerTask implements Runnable {

    private final Plugin plugin;
    private final List<String> commands;

    public CmdSchedulerTask(Plugin plugin, List<String> commands) {
        this.plugin = plugin;
        this.commands = List.copyOf(commands);
    }

    @Override
    public void run() {
        // switch from async scheduler to main thread
        Bukkit.getScheduler().runTask(this.plugin, this::execute);
    }

    public void execute() {
        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        for (String command : this.commands) {
            this.plugin.getSLF4JLogger().info("Executing scheduled command: {}", command);
            Bukkit.dispatchCommand(sender, command);
        }
    }
}
