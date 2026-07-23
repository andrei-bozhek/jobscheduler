package com.andreibozhek.jobscheduler.tasks.repo;

import com.andreibozhek.jobscheduler.IntegrationTestBase;
import com.andreibozhek.jobscheduler.tasks.domain.Task;
import com.andreibozhek.jobscheduler.tasks.domain.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.List;
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

    @Test
    void requeueExpiredRunningTasksKeepsActiveRunningTaskLocked() {
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
                "worker-active",
                now.plusMinutes(1),
                now,
                now
        );

        repo.insert(task);

        int requeued = repo.requeueExpiredRunningTasks();

        Task updated = repo.findByID(taskId).orElseThrow();

        assertThat(requeued).isZero();
        assertThat(updated.status()).isEqualTo(TaskStatus.RUNNING);
        assertThat(updated.lockedBy()).isEqualTo("worker-active");
        assertThat(updated.lockedUntil()).isNotNull();
        assertThat(updated.attempt()).isEqualTo(1);
    }

    @Test
    void claimDueTasksMarksPendingTaskAsRunning() {
        UUID taskId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        Task task = new Task(
                taskId,
                "echo",
                "{\"message\":\"hello\"}",
                TaskStatus.PENDING,
                now.minusMinutes(1),
                0,
                3,
                null,
                null,
                null,
                now,
                now
        );

        repo.insert(task);

        List<Task> claimed = repo.claimDueTasks("worker-test", 10, 30);

        Task updated = repo.findByID(taskId).orElseThrow();

        assertThat(claimed).hasSize(1);
        assertThat(claimed.getFirst().id()).isEqualTo(taskId);
        assertThat(updated.status()).isEqualTo(TaskStatus.RUNNING);
        assertThat(updated.lockedBy()).isEqualTo("worker-test");
        assertThat(updated.lockedUntil()).isNotNull();
        assertThat(updated.attempt()).isEqualTo(1);
    }

    @Test
    void claimDueTasksSkipsFutureTask() {
        UUID taskId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        Task task = new Task(
                taskId,
                "echo",
                "{\"message\":\"hello\"}",
                TaskStatus.PENDING,
                now.plusMinutes(10),
                0,
                3,
                null,
                null,
                null,
                now,
                now
        );

        repo.insert(task);

        List<Task> claimed = repo.claimDueTasks("worker-test", 10, 30);

        Task updated = repo.findByID(taskId).orElseThrow();

        assertThat(claimed).isEmpty();
        assertThat(updated.status()).isEqualTo(TaskStatus.PENDING);
        assertThat(updated.lockedBy()).isNull();
        assertThat(updated.lockedUntil()).isNull();
        assertThat(updated.attempt()).isZero();
    }

    @Test
    void claimDueTasksRespectsBatchSize() {
        OffsetDateTime now = OffsetDateTime.now();

        Task firstTask = new Task(
                UUID.randomUUID(),
                "echo",
                "{\"message\":\"first\"}",
                TaskStatus.PENDING,
                now.minusMinutes(1),
                0,
                3,
                null,
                null,
                null,
                now,
                now
        );

        Task secondTask = new Task(
                UUID.randomUUID(),
                "echo",
                "{\"message\":\"second\"}",
                TaskStatus.PENDING,
                now.minusMinutes(1),
                0,
                3,
                null,
                null,
                null,
                now,
                now
        );

        repo.insert(firstTask);
        repo.insert(secondTask);

        List<Task> claimed = repo.claimDueTasks("worker-test", 1, 30);

        Task updatedFirstTask = repo.findByID(firstTask.id()).orElseThrow();
        Task updatedSecondTask = repo.findByID(secondTask.id()).orElseThrow();

        assertThat(claimed).hasSize(1);

        long runningTasks = List.of(updatedFirstTask, updatedSecondTask).stream()
                .filter(task -> task.status() == TaskStatus.RUNNING)
                .count();

        long pendingTasks = List.of(updatedFirstTask, updatedSecondTask).stream()
                .filter(task -> task.status() == TaskStatus.PENDING)
                .count();

        assertThat(runningTasks).isEqualTo(1);
        assertThat(pendingTasks).isEqualTo(1);
    }
}
