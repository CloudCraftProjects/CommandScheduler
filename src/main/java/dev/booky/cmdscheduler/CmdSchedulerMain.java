package dev.booky.cmdscheduler;
// Created by booky10 in CommandScheduler (15:30 25.10.2025)

import dev.booky.cloudcore.config.ConfigurateLoader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static io.papermc.paper.util.Tick.tick;

@NullMarked
public class CmdSchedulerMain extends JavaPlugin {

    @Override
    public void onEnable() {
        this.reloadTasks();
    }

    public void reloadTasks() {
        CmdSchedulerConfig config = this.loadConfig();
        Bukkit.getScheduler().cancelTasks(this);
        this.registerFixedTasks(config.getFixed());
    }

    private void registerFixedTasks(Map<Long, List<String>> tasks) {
        for (Map.Entry<Long, List<String>> entry : tasks.entrySet()) {
            Instant instant = Instant.ofEpochMilli(entry.getKey());
            this.registerFixedTask(instant, entry.getValue());
        }
    }

    private void registerFixedTask(Instant time, List<String> commands) {
        Instant now = Instant.now();
        if (now.isAfter(time)) {
            return; // time has passed, ignore
        }
        Duration delay = Duration.between(now, time);
        Bukkit.getScheduler().runTaskLater(this,
                new CmdSchedulerTask(commands),
                tick().fromDuration(delay));

        this.getSLF4JLogger().info("Registered {} command(s) to execute in {}: {}",
                commands.size(), delay, commands);
    }

    private CmdSchedulerConfig loadConfig() {
        Path configPath = this.getDataPath().resolve("config.yml");
        return ConfigurateLoader.yamlLoader().build()
                .loadObject(configPath, CmdSchedulerConfig.class, CmdSchedulerConfig::new);
    }
}
