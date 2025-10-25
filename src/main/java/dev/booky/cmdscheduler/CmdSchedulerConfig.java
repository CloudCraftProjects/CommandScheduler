package dev.booky.cmdscheduler;
// Created by booky10 in CommandScheduler (15:31 25.10.2025)

import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked
@ConfigSerializable
public class CmdSchedulerConfig {

    private Map<Long, List<String>> fixed = new HashMap<>();

    public Map<Long, List<String>> getFixed() {
        return this.fixed;
    }
}
