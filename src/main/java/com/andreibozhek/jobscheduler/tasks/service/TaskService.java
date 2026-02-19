package com.andreibozhek.jobscheduler.tasks.service;

import com.andreibozhek.jobscheduler.tasks.api.CreateTaskRequest;
import com.andreibozhek.jobscheduler.tasks.api.TaskAttemptResponse;
import com.andreibozhek.jobscheduler.tasks.api.TaskNotFoundException;
import com.andreibozhek.jobscheduler.tasks.domain.Task;
import com.andreibozhek.jobscheduler.tasks.domain.TaskStatus;
import com.andreibozhek.jobscheduler.tasks.repo.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.andreibozhek.jobscheduler.tasks.api.UnsupportedTaskTypeException;
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

    public Task create(CreateTaskRequest req) {
        int maxAttempts;
        if (req.maxAttempts() == null) {
            maxAttempts = 3;
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

    public boolean cancel(UUID id) {
        return repo.cancelIfPending(id);
    }
}