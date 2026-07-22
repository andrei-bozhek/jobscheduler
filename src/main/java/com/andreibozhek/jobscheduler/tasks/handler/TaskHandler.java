package com.andreibozhek.jobscheduler.tasks.handler;

import com.andreibozhek.jobscheduler.tasks.domain.Task;

public interface TaskHandler {
    String type();

    void handle(Task task);
}
