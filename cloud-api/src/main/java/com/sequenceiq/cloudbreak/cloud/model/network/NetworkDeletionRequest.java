package com.sequenceiq.cloudbreak.cloud.model.network;

import java.util.Set;

import com.sequenceiq.cloudbreak.cloud.model.CloudCredential;

public class NetworkDeletionRequest {

    private final String stackName;

    private final CloudCredential cloudCredential;

    private final String region;

    private final String resourceGroup;

    private final Set<String> subnetIds;

    private final String networkId;

    private NetworkDeletionRequest(Builder builder) {
        this.stackName = builder.stackName;
        this.cloudCredential = builder.cloudCredential;
        this.region = builder.region;
        this.resourceGroup = builder.resourceGroup;
        this.subnetIds = builder.subnetIds;
        this.networkId = builder.networkId;
    }

    public String getStackName() {
        return stackName;
    }

    public CloudCredential getCloudCredential() {
        return cloudCredential;
    }

    public String getRegion() {
        return region;
    }

    public String getResourceGroup() {
        return resourceGroup;
    }

    public Set<String> getSubnetIds() {
        return subnetIds;
    }

    public String getNetworkId() {
        return networkId;
    }

    public static final class Builder {

        private String stackName;

        private CloudCredential cloudCredential;

        private String region;

        private String resourceGroup;

        private Set<String> subnetIds;

        private String networkId;

        public Builder() {
        }

        public Builder withStackName(String stackName) {
            this.stackName = stackName;
            return this;
        }

        public Builder withCloudCredential(CloudCredential cloudCredential) {
            this.cloudCredential = cloudCredential;
            return this;
        }

        public Builder withRegion(String region) {
            this.region = region;
            return this;
        }

        public Builder withResourceGroup(String resourceGroup) {
            this.resourceGroup = resourceGroup;
            return this;
        }

        public Builder withNetworkId(String networkId) {
            this.networkId = networkId;
            return this;
        }

        public Builder withSubnetIds( Set<String> subnetIds) {
            this.subnetIds = subnetIds;
            return this;
        }

        public NetworkDeletionRequest build() {
            return new NetworkDeletionRequest(this);
        }
    }
}
