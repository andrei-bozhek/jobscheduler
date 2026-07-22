package com.andreibozhek.jobscheduler.tasks.api;

import com.andreibozhek.jobscheduler.tasks.domain.Task;
import com.andreibozhek.jobscheduler.tasks.domain.TaskStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        String          error
) {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static TaskResponse from(Task t) {
        return new TaskResponse(
                t.id(),
                t.type(),
                t.payloadJson(),
                t.status(),
                t.runAt(),
                t.attempt(),
                t.maxAttempts(),
                t.error()
        );
    }

    private static JsonNode readPayload(String payloadJson) {
        try {
            return OBJECT_MAPPER.readTree(payloadJson);
        } catch (Exception ex) {
            throw new IllegalStateException("Stored task payload is not valid JSON", ex);
        }
    }
}