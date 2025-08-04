package dev.isnow.pluginbase.util.cooldown;

import dev.isnow.pluginbase.util.DateUtil;
import lombok.Data;

import java.time.Clock;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Data
public class Cooldown<T> {
    private final long cooldownDurationMillis;
    private final ConcurrentHashMap<T, Long> cooldownStartTimes = new ConcurrentHashMap<>();
    private final Clock clock;

    public Cooldown(final long duration, final TimeUnit unit) {
        this(duration, unit, Clock.systemUTC());
    }

    public Cooldown(final long duration, final TimeUnit unit, final Clock clock) {
        this.cooldownDurationMillis = unit.toMillis(duration);
        this.clock = clock;
    }

    public void addCooldown(final T item) {
        cooldownStartTimes.put(item, clock.millis());
    }

    public void removeCooldown(final T item) {
        cooldownStartTimes.remove(item);
    }

    public boolean isOnCooldown(final T item) {
        return getRemainingMillis(item).isPresent();
    }

    public Optional<Long> getRemainingMillis(final T item) {
        final Long startTime = cooldownStartTimes.get(item);
        if (startTime == null) {
            return Optional.empty();
        }

        final long elapsedTime = clock.millis() - startTime;

        if (elapsedTime >= cooldownDurationMillis) {
            cooldownStartTimes.remove(item);
            return Optional.empty();
        }

        return Optional.of(cooldownDurationMillis - elapsedTime);
    }

    public Optional<String> getRemainingFormatted(final T item) {
        return getRemainingMillis(item).map(DateUtil::formatElapsedTime);
    }
}
