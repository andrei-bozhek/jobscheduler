package com.andreibozhek.jobscheduler.tasks.worker;

import com.andreibozhek.jobscheduler.tasks.domain.Task;
import com.andreibozhek.jobscheduler.tasks.handler.TaskHandler;
import com.andreibozhek.jobscheduler.tasks.repo.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Background worker that executes scheduled tasks.
 * <p>
 * The worker wakes up on a fixed delay, returns expired RUNNING tasks back to
 * the queue, claims due PENDING tasks, and executes each claimed task through
 * a matching TaskHandler.
 * <p>
 * The worker is disabled in integration tests with worker.enabled=false so
 * tests can control task state directly.
 */
@Component
@ConditionalOnProperty(
        name = "worker.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class TaskWorker {
    private static final Logger log = LoggerFactory.getLogger(TaskWorker.class);

    private final TaskRepository repo;
    private final String workerId = "worker-" + UUID.randomUUID();
    private final Map<String, TaskHandler> handlers;

    /**
     * Builds the worker and creates a lookup map from task type to handler.
     * <p>
     * Spring injects all TaskHandler beans. The map lets the worker find the right
     * handler for each task type without hard-coded if statements.
     */
    public TaskWorker(TaskRepository repo, List<TaskHandler> handlers) {
        this.repo = repo;
        this.handlers = handlers.stream()
                .collect(Collectors.toMap(
                        TaskHandler::type,
                        handler -> handler
                ));
    }

    /**
     * Runs one worker cycle.
     * <p>
     * First, expired RUNNING tasks are requeued. Then this worker claims a small
     * batch of due PENDING tasks and executes them one by one.
     */
    @Scheduled(fixedDelayString = "${worker.fixedDelayMs:1000}")
    public void tick() {
        int requeued = repo.requeueExpiredRunningTasks();

        if (requeued > 0) {
            log.warn("[{}] Requeued {} expired running tasks", workerId, requeued);
        }

        List<Task> claimed = repo.claimDueTasks(workerId, 10, 30);
        for (Task t : claimed) {
            executeOne(t);
        }
    }

    /**
     * Executes one claimed task and records the result.
     * <p>
     * The method starts an attempt record before running the handler. If the
     * handler finishes without an exception, the attempt is marked as successful
     * and the task becomes DONE. If the handler throws an exception, the task is
     * either retried later or marked as FAILED.
     */
    private void executeOne(Task t) {
        int attempt = t.attempt();
        repo.insertAttemptStart(t.id(), attempt);

        try {
            TaskHandler handler = handlers.get(t.type());
            if (handler == null) {
                throw new IllegalArgumentException("Unknown task type: " + t.type());
            }

            handler.handle(t);

            repo.finishAttempt(t.id(), attempt, "SUCCESS", null);
            repo.markDone(t.id());

        } catch (Exception ex) {
            String msg = ex.getClass().getSimpleName() + ": " + ex.getMessage();
            log.warn("[{}] Task {} failed {}", workerId, t.id(), msg);
            repo.markFailedOrRetry(t.id(), attempt, t.maxAttempts(), msg);
        }

    }

}