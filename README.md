# Job Scheduler

A small task scheduling service built with Spring Boot and PostgreSQL.

This repository is a portfolio project that focuses on:
- clean layering (API / service / repository),
- DB migrations with Flyway,
- request validation and API boundaries,
- concurrency-safe task claiming (PostgreSQL row-level locking / lease columns),
- retry with backoff and execution history.

---

## Tech Stack
- Java 21
- Spring Boot (Web / Validation / JDBC / Actuator)
- PostgreSQL 16
- Flyway

---

## Run locally

### 1) Start PostgreSQL (Docker)

```bash
docker compose up -d
```

`docker-compose.yml` defaults:
- DB: `job_scheduler`
- User: `job_user`
- Password: `job_pass`
- Host port: `5433` → container `5432`

> The app is configured to connect to Postgres on `localhost:5433`.
> If your `application.yaml` differs, update it accordingly.

### 2) Run the app

```bash
./gradlew bootRun
```

Flyway migrations are applied automatically on startup.

### 3) (Optional) Health check

If Actuator is enabled:
```bash
curl http://localhost:8080/actuator/health
```

---

## API

Base path: `/tasks`

### Create a task

`POST /tasks`

Request body (`CreateTaskRequest`):
```json
{
  "type": "echo",
  "payload": { "message": "hello" },
  "runAt": "2026-02-20T10:20:00+09:00",
  "maxAttempts": 3
}
```

Validation (controller/DTO level):
- `type`: required, non-blank
- `payload`: required JSON
- `runAt`: required
- `maxAttempts`: optional; if provided must be `1..5`

Response (`TaskResponse`):
```json
{
  "id": "2c7a9a1f-3ac8-4d2d-8f5f-6f2d6a5c7e2a",
  "type": "echo",
  "payload": "{\"message\":\"hello\"}",
  "status": "PENDING",
  "runAt": "2026-02-20T10:20:00+09:00",
  "attempt": 0,
  "maxAttempts": 3,
  "error": null
}
```

### Get a task

`GET /tasks/{id}`

Returns `TaskResponse`.

### List tasks

`GET /tasks?status=PENDING&limit=20&offset=0`

Query params:
- `status` (optional): `PENDING | RUNNING | DONE | FAILED | CANCELED`
- `limit`: default 20, must be `1..100`
- `offset`: default 0, must be `>= 0`

Returns `List<TaskResponse>`.

### Run history (attempts)

`GET /tasks/{id}/runs`

Returns `List<TaskAttemptResponse>`.

### Cancel a task

`POST /tasks/{id}/cancel`

- Allowed only when the task is `PENDING`
- Returns `204 No Content`

---

## Data Model (PostgreSQL)

### `tasks`
- `id` (UUID, PK)
- `type` (TEXT, NOT NULL)
- `payload` (JSONB, NOT NULL)
- `status` (TEXT, NOT NULL): `PENDING/RUNNING/DONE/FAILED/CANCELED`
- `run_at` (TIMESTAMPTZ, NOT NULL)
- `attempt` (INT, default 0)
- `max_attempts` (INT, default 3)
- `error` (TEXT, nullable)
- `locked_by` (TEXT, nullable)
- `locked_until` (TIMESTAMPTZ, nullable)
- `created_at` / `updated_at` (TIMESTAMPTZ)

Indexes:
- `idx_tasks_status_run_at` on `(status, run_at)`
- `idx_tasks_status_until` on `(locked_until)`

Trigger:
- `updated_at` is updated automatically before each update.

### `task_attempts`
- `id` (BIGSERIAL, PK)
- `task_id` (UUID, FK → tasks.id, cascade delete)
- `attempt` (INT)
- `started_at` (TIMESTAMPTZ, default now())
- `finished_at` (TIMESTAMPTZ, nullable)
- `status` (TEXT)
- `error` (TEXT, nullable)

Index:
- `idx_task_attempts_task_id_attempt` on `(task_id, attempt)`

---

## Task lifecycle

Statuses:
- `PENDING`   — waiting for execution
- `RUNNING`   — claimed by a worker
- `DONE`      — finished successfully
- `FAILED`    — failed after all retries
- `CANCELED`  — canceled by user

Typical transitions:
- `PENDING → RUNNING`
- `RUNNING → DONE | FAILED | PENDING` (retry)
- `PENDING → CANCELED` (cancel is allowed only while pending)

---

## Concurrency model (safe claiming)

Multiple worker threads / multiple app instances can run concurrently.

The design supports safe claiming using:
- PostgreSQL row-level locking (e.g., `SELECT ... FOR UPDATE SKIP LOCKED`), and/or
- a lease with `locked_by` and `locked_until`.

Goal: a due task is claimed by at most one worker at a time.

---

## Retry / backoff

On failure:
- if attempts remain → task returns to `PENDING`
- next `runAt` is shifted by a simple linear backoff: `(attempt * 5 seconds)`
- each attempt is recorded in `task_attempts`

---

## To Do

- actuator health status
- integration tests
- metrics (tasks by status, success/failure rate, durations)
- “RUNNING lease timeout” requeue rule (if a worker dies mid-run)
- handler strategy per `type`
