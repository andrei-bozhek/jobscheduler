package com.andreibozhek.jobscheduler.tasks.api;

import java.util.Set;

public class UnsupportedTaskTypeException extends RuntimeException {
    public UnsupportedTaskTypeException(String type, Set<String> supported) {
        super("Unsupported task type: " + type + ". Supported types: " + String.join(", ", supported));

    }
}