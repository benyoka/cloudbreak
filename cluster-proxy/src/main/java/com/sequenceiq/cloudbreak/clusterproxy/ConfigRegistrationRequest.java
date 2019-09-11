package com.sequenceiq.cloudbreak.clusterproxy;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfigRegistrationRequest {
    @JsonProperty
    private String clusterCrn;

    @JsonProperty
    private List<ClusterServiceConfig> services;

    @JsonProperty
    private List<String> certificates;

    @JsonCreator
    public ConfigRegistrationRequest(String clusterCrn, List<ClusterServiceConfig> services, List<String> certificates) {
        this.clusterCrn = clusterCrn;
        this.services = services;
        this.certificates = certificates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConfigRegistrationRequest that = (ConfigRegistrationRequest) o;

        return Objects.equals(clusterCrn, that.clusterCrn) &&
                Objects.equals(services, that.services) &&
                Objects.equals(certificates, that.certificates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clusterCrn, services, certificates);
    }

    @Override
    public String toString() {
        return "ConfigRegistrationRequest{clusterCrn='" + clusterCrn
                + '\'' + ", services=" + services
                + '\'' + ", certificates=" + certificates
                + '}';
    }
}
