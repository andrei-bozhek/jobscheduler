package com.andreibozhek.jobscheduler.tasks.handler;

import com.andreibozhek.jobscheduler.tasks.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Handler for echo tasks.
 * <p>
 * Echo tasks do not call an external system. They write the payload to the
 * application log and then finish successfully. This makes the type useful for
 * local testing and demos.
 */
@Component
public class EchoTaskHandler implements TaskHandler {
    private static final Logger log = LoggerFactory.getLogger(EchoTaskHandler.class);

    /**
     * Registers this handler for the echo task type.
     */
    @Override
    public String type() {
        return "echo";
    }

    /**
     * Executes an echo task by logging its payload.
     * <p>
     * The worker marks the task as DONE after this method returns successfully.
     */
    @Override
    public void handle(Task task) {
        log.info("ECHO task {} payload {}", task.id(), task.payloadJson());
    }

}
