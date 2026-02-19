package com.andreibozhek.jobscheduler.tasks.api;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.OffsetDateTime;

public record CreateTaskRequest(
        @NotBlank           String          type,
        @NotNull            JsonNode        payload,
        @NotNull            OffsetDateTime  runAt,
        @Positive @Max(5)   Integer         maxAttempts
) {}