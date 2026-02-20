CREATE TABLE tasks (
    id                  UUID PRIMARY KEY,
    type                TEXT NOT NULL,
    payload             JSONB NOT NULL,

    status              TEXT NOT NULL,          -- PENDING/RUNNING/DONE/FAILED/CANCELED
    run_at              TIMESTAMPTZ NOT NULL,

    attempt             INT NOT NULL DEFAULT 0,
    max_attempts        INT NOT NULL DEFAULT 3,
    error               TEXT,

    locked_by           TEXT,
    locked_until        TIMESTAMPTZ,

    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_tasks_status_run_at ON tasks(status, run_at);
CREATE INDEX idx_tasks_status_until ON tasks(locked_until);

CREATE TABLE task_attempts (
    id                  BIGSERIAL PRIMARY KEY,
    task_id             UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    attempt             INT NOT NULL,

    started_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    finished_at         TIMESTAMPTZ,
    status              TEXT NOT NULL,
    error               TEXT
);

CREATE INDEX idx_task_attempts_task_id_attempt ON task_attempts(task_id, attempt);

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_tasks_updated_at ON tasks;

CREATE TRIGGER trg_tasks_updated_at
BEFORE UPDATE ON tasks
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();
