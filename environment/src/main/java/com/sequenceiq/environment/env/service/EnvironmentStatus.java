package com.sequenceiq.environment.env.service;

public enum EnvironmentStatus {
    CREATION_INITIATED,
    NETWORK_CREATION_IN_PROGRESS,
    RDBMS_CREATION_IN_PROGRESS,
    FREEIPA_CREATION_IN_PROGRESS,
    AVAILABLE,
    ARCHIVED,
    CORRUPTED
}
