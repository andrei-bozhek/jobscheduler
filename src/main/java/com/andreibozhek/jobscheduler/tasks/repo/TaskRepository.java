package com.andreibozhek.jobscheduler.tasks.repo;

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

    public void insert(Task t) {
        jdbc.update("""
                INSERT INTO tasks(
                    id, type, payload, status, run_at,
                    attempt, max_attempts, last_error,
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
                t.lastError(),
                t.lockedBy(),
                t.lockedUntil()
        );
    }

    public Optional<Task> findByID(UUID id) {
        List<Task> rows = jdbc.query(
                "SELECT * FROM tasks WHERE id = ?",
                taskRowMapper(),
                id
        );
        return rows.stream().findFirst();
    }

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

    public boolean cancelIfPending(UUID id) {
        int updated = jdbc.update("""
                UPDATE tasks
                SET status = 'CANCELED'
                WHERE id = ? AND status = 'PENDING'
                """, id);
        return updated == 1;
    }

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
                        rs.getString("last_error"),
                        rs.getString("locked_by"),
                        rs.getObject("locked_until", OffsetDateTime.class),
                        rs.getObject("created_at", OffsetDateTime.class),
                        rs.getObject("updated_at", OffsetDateTime.class)
                );
            }
        };
    }

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

    public void insertAttemptStart(UUID taskId, int attempt) {
        jdbc.update("""
                INSERT INTO task_attempts(task_id, attempt, status)
                VALUES (?, ?, 'STARTED')
                """, taskId, attempt);
    }

    public void finishAttempt(UUID taskId, int attempt, String status, String error) {
        jdbc.update("""
            UPDATE task_attempts
            SET finished_at = now(),
                status = ?,
                last_error = ?
            WHERE task_id = ? AND attempt = ?
            """, status, error, taskId, attempt);
    }

    public void markDone(UUID taskId) {
        jdbc.update("""
            UPDATE tasks
            SET status = 'DONE',
                locked_by = NULL,
                locked_until = NULL,
                last_error = NULL
            WHERE id = ?
            """, taskId);
    }

    public void markFailedOrRetry(UUID taskId, int attempt, int maxAttempts, String error) {
        if (attempt < maxAttempts) {
            int backoffSeconds = attempt * 5;
            jdbc.update("""
                    UPDATE tasks
                    SET status = 'PENDING',
                        run_at = now() + (? * interval '1 second'),
                        locked_by = NULL,
                        locked_until = NULL,
                        last_error = ?
                    WHERE id = ?
                    """, backoffSeconds, error, taskId);
        } else {
            jdbc.update("""
                    UPDATE tasks
                    SET status = 'FAILED',
                        locked_by = NULL,
                        locked_until = NULL,
                        last_error = ?
                    WHERE id = ?
                    """, error, taskId);
        }
    }

}
