package dev.isnow.pluginbase.util;

import dev.isnow.pluginbase.PluginBase;
import lombok.Getter;
import org.hibernate.Session;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter
public final class ExpiringSession implements AutoCloseable {
    private final Session session;
    private final int delay;
    private ScheduledFuture<?> scheduledFuture;

    public ExpiringSession(final Session session) {
        this.session = session;
        this.delay = 5;

        scheduleSessionClose();
    }

    public ExpiringSession(final Session session, final int delay) {
        this.session = session;
        this.delay = delay;

        scheduleSessionClose();
    }

    private void scheduleSessionClose() {
        scheduledFuture = PluginBase.getInstance().getScheduler().schedule(() -> {
            if (session.isOpen()) {
                session.close();
                BaseLogger.debug("Closed session due to expiration.");
            }
        }, delay, TimeUnit.SECONDS);
    }

    public void closeSession() {
        if (session.isOpen()) {
            session.close();

            scheduledFuture.cancel(true);
        }
    }

    public boolean isOpen() {
        return session.isOpen();
    }

    @Override
    public void close() throws Exception {
        closeSession();
    }
}
