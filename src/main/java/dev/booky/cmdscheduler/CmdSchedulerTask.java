package dev.booky.cmdscheduler;
// Created by booky10 in CommandScheduler (15:33 25.10.2025)

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public final class CmdSchedulerTask implements Runnable {

    private final Plugin plugin;
    private final List<CommandUnit> units;

    public CmdSchedulerTask(Plugin plugin, List<CommandUnit> units) {
        this.plugin = plugin;
        this.units = units.stream().filter(unit -> !unit.isEmpty()).toList();
    }

    @Override
    public void run() {
        // switch from async scheduler to main thread
        Bukkit.getScheduler().runTask(this.plugin, this::execute);
    }

    public void execute() {
        for (CommandUnit unit : this.units) {
            unit.execute(this.plugin);
        }
    }
}
