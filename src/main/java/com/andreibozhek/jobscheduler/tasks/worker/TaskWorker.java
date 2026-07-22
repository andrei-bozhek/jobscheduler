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

    public TaskWorker(TaskRepository repo, List<TaskHandler> handlers) {
        this.repo = repo;
        this.handlers = handlers.stream()
                .collect(Collectors.toMap(
                        handler -> handler.type(),
                        handler -> handler
                ));
    }

    @Scheduled(fixedDelayString = "${worker.fixedDelayMs:1000}")
    public void tick() {
        List<Task> claimed = repo.claimDueTasks(workerId, 10, 30);
        for (Task t : claimed) {
            executeOne(t);
        }
    }

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