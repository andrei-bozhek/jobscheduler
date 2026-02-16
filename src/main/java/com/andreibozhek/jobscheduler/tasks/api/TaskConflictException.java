package com.andreibozhek.jobscheduler.tasks.api;

public class TaskConflictException extends RuntimeException {
    public TaskConflictException(String msg) {
        super(msg);
    }
}