package com.andreibozhek.jobscheduler.tasks.service;

import jakarta.validation.constraints.Pattern;

import java.util.Set;

public final class SupportedTaskTypes {
    private SupportedTaskTypes() {}

    @Pattern(regexp = "^[a-zA-Z0-9_-]{1,32}$")
    public static final Set<String> TYPES = Set.of(
            "echo",
            "job",
            "routine",
            "urgent",
            "learning",
            "rest"
    );
}