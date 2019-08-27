package com.sequenceiq.environment.api.v1.environment.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sequenceiq.common.api.telemetry.response.TelemetryResponse;
import com.sequenceiq.environment.api.v1.credential.model.response.CredentialResponse;
import com.sequenceiq.environment.api.v1.environment.model.base.Tunnel;
import com.sequenceiq.environment.api.v1.environment.model.request.aws.AwsEnvironmentParameters;
import com.sequenceiq.environment.api.v1.environment.model.request.azure.AzureEnvironmentParameters;
import com.sequenceiq.environment.api.v1.environment.model.request.cumulus.CumulusEnvironmentParameters;
import com.sequenceiq.environment.api.v1.environment.model.request.gcp.GcpEnvironmentParameters;
import com.sequenceiq.environment.api.v1.environment.model.request.openstack.OpenstackEnvironmentParameters;
import com.sequenceiq.environment.api.v1.environment.model.request.yarn.YarnEnvironmentParameters;

import io.swagger.annotations.ApiModel;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "SimpleEnvironmentV1Response")
public class SimpleEnvironmentResponse extends EnvironmentBaseResponse {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String crn;

        private String name;

        private String description;

        private boolean createFreeIpa;

        private CompactRegionResponse regions;

        private String cloudPlatform;

        private CredentialResponse credentialResponse;

        private LocationResponse location;

        private TelemetryResponse telemetry;

        private EnvironmentNetworkResponse network;

        private EnvironmentStatus environmentStatus;

        private String statusReason;

        private Long created;

        private Tunnel tunnel;

        private String adminGroupName;

        private AwsEnvironmentParameters aws;

        private AzureEnvironmentParameters azure;

        private GcpEnvironmentParameters gcp;

        private YarnEnvironmentParameters yarn;

        private CumulusEnvironmentParameters cumulus;

        private OpenstackEnvironmentParameters openstack;

        private Builder() {
        }

        public Builder withCrn(String crn) {
            this.crn = crn;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withRegions(CompactRegionResponse regions) {
            this.regions = regions;
            return this;
        }

        public Builder withCloudPlatform(String cloudPlatform) {
            this.cloudPlatform = cloudPlatform;
            return this;
        }

        public Builder withCredential(CredentialResponse credentialResponse) {
            this.credentialResponse = credentialResponse;
            return this;
        }

        public Builder withTelemetry(TelemetryResponse telemetry) {
            this.telemetry = telemetry;
            return this;
        }

        public Builder withLocation(LocationResponse location) {
            this.location = location;
            return this;
        }

        public Builder withNetwork(EnvironmentNetworkResponse network) {
            this.network = network;
            return this;
        }

        public Builder withEnvironmentStatus(EnvironmentStatus environmentStatus) {
            this.environmentStatus = environmentStatus;
            return this;
        }

        public Builder withCreateFreeIpa(boolean createFreeIpa) {
            this.createFreeIpa = createFreeIpa;
            return this;
        }

        public Builder withStatusReason(String statusReason) {
            this.statusReason = statusReason;
            return this;
        }

        public Builder withCreated(Long created) {
            this.created = created;
            return this;
        }

        public Builder withTunnel(Tunnel tunnel) {
            this.tunnel = tunnel;
            return this;
        }

        public Builder withAdminGroupName(String adminGroupName) {
            this.adminGroupName = adminGroupName;
            return this;
        }

        public Builder withAws(AwsEnvironmentParameters aws) {
            this.aws = aws;
            return this;
        }

        public Builder withAzure(AzureEnvironmentParameters azure) {
            this.azure = azure;
            return this;
        }

        public Builder withGcp(GcpEnvironmentParameters gcp) {
            this.gcp = gcp;
            return this;
        }

        public Builder withYarn(YarnEnvironmentParameters yarn) {
            this.yarn = yarn;
            return this;
        }

        public Builder withCumulus(CumulusEnvironmentParameters cumulus) {
            this.cumulus = cumulus;
            return this;
        }

        public Builder withOpenstack(OpenstackEnvironmentParameters openstack) {
            this.openstack = openstack;
            return this;
        }

        public SimpleEnvironmentResponse build() {
            SimpleEnvironmentResponse simpleEnvironmentResponse = new SimpleEnvironmentResponse();
            simpleEnvironmentResponse.setCrn(crn);
            simpleEnvironmentResponse.setName(name);
            simpleEnvironmentResponse.setDescription(description);
            simpleEnvironmentResponse.setRegions(regions);
            simpleEnvironmentResponse.setCloudPlatform(cloudPlatform);
            simpleEnvironmentResponse.setCredential(credentialResponse);
            simpleEnvironmentResponse.setLocation(location);
            simpleEnvironmentResponse.setNetwork(network);
            simpleEnvironmentResponse.setEnvironmentStatus(environmentStatus);
            simpleEnvironmentResponse.setCreateFreeIpa(createFreeIpa);
            simpleEnvironmentResponse.setStatusReason(statusReason);
            simpleEnvironmentResponse.setCreated(created);
            simpleEnvironmentResponse.setTelemetry(telemetry);
            simpleEnvironmentResponse.setTunnel(tunnel);
            simpleEnvironmentResponse.setAws(aws);
            simpleEnvironmentResponse.setAzure(azure);
            simpleEnvironmentResponse.setYarn(yarn);
            simpleEnvironmentResponse.setCumulus(cumulus);
            simpleEnvironmentResponse.setOpenstack(openstack);
            simpleEnvironmentResponse.setGcp(gcp);
            simpleEnvironmentResponse.setAdminGroupName(adminGroupName);
            return simpleEnvironmentResponse;
        }
    }
}
