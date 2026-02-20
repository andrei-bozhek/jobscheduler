package com.andreibozhek.jobscheduler.tasks.api;

import com.andreibozhek.jobscheduler.tasks.domain.Task;
import com.andreibozhek.jobscheduler.tasks.domain.TaskStatus;
import com.andreibozhek.jobscheduler.tasks.service.TaskService;

import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody CreateTaskRequest req) throws BadRequestException {
        Task t = service.create(req);
        return TaskResponse.from(t);
    }

    @GetMapping("/{id}")
    public TaskResponse get(@PathVariable UUID id) {
        Task t = service.get(id).orElseThrow(()-> new TaskNotFoundException(id));
        return TaskResponse.from(t);
    }

    @GetMapping
    public List<TaskResponse> list(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        if (limit < 1 || limit > 100) {
            throw new TaskConflictException("limit must be in range 1..100");
        }
        if (offset < 0) {
            throw new TaskConflictException("offset must be >=0");
        }

        return service.list(status, limit, offset).stream().map(TaskResponse::from).toList();
    }

    @GetMapping("/{id}/runs")
    public List<TaskAttemptResponse> runs(@PathVariable UUID id) {
        return service.listRuns(id);
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable UUID id) {
        service.cancel(id);
    }

}