package dev.booky.cmdscheduler;
// Created by booky10 in CommandScheduler (15:30 25.10.2025)

import dev.booky.cloudcore.config.ConfigurateLoader;
import io.papermc.paper.util.Tick;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@NullMarked
public class CmdSchedulerMain extends JavaPlugin {

    @Override
    public void onEnable() {
        CmdSchedulerConfig config = this.loadConfig();
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
        long delayTicks = Tick.tick().between(Instant.now(), time);
        CmdSchedulerTask task = new CmdSchedulerTask(commands);
        Bukkit.getScheduler().runTaskLater(this, task, delayTicks);
    }

    private CmdSchedulerConfig loadConfig() {
        Path configPath = this.getDataPath().resolve("config.yml");
        return ConfigurateLoader.yamlLoader().build()
                .loadObject(configPath, CmdSchedulerConfig.class, CmdSchedulerConfig::new);
    }
}
