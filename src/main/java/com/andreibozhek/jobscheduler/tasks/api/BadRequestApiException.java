package com.andreibozhek.jobscheduler.tasks.api;

public class BadRequestApiException extends RuntimeException {
    public BadRequestApiException(String message) {
        super(message);
    }
}
