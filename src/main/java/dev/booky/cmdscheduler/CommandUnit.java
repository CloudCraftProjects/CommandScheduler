package dev.booky.cmdscheduler;
// Created by booky10 in CommandScheduler (1:01 PM 11.11.2025)

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.TimeUnit;

@NullMarked
public interface CommandUnit {

    static CommandUnit parse(String line) {
        line = line.trim();
        if (line.isEmpty()) {
            return EmptyCommandUnit.INSTANCE;
        } else if (MetaUnit.META_CHAR == line.charAt(0)) {
            int spaceIndex = line.indexOf(' ');
            String id = line.substring(0, spaceIndex);
            String argStr = line.substring(spaceIndex + 1);
            String[] args = StringUtils.split(argStr, ' ');
            return switch (id) {
                case "interval" -> IntervalMeta.parse(args);
                default -> throw new UnsupportedOperationException("Unsupported meta: " + id);
            };
        } else {
            return new ConsoleCommandUnit(line);
        }
    }

    void execute(Plugin plugin);

    boolean isEmpty();

    final class EmptyCommandUnit implements CommandUnit {

        public static final CommandUnit INSTANCE = new EmptyCommandUnit();

        private EmptyCommandUnit() {
        }

        @Override
        public void execute(Plugin plugin) {
            // NO-OP
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    }

    record ConsoleCommandUnit(String command) implements CommandUnit {

        @Override
        public void execute(Plugin plugin) {
            plugin.getSLF4JLogger().info("Executing scheduled command: {}", this.command);
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            Bukkit.dispatchCommand(console, this.command);
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

    }

    interface MetaUnit extends CommandUnit {

        char META_CHAR = '#';

        @Override
        default void execute(Plugin plugin) {
            throw new UnsupportedOperationException();
        }

        @Override
        default boolean isEmpty() {
            return true;
        }
    }

    record IntervalMeta(long interval, TimeUnit unit) implements MetaUnit {

        public static IntervalMeta parse(String[] args) {
            long interval = Long.parseLong(args[0]);
            TimeUnit unit = args.length > 1 ? TimeUnit.valueOf(args[1]) : TimeUnit.SECONDS;
            return new IntervalMeta(interval, unit);
        }
    }
}
