package com.andreibozhek.jobscheduler.tasks.api;

import com.andreibozhek.jobscheduler.tasks.domain.Task;
import com.andreibozhek.jobscheduler.tasks.domain.TaskStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TaskResponse(
        UUID            id,
        String          type,
        String          payload,
        TaskStatus      status,
        OffsetDateTime  runAt,
        int             attempt,
        int             maxAttempts,
        String          lastError
) {
    public static TaskResponse from(Task t) {
        return new TaskResponse(
                t.id(),
                t.type(),
                t.payloadJson(),
                t.status(),
                t.runAt(),
                t.attempt(),
                t.maxAttempts(),
                t.lastError()
        );
    }
}