package com.andreibozhek.jobscheduler.tasks.repo;

import com.andreibozhek.jobscheduler.tasks.domain.Task;
import com.andreibozhek.jobscheduler.tasks.domain.TaskStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
            public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Task(
                        UUID.fromString(rs.getNString("id")),
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
}
