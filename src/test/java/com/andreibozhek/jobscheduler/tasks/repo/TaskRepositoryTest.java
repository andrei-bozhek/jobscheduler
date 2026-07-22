package com.andreibozhek.jobscheduler.tasks.repo;

import com.andreibozhek.jobscheduler.IntegrationTestBase;
import com.andreibozhek.jobscheduler.tasks.domain.Task;
import com.andreibozhek.jobscheduler.tasks.domain.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TaskRepositoryTest extends IntegrationTestBase {

    @Autowired
    TaskRepository repo;

    @Test
    void requeueExpiredRunningTaskReturnsExpiredRunningTaskToPending() {
        UUID taskId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        Task task = new Task(
                taskId,
                "echo",
                "{\"message\":\"hello\"}",
                TaskStatus.RUNNING,
                now.minusMinutes(5),
                1,
                3,
                null,
                "worker-old",
                now.minusMinutes(1),
                now,
                now
        );

        repo.insert(task);

        int requeued = repo.requeueExpiredRunningTasks();

        Task updated = repo.findByID(taskId).orElseThrow();

        assertThat(requeued).isEqualTo(1);
        assertThat(updated.status()).isEqualTo(TaskStatus.PENDING);
        assertThat(updated.lockedBy()).isNull();
        assertThat(updated.lockedUntil()).isNull();
        assertThat(updated.error()).isEqualTo("Task lease expired");
        assertThat(updated.attempt()).isEqualTo(1);

    }
}
