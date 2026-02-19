package com.andreibozhek.jobscheduler.tasks.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ProblemDetail notFound(TaskNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Not Found");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(TaskConflictException.class)
    public ProblemDetail conflict(TaskConflictException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflict");
        pd.setDetail(ex.getMessage());
        return pd;
    }

//    @ExceptionHandler(UnsupportedTaskTypeException.class)
//    public ProblemDetail badRequest(UnsupportedTaskTypeException ex) {
//        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
//        pd.setTitle("Bad Request");
//        pd.setDetail(ex.getMessage());
//        return pd;
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail badRequest(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail("Request has invalid fields");

        List<Map<String, String>> errors = new ArrayList<>();
        for (FieldError fe : ex.getFieldErrors()) {
            Map<String, String> item = new HashMap<>();
            item.put("field", fe.getField());
            item.put("message", fe.getDefaultMessage());
            errors.add(item);
        }
        pd.setProperty("errors", errors);
        return pd;
    }
}