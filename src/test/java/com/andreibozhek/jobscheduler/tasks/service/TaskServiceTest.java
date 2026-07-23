package com.andreibozhek.jobscheduler.tasks.service;

import com.andreibozhek.jobscheduler.IntegrationTestBase;
import com.andreibozhek.jobscheduler.tasks.api.BadRequestApiException;
import com.andreibozhek.jobscheduler.tasks.api.CreateTaskRequest;
import com.andreibozhek.jobscheduler.tasks.api.TaskConflictException;
import com.andreibozhek.jobscheduler.tasks.api.UnsupportedTaskTypeException;
import com.andreibozhek.jobscheduler.tasks.domain.Task;
import com.andreibozhek.jobscheduler.tasks.domain.TaskStatus;
import com.andreibozhek.jobscheduler.tasks.repo.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskServiceTest extends IntegrationTestBase {

    @Autowired
    TaskService service;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TaskRepository repo;

    /**
     * Verifies that maxAttempts gets a default value when the client omits it.
     * <p>
     * What this test checks:
     * - the request has maxAttempts set to null;
     * - service.create(...) still creates the task;
     * - the created task uses 3 as the default maxAttempts value.
     */
    @Test
    void createTaskUsesDefaultMaxAttempts() {
        CreateTaskRequest request = new CreateTaskRequest(
                "echo",
                objectMapper.createObjectNode().put("message", "hello"),
                OffsetDateTime.now().plusMinutes(1),
                null
        );

        Task task = service.create(request);

        assertThat(task.maxAttempts()).isEqualTo(3);
    }

    /**
     * Verifies that task type is normalized before saving.
     * <p>
     * What this test checks:
     * - the request uses uppercase ECHO;
     * - service.create(...) accepts it;
     * - the stored task type becomes lowercase echo.
     */
    @Test
    void createTaskNormalizesTaskType() {
        CreateTaskRequest request = new CreateTaskRequest(
                "ECHO",
                objectMapper.createObjectNode().put("message", "hello"),
                OffsetDateTime.now().plusMinutes(1),
                3
        );

        Task task = service.create(request);

        assertThat(task.type()).isEqualTo("echo");
    }

    /**
     * Verifies that unknown task types are rejected.
     * <p>
     * What this test checks:
     * - the request uses a task type that has no handler;
     * - service.create(...) does not create the task;
     * - UnsupportedTaskTypeException is thrown.
     */
    @Test
    void createTaskRejectsUnsupportedTaskType() {
        CreateTaskRequest request = new CreateTaskRequest(
                "unknown",
                objectMapper.createObjectNode().put("message", "hello"),
                OffsetDateTime.now().plusMinutes(1),
                3
        );

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(UnsupportedTaskTypeException.class);
    }

    /**
     * Verifies that tasks cannot be scheduled too far in the past.
     * <p>
     * What this test checks:
     * - the request uses runAt in the past;
     * - service.create(...) rejects the request;
     * - BadRequestApiException is thrown.
     */
    @Test
    void createTaskRejectsPastRunTime() {
        CreateTaskRequest request = new CreateTaskRequest(
                "echo",
                objectMapper.createObjectNode().put("message", "hello"),
                OffsetDateTime.now().minusMinutes(1),
                3
        );

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BadRequestApiException.class);
    }

    /**
     * Verifies that a pending task can be canceled.
     * <p>
     * What this test checks:
     * - service.create(...) creates a PENDING task;
     * - service.cancel(...) is called for that task;
     * - the task status becomes CANCELED.
     */
    @Test
    void cancelPendingTaskMarksTaskAsCanceled() {
        CreateTaskRequest request = new CreateTaskRequest(
                "echo",
                objectMapper.createObjectNode().put("message", "hello"),
                OffsetDateTime.now().plusMinutes(1),
                3
        );

        Task task = service.create(request);

        service.cancel(task.id());

        Task updated = service.get(task.id()).orElseThrow();

        assertThat(updated.status()).isEqualTo(TaskStatus.CANCELED);
    }

    /**
     * Verifies that a running task cannot be canceled.
     * <p>
     * What this test checks:
     * - the database contains a RUNNING task;
     * - service.cancel(...) is called for that task;
     * - TaskConflictException is thrown because only PENDING tasks are cancelable.
     */
    @Test
    void cancelRunningTaskThrowsConflict() {
        OffsetDateTime now = OffsetDateTime.now();

        Task task = new Task(
                UUID.randomUUID(),
                "echo",
                "{\"message\":\"hello\"}",
                TaskStatus.RUNNING,
                now.minusMinutes(1),
                1,
                3,
                null,
                "worker-test",
                now.plusMinutes(1),
                now,
                now
        );

        repo.insert(task);

        assertThatThrownBy(() -> service.cancel(task.id()))
                .isInstanceOf(TaskConflictException.class);
    }

    /**
     * Verifies that very large payloads are rejected.
     * <p>
     * What this test checks:
     * - the payload contains a long message;
     * - after JSON serialization, the payload is larger than the allowed limit;
     * - service.create(...) rejects the request;
     * - BadRequestApiException is thrown.
     */
    @Test
    void createTaskRejectsTooLargePayload() {
        String message = "a".repeat(5000);

        CreateTaskRequest request = new CreateTaskRequest(
                "echo",
                objectMapper.createObjectNode().put("message", message),
                OffsetDateTime.now().plusMinutes(1),
                3
        );

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BadRequestApiException.class);
    }

}