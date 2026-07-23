package com.andreibozhek.jobscheduler.tasks.service;

import com.andreibozhek.jobscheduler.tasks.api.*;
import com.andreibozhek.jobscheduler.tasks.domain.Task;
import com.andreibozhek.jobscheduler.tasks.domain.TaskStatus;
import com.andreibozhek.jobscheduler.tasks.handler.TaskHandler;
import com.andreibozhek.jobscheduler.tasks.repo.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TaskService {
    private final TaskRepository repo;
    private final ObjectMapper objectMapper;
    private final Set<String> supportedTypes;

    public TaskService(
            TaskRepository repo,
            ObjectMapper objectMapper,
            List<TaskHandler> handlers) {
        this.repo = repo;
        this.objectMapper = objectMapper;
        this.supportedTypes = handlers.stream()
                .map(TaskHandler::type)
                .collect(Collectors.toSet());
    }

    /**
     * Creates a new task and stores it as PENDING.
     * <p>
     * The method normalizes the task type, validates that the type is supported,
     * serializes the payload to JSON, and applies the default maxAttempts value
     * when the client does not provide it.
     */
    public Task create(CreateTaskRequest req) {
        int maxAttempts;
        if (req.maxAttempts() == null) {
            maxAttempts = 3;
        } else {
            maxAttempts = req.maxAttempts();
        }

        String normalizedType = req.type().toLowerCase();
        if (!supportedTypes.contains(normalizedType)) {
            throw new UnsupportedTaskTypeException(req.type(), supportedTypes);
        }

        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(req.payload());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid payload JSON");
        }

        if (payloadJson.length() > 5000) {
            throw new BadRequestApiException("payload too long");
        }

        OffsetDateTime now = OffsetDateTime.now();
        if (req.runAt().isBefore(now.minusSeconds(10))) {
            throw new BadRequestApiException("runAt must be in future");
        }

        Task t = new Task(
                UUID.randomUUID(),
                normalizedType,
                payloadJson,
                TaskStatus.PENDING,
                req.runAt(),
                0,
                maxAttempts,
                null,
                null,
                null,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        repo.insert(t);
        return t;
    }

    /**
     * Finds a task by id.
     * <p>
     * The method returns Optional.empty() when the task does not exist.
     * The controller decides how to convert that case into an HTTP 404 response.
     */
    public Optional<Task> get (UUID id) {
        return repo.findByID(id);
    }

    /**
     * Lists tasks with optional status filtering.
     * <p>
     * When status is null, the repository returns tasks from all statuses.
     * Pagination is controlled by limit and offset.
     */
    public List<Task> list(TaskStatus status, int limit, int offset) {
        return repo.list(status, limit, offset);
    }

    /**
     * Returns execution attempts for a task.
     * <p>
     * The task must exist. If the task id is unknown, the method throws
     * TaskNotFoundException so the API can return a 404 response.
     */
    public List<TaskAttemptResponse> listRuns(UUID taskId) {
        repo.findByID(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        return repo.listAttempts(taskId);
    }

    /**
     * Cancels a task when it is still waiting for execution.
     * <p>
     * Only PENDING tasks can be canceled. If the task is already RUNNING,
     * DONE, FAILED, or CANCELED, the method throws TaskConflictException.
     */
    public void cancel(UUID id) {
        Task t = repo.findByID(id).orElseThrow(() -> new TaskNotFoundException(id));

        if (t.status() != TaskStatus.PENDING) {
            throw new TaskConflictException("Task is not cancelable in status: "+ t.status());
        }

        boolean ok = repo.cancelIfPending(id);
        if (!ok) {
            throw new TaskConflictException("Task status changed, cancel failed: " + id);
        }
    }
}