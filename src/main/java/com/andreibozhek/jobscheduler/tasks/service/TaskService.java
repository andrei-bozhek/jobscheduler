package com.andreibozhek.jobscheduler.tasks.service;

import com.andreibozhek.jobscheduler.tasks.api.CreateTaskRequest;
import com.andreibozhek.jobscheduler.tasks.domain.Task;
import com.andreibozhek.jobscheduler.tasks.domain.TaskStatus;
import com.andreibozhek.jobscheduler.tasks.repo.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepository repo;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    public Task create(CreateTaskRequest req) {
        int maxAttempts;
        if (req.maxAttempts() == null) {
            maxAttempts = 3;
        } else {
            maxAttempts = req.maxAttempts();
        }
        Task t = new Task(
                UUID.randomUUID(),
                req.type(),
                req.payload(),
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

    public boolean cancel(UUID id) {
        return repo.cancelIfPending(id);
    }
}