package com.andreibozhek.jobscheduler.tasks.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record CreateTaskRequest(
        @NotBlank   String      type,
        @NotNull    String      payload,
        @NotNull    OffsetDateTime runAt,
                    Integer     maxAttempts
) {}