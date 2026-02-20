package com.andreibozhek.jobscheduler.tasks.service;

import com.andreibozhek.jobscheduler.tasks.api.*;
import com.andreibozhek.jobscheduler.tasks.domain.Task;
import com.andreibozhek.jobscheduler.tasks.domain.TaskStatus;
import com.andreibozhek.jobscheduler.tasks.repo.TaskRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static  com.andreibozhek.jobscheduler.tasks.service.SupportedTaskTypes.TYPES;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TaskService {
    private final TaskRepository repo;
    private final ObjectMapper objectMapper;

    public TaskService(TaskRepository repo, ObjectMapper objectMapper) {
        this.repo = repo;
        this.objectMapper = objectMapper;
    }

    public Task create(CreateTaskRequest req) throws BadRequestException {
        int maxAttempts;
        if (req.maxAttempts() == null) {
            maxAttempts = 5;
        } else {
            maxAttempts = req.maxAttempts();
        }

        String normalizedType = req.type().toLowerCase();
        if (!TYPES.contains(normalizedType)) {
            throw new UnsupportedTaskTypeException(req.type(), TYPES);
        }

        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(req.payload());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid payload JSON");
        }
        if (payloadJson.length() > 5000) {
            throw new BadRequestException("payload too long");
        }

        OffsetDateTime now = OffsetDateTime.now();
        if (req.runAt().isBefore(now.minusSeconds(10))) {
            throw new BadRequestException("runAt must be in future");
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

    public Optional<Task> get (UUID id) {
        return repo.findByID(id);
    }

    public List<Task> list(TaskStatus status, int limit, int offset) {
        return repo.list(status, limit, offset);
    }

    public List<TaskAttemptResponse> listRuns(UUID taskId) {
        repo.findByID(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        return repo.listAttempts(taskId);
    }

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