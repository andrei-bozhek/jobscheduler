package com.andreibozhek.jobscheduler.tasks.handler;

import com.andreibozhek.jobscheduler.tasks.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EchoTaskHandler implements TaskHandler {
    private static final Logger log = LoggerFactory.getLogger(EchoTaskHandler.class);

    @Override
    public String type() {
        return "echo";
    }

    @Override
    public void handle(Task task) {
        log.info("ECHO task {} payload {}", task.id(), task.payloadJson());
    }

}
