package com.andreibozhek.jobscheduler.tasks.worker;

import com.andreibozhek.jobscheduler.tasks.domain.Task;
import com.andreibozhek.jobscheduler.tasks.repo.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class TaskWorker {
    private static final Logger log = LoggerFactory.getLogger(TaskWorker.class);

    private final TaskRepository repo;
    private final String workerId = "worker-" + UUID.randomUUID();

    public TaskWorker(TaskRepository repo) {
        this.repo = repo;
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
            if ("echo".equalsIgnoreCase(t.type())) {
                log.info("[{}] ECHO task {} payload {}", workerId, t.id(), t.payloadJson());
                repo.finishAttempt(t.id(), attempt, "SUCCESS", null);
                repo.markDone(t.id());
                return;
            }

            throw new IllegalArgumentException("Unknown task type: " + t.type());

        } catch (Exception ex) {
            String msg = ex.getClass().getSimpleName() + ": " + ex.getMessage();
            log.warn("[{}] Task {} failed {}", workerId, t.id(), msg);
            repo.markFailedOrRetry(t.id(), attempt, t.maxAttempts(), msg);
        }

    }

}