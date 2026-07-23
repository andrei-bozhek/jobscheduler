package com.andreibozhek.jobscheduler.tasks.api;

import com.andreibozhek.jobscheduler.IntegrationTestBase;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TaskControllerTest extends IntegrationTestBase {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void createTaskReturnsPayloadAsJson() {
        Map<String, Object> request = Map.of(
                "type", "echo",
                "payload", Map.of("message", "hello"),
                "runAt", OffsetDateTime.now().plusMinutes(1).toString(),
                "maxAttempts", 3
        );

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                "/tasks",
                request,
                JsonNode.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("payload").get("message").asText()).isEqualTo("hello");
        assertThat(response.getBody().get("payload").isObject()).isTrue();
    }
}