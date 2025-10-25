package dev.booky.cmdscheduler;
// Created by booky10 in CommandScheduler (15:30 25.10.2025)

import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CmdSchedulerMain extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getLogger().info("Hello world!");
    }
}
