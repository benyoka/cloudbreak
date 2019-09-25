package com.sequenceiq.it.cloudbreak.mock;

import java.util.List;

import spark.Service;

public abstract class MockModel {

    private String mockServerAddress;

    public String getMockServerAddress() {
        return mockServerAddress;
    }

    public void setMockServerAddress(String mockServerAddress) {
        this.mockServerAddress = mockServerAddress;
    }

    public abstract void startModel(Service sparkService, String mockServerAddress, List<String> activeProfiles);

    public abstract String getClusterName();
}
