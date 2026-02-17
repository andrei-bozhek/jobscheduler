package com.andreibozhek.jobscheduler.tasks.service;

import java.util.Set;

public final class SupportedTaskTypes {
    private SupportedTaskTypes() {}

    public static final Set<String> TYPES = Set.of("echo");
}