package com.andreibozhek.jobscheduler.tasks.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Task(
  UUID              id,
  String            type,
  String            payloadJson,
  TaskStatus        status,
  OffsetDateTime    runAt,
  int               attempt,
  int               maxAttempts,
  String            error,
  String            lockedBy,
  OffsetDateTime    lockedUntil,
  OffsetDateTime    createdAt,
  OffsetDateTime    updatedAt
) {}
