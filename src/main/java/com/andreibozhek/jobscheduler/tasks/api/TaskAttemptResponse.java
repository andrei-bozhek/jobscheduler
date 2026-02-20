package com.andreibozhek.jobscheduler.tasks.api;

import java.time.OffsetDateTime;

public record TaskAttemptResponse(
        long            id,
        int             attempt,
        String          status,
        OffsetDateTime  startedAt,
        OffsetDateTime  finishedAt,
        String          error
) {

}