package com.andreibozhek.jobscheduler.tasks.handler;

import com.andreibozhek.jobscheduler.tasks.domain.Task;

/**
 * Defines how one task type is executed.
 * <p>
 * Each implementation handles one task type, such as "echo". The worker uses
 * type() to find the correct handler and then calls handle(task) to execute
 * the task.
 */
public interface TaskHandler {
    /**
     * Returns the task type supported by this handler.
     * <p>
     * The value must match the type stored in the tasks table.
     */
    String type();

    /**
     * Executes the task.
     * <p>
     * If execution fails, the method should throw an exception. The worker catches
     * that exception and decides whether the task should be retried or marked as
     * failed.
     */
    void handle(Task task);
}
