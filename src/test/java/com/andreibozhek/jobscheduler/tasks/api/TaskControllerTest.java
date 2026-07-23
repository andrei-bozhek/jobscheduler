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

    /**
     * Verifies the HTTP response shape for task creation.
     *
     * What this test checks:
     * - the client sends payload as a JSON object;
     * - POST /tasks creates the task and returns HTTP 201;
     * - the response body is present;
     * - payload is returned as a JSON object, not as an escaped JSON string;
     * - the nested payload field message keeps the original value.
     */
    @Test
    void createTaskReturnsPayloadAsJson() {
        // Build the same kind of request body an API client would send
        Map<String, Object> request = Map.of(
                "type", "echo",
                "payload", Map.of("message", "hello"),
                "runAt", OffsetDateTime.now().plusMinutes(1).toString(),
                "maxAttempts", 3
        );

        // Send a real HTTP POST request to the running test server
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                "/tasks",
                request,
                JsonNode.class
        );

        // The endpoint created the task and returned payload as JSON
        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("payload").get("message").asText()).isEqualTo("hello");
        assertThat(response.getBody().get("payload").isObject()).isTrue();
    }
}