package io.github.simonalexs.tools;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduleUtil {
    public static void runPeriodically(Runnable runnable, int intervalMs, int keepMs) {
        long endTime = System.currentTimeMillis() + keepMs;

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, intervalMs, TimeUnit.MILLISECONDS);

        Runnable shutdownRunnable = () -> {
            while (!service.isShutdown() && !service.isTerminated()) {
                if (System.currentTimeMillis() >= endTime) {
                    service.shutdown();
                    return;
                }
            }
        };
        new Thread(shutdownRunnable).start();
    }
}
