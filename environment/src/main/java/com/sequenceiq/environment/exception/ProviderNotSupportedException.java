package com.sequenceiq.environment.exception;

public class ProviderNotSupportedException extends RuntimeException {
    private static ProviderNotSupportedException ourInstance = new ProviderNotSupportedException();

    public static ProviderNotSupportedException getInstance() {
        return ourInstance;
    }

    private ProviderNotSupportedException() {
    }
}
