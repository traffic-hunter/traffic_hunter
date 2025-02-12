package ygo.traffic_hunter.core.schedule;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author JuSeong
 * @version 1.1.0
 */

@Getter
@RequiredArgsConstructor
public class Scheduler {

    private final ScheduledExecutorService executor;

    private ScheduledFuture<?> currentTask;

    public void schedule(final int interval, final Runnable runnable) {
        if (currentTask != null && !currentTask.isCancelled()) {
            currentTask.cancel(true);
        }
        currentTask = executor.scheduleWithFixedDelay(runnable,
                0,
                interval,
                TimeUnit.MILLISECONDS
        );
    }

}
