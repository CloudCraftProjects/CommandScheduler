package dev.booky.cmdscheduler;
// Created by booky10 in CommandScheduler (15:30 25.10.2025)

import dev.booky.cloudcore.config.ConfigurateLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@NullMarked
public class CmdSchedulerMain extends JavaPlugin {

    private final CmdSchedulerCommand command = new CmdSchedulerCommand(this);

    // schedule tasks using a async executor service running independently
    // of the main server thread, as this plugin shouldn't be bothered by lag
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private final List<ScheduledFuture<?>> tasks = new ArrayList<>();

    @Override
    public void onEnable() {
        this.reloadTasks();
        this.command.register();
    }

    @Override
    public void onDisable() {
        this.command.unregister();
        this.cancelTasks();
        this.service.shutdownNow();
    }

    public void cancelTasks() {
        for (ScheduledFuture<?> task : this.tasks) {
            task.cancel(false);
        }
        this.tasks.clear();
    }

    public void reloadTasks() {
        this.cancelTasks();
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
        if (commands.isEmpty()) {
            return; // useless task, don't register
        }
        Instant now = Instant.now();
        if (now.isAfter(time)) {
            return; // time has passed, ignore
        }
        Duration delay = Duration.between(now, time);
        ScheduledFuture<?> task = this.service.schedule(new CmdSchedulerTask(this, commands),
                delay.toMillis(), TimeUnit.MILLISECONDS);
        this.tasks.add(task);

        this.getSLF4JLogger().info("Registered {} command(s) to execute in {}: {}",
                commands.size(), delay, commands);
    }

    private CmdSchedulerConfig loadConfig() {
        Path configPath = this.getDataPath().resolve("config.yml");
        return ConfigurateLoader.yamlLoader().build()
                .loadObject(configPath, CmdSchedulerConfig.class, CmdSchedulerConfig::new);
    }
}
