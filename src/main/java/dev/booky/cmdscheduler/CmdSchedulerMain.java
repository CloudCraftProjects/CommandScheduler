package dev.booky.cmdscheduler;
// Created by booky10 in CommandScheduler (15:30 25.10.2025)

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import dev.booky.cloudcore.config.ConfigurateLoader;
import dev.booky.cmdscheduler.CommandUnit.IntervalMeta;
import dev.booky.cmdscheduler.CommandUnit.MetaUnit;
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
            List<CommandUnit> units = entry.getValue().stream().map(CommandUnit::parse).toList();
            ClassToInstanceMap<MetaUnit> meta = this.extractMetaUnits(units);
            this.registerFixedTask(instant, units, meta);
        }
    }

    private ClassToInstanceMap<MetaUnit> extractMetaUnits(List<CommandUnit> units) {
        ClassToInstanceMap<MetaUnit> meta = MutableClassToInstanceMap.create();
        for (CommandUnit unit : units) {
            if (unit instanceof MetaUnit metaUnit) {
                @SuppressWarnings("unchecked")
                Class<MetaUnit> clazz = (Class<MetaUnit>) metaUnit.getClass();
                meta.putInstance(clazz, metaUnit);
            }
        }
        return ImmutableClassToInstanceMap.copyOf(meta);
    }

    private void registerFixedTask(Instant time, List<CommandUnit> units, ClassToInstanceMap<MetaUnit> meta) {
        if (units.isEmpty()) {
            return; // useless task, don't register
        }
        CmdSchedulerTask runnable = new CmdSchedulerTask(this, units);

        // check whether it's a repeating task or not
        Instant now = Instant.now();
        Duration period;
        if (meta.getInstance(IntervalMeta.class) instanceof IntervalMeta(long interval, TimeUnit unit)) {
            period = Duration.of(interval, unit.toChronoUnit());
            // if the start timestamp as passed, move it to the next possible iteration
            if (now.isAfter(time)) {
                long count = Duration.between(time, now).dividedBy(period);
                time = time.plus(period.multipliedBy(count + 1L));
            }
        } else {
            period = Duration.ZERO;
        }

        if (now.isAfter(time)) {
            return; // time has passed, ignore
        }

        // schedule task in execution service
        Duration delay = Duration.between(now, time);
        ScheduledFuture<?> task;
        if (period.isPositive()) {
            task = this.service.scheduleAtFixedRate(runnable, delay.toMillis(),
                    period.toMillis(), TimeUnit.MILLISECONDS);
        } else {
            task = this.service.schedule(runnable, delay.toMillis(), TimeUnit.MILLISECONDS);
        }
        this.tasks.add(task);

        this.getSLF4JLogger().info("Registered {} command(s) to execute in {}: {}",
                units.size(), delay, units);
    }

    private CmdSchedulerConfig loadConfig() {
        Path configPath = this.getDataPath().resolve("config.yml");
        return ConfigurateLoader.yamlLoader().build()
                .loadObject(configPath, CmdSchedulerConfig.class, CmdSchedulerConfig::new);
    }
}
