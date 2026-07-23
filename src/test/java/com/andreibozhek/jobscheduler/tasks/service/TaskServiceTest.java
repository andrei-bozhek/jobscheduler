package com.andreibozhek.jobscheduler.tasks.service;

import com.andreibozhek.jobscheduler.IntegrationTestBase;
import com.andreibozhek.jobscheduler.tasks.api.CreateTaskRequest;
import com.andreibozhek.jobscheduler.tasks.domain.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TaskServiceTest extends IntegrationTestBase {

    @Autowired
    TaskService service;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createTaskUsesDefaultMaxAttempts() {
        CreateTaskRequest request = new CreateTaskRequest(
                "echo",
                objectMapper.createObjectNode().put("message", "hello"),
                OffsetDateTime.now().plusMinutes(1),
                null
        );

        Task task = service.create(request);

        assertThat(task.maxAttempts()).isEqualTo(3);
    }

    @Test
    void createTaskNormalizesTaskType() {
        CreateTaskRequest request = new CreateTaskRequest(
                "ECHO",
                objectMapper.createObjectNode().put("message", "hello"),
                OffsetDateTime.now().plusMinutes(1),
                3
        );

        Task task = service.create(request);

        assertThat(task.type()).isEqualTo("echo");
    }
}