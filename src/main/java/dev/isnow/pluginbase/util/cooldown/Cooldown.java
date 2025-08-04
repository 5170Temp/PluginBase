package dev.isnow.pluginbase.util.cooldown;

import dev.isnow.pluginbase.util.DateUtil;
import lombok.Data;

import java.time.Clock;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Data
public class Cooldown<T> {
    private final long cooldownDurationMillis;
    private final ConcurrentHashMap<T, Long> cooldownStartTimes = new ConcurrentHashMap<>();
    private final Clock clock;

    public Cooldown(long duration, TimeUnit unit) {
        this(duration, unit, Clock.systemUTC());
    }

    public Cooldown(long duration, TimeUnit unit, Clock clock) {
        this.cooldownDurationMillis = unit.toMillis(duration);
        this.clock = clock;
    }

    public long getCooldownDurationMillis() {
        return cooldownDurationMillis;
    }

    public void addCooldown(T item) {
        if (item == null) {
            throw new NullPointerException("item is marked non-null but is null");
        }
        cooldownStartTimes.put(item, clock.millis());
    }

    public void removeCooldown(T item) {
        if (item == null) {
            throw new NullPointerException("item is marked non-null but is null");
        }
        cooldownStartTimes.remove(item);
    }

    public boolean isOnCooldown(T item) {
        if (item == null) {
            throw new NullPointerException("item is marked non-null but is null");
        }
        return getRemainingMillis(item).isPresent();
    }

    public Optional<Long> getRemainingMillis(T item) {
        if (item == null) {
            throw new NullPointerException("item is marked non-null but is null");
        }
        Long startTime = cooldownStartTimes.get(item);
        if (startTime == null) {
            return Optional.empty();
        }

        long elapsedTime = clock.millis() - startTime;

        if (elapsedTime >= cooldownDurationMillis) {
            cooldownStartTimes.remove(item);
            return Optional.empty();
        }

        return Optional.of(cooldownDurationMillis - elapsedTime);
    }

    public Optional<String> getRemainingFormatted(T item) {
        if (item == null) {
            throw new NullPointerException("item is marked non-null but is null");
        }
        return getRemainingMillis(item).map(this::formatElapsedTime);
    }

    private String formatElapsedTime(long millis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long hours = TimeUnit.MILLISECONDS.toHours(millis);

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0 || (hours == 0 && minutes == 0)) sb.append(seconds).append("s");

        return sb.toString().trim();
    }
}
