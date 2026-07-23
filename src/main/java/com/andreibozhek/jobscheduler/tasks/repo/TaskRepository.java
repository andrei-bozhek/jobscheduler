package com.andreibozhek.jobscheduler.tasks.repo;

import com.andreibozhek.jobscheduler.tasks.api.TaskAttemptResponse;
import com.andreibozhek.jobscheduler.tasks.domain.Task;
import com.andreibozhek.jobscheduler.tasks.domain.TaskStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

@Repository
public class TaskRepository {
    private final JdbcTemplate jdbc;

    public TaskRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Stores a new task in the database.
     * <p>
     * The task is usually created with status PENDING.
     * The payload is stored as PostgreSQL JSONB,
     * so the method casts the JSON string with ::jsonb.
     */
    public void insert(Task t) {
        jdbc.update("""
                INSERT INTO tasks(
                    id, type, payload, status, run_at,
                    attempt, max_attempts, error,
                    locked_by, locked_until
                ) VALUES (
                    ?, ?, ?::jsonb, ?, ?,
                    ?, ?, ?,
                    ?, ?
                )
                """,
                t.id(),
                t.type(),
                t.payloadJson(),
                t.status().name(),
                t.runAt(),
                t.attempt(),
                t.maxAttempts(),
                t.error(),
                t.lockedBy(),
                t.lockedUntil()
        );
    }

    /**
     * Finds one task by its id.
     * <p>
     * The method returns Optional.empty() when no row exists for the given id.
     * This lets the service or controller decide how to handle a missing task.
     */
    public Optional<Task> findByID(UUID id) {
        List<Task> rows = jdbc.query(
                "SELECT * FROM tasks WHERE id = ?",
                taskRowMapper(),
                id
        );
        return rows.stream().findFirst();
    }

    /**
     * Lists tasks with optional status filtering.
     * <p>
     * When status is null, tasks from all statuses are returned. Results are
     * ordered by creation time, newest first, and limited by limit and offset.
     */
    public List<Task> list(TaskStatus status, int limit, int offset) {
        if (status == null) {
            return jdbc.query("""
                    SELECT * FROM tasks
                    ORDER BY created_at DESC
                    LIMIT ? OFFSET ?
                    """,
                    taskRowMapper(),
                    limit,
                    offset
            );
        }
        return jdbc.query("""
                SELECT * FROM tasks
                WHERE status = ?
                ORDER BY created_at DESC
                LIMIT ? OFFSET ?
                """,
                taskRowMapper(),
                status.name(),
                limit,
                offset
        );
    }

    /**
     * Cancels a task only if it is still pending.
     * <p>
     * The status check is part of the SQL update.
     * This protects the method from race conditions
     * where another worker may change the task status at the same time.
     * The method returns true only when exactly one row was updated.
     */
    public boolean cancelIfPending(UUID id) {
        int updated = jdbc.update("""
                UPDATE tasks
                SET status = 'CANCELED'
                WHERE id = ? AND status = 'PENDING'
                """, id);
        return updated == 1;
    }

    /**
     * Creates a RowMapper that converts a database row into a Task record.
     * <p>
     * Keeping the mapping in one method makes all task queries use the same
     * conversion logic for timestamps, UUID values, status values, and lock fields.
     */
    private RowMapper<Task> taskRowMapper() {
        return new RowMapper<>() {
            @Override
            public Task mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
                return new Task(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("type"),
                        rs.getString("payload"),
                        TaskStatus.valueOf(rs.getString("status")),
                        rs.getObject("run_at", OffsetDateTime.class),
                        rs.getInt("attempt"),
                        rs.getInt("max_attempts"),
                        rs.getString("error"),
                        rs.getString("locked_by"),
                        rs.getObject("locked_until", OffsetDateTime.class),
                        rs.getObject("created_at", OffsetDateTime.class),
                        rs.getObject("updated_at", OffsetDateTime.class)
                );
            }
        };
    }

    /**
     * Claims due pending tasks for one worker.
     * <p>
     * A task is due when its status is PENDING and run_at is not in the future.
     * The SELECT query uses FOR UPDATE SKIP LOCKED so multiple workers can claim
     * tasks at the same time without taking the same row.
     * <p>
     * Claimed tasks are moved to RUNNING, assigned to the worker id, given a lease
     * time, and their attempt counter is increased. The method returns the updated
     * task rows so the worker can execute them.
     */
    @Transactional
    public List<Task> claimDueTasks(String workerId, int batchSize, int lockSeconds) {

        List<UUID> ids = jdbc.query("""
                SELECT id
                FROM tasks
                WHERE status = 'PENDING'
                AND run_at <= now()
                ORDER BY run_at
                FOR UPDATE SKIP LOCKED
                LIMIT ?
                """,
                (rs, rowNum) -> UUID.fromString(rs.getString("id")),
                batchSize
        );

        if(ids.isEmpty()) {
            return List.of();
        }

        String inSql =String.join(",", ids.stream().map(x->"?").toList());

        ArrayList<Object> params = new ArrayList<>();
        params.add(workerId);
        params.add(lockSeconds);
        params.addAll(ids);

        String updateSql = """
                UPDATE tasks
                SET status = 'RUNNING',
                    locked_by = ?,
                    locked_until = now() + (? * interval '1 second'),
                    attempt = attempt + 1
                WHERE id IN (%s)
                """.formatted(inSql);
        jdbc.update(updateSql, params.toArray());

        String selectSql = """
                SELECT * FROM tasks
                WHERE id IN (%s)
                """.formatted(inSql);

        return jdbc.query(selectSql, taskRowMapper(), ids.toArray());

    }

    /**
     * Records the start of one execution attempt.
     * <p>
     * The attempt number is passed from the task row after claiming. This makes
     * task_attempts show which task attempt was started by the worker.
     */
    public void insertAttemptStart(UUID taskId, int attempt) {
        jdbc.update("""
                INSERT INTO task_attempts(task_id, attempt, status)
                VALUES (?, ?, 'STARTED')
                """, taskId, attempt);
    }

    /**
     * Marks one execution attempt as finished.
     * <p>
     * The status is usually SUCCESS or FAILED. When the attempt failed, error keeps
     * a short message that can be returned by the run history API.
     */
    public void finishAttempt(UUID taskId, int attempt, String status, String error) {
        jdbc.update("""
            UPDATE task_attempts
            SET finished_at = now(),
                status = ?,
                error = ?
            WHERE task_id = ? AND attempt = ?
            """, status, error, taskId, attempt);
    }

    /**
     * Marks a task as completed successfully.
     * <p>
     * The task leaves RUNNING state, its lock fields are cleared,
     * and the last error is removed because the final result is successful.
     */
    public void markDone(UUID taskId) {
        jdbc.update("""
            UPDATE tasks
            SET status = 'DONE',
                locked_by = NULL,
                locked_until = NULL,
                error = NULL
            WHERE id = ?
            """, taskId);
    }

    /**
     * Handles a failed task execution.
     * <p>
     * When attempts remain, the task is moved back to PENDING and scheduled again
     * with a small linear backoff. When no attempts remain, the task is marked as
     * FAILED. In both cases the lock fields are cleared.
     */
    public void markFailedOrRetry(UUID taskId, int attempt, int maxAttempts, String error) {
        if (attempt < maxAttempts) {
            int backoffSeconds = attempt * 5;
            jdbc.update("""
                    UPDATE tasks
                    SET status = 'PENDING',
                        run_at = now() + (? * interval '1 second'),
                        locked_by = NULL,
                        locked_until = NULL,
                        error = ?
                    WHERE id = ?
                    """, backoffSeconds, error, taskId);
        } else {
            jdbc.update("""
                    UPDATE tasks
                    SET status = 'FAILED',
                        locked_by = NULL,
                        locked_until = NULL,
                        error = ?
                    WHERE id = ?
                    """, error, taskId);
        }
    }

    /**
     * Returns expired running tasks back to the pending queue.
     * <p>
     * This handles the case where a worker claimed a task but stopped before
     * finishing it. Only RUNNING tasks with an expired locked_until value are
     * requeued. The method returns the number of rows that were updated.
     */
    public int requeueExpiredRunningTasks() {
        return jdbc.update("""
                UPDATE tasks
                SET status = 'PENDING',
                    locked_by = NULL,
                    locked_until = NULL,
                    error = 'Task lease expired'
                WHERE status = 'RUNNING'
                AND locked_until < now()
                """);
    }

    /**
     * Lists all execution attempts for one task.
     * <p>
     * Attempts are ordered by attempt number and then by id.
     * This gives a stable history for the API even
     * if more than one row has the same attempt number.
     */
    public List<TaskAttemptResponse> listAttempts(UUID taskId) {
        return jdbc.query("""
                SELECT id, attempt, status, started_at, finished_at, error
                FROM task_attempts
                WHERE task_id = ?
                ORDER BY attempt ASC, id ASC
                """,
                (rs, rowNum) -> new TaskAttemptResponse(
                        rs.getLong("id"),
                        rs.getInt("attempt"),
                        rs.getString("status"),
                        rs.getObject("started_at", OffsetDateTime.class),
                        rs.getObject("finished_at", OffsetDateTime.class),
                        rs.getString("error")
                ),
                taskId
        );
    }

}
