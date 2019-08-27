package com.sequenceiq.environment.api.v1.environment.model.request;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import com.sequenceiq.common.api.telemetry.request.TelemetryRequest;
import com.sequenceiq.environment.api.doc.ModelDescriptions;
import com.sequenceiq.environment.api.doc.environment.EnvironmentModelDescription;
import com.sequenceiq.environment.api.v1.environment.model.base.IdBrokerMappingSource;
import com.sequenceiq.environment.api.v1.environment.model.request.aws.AwsEnvironmentParameters;
import com.sequenceiq.environment.api.v1.environment.model.request.azure.AzureEnvironmentParameters;
import com.sequenceiq.environment.api.v1.environment.model.request.cumulus.CumulusEnvironmentParameters;
import com.sequenceiq.environment.api.v1.environment.model.request.gcp.GcpEnvironmentParameters;
import com.sequenceiq.environment.api.v1.environment.model.request.openstack.OpenstackEnvironmentParameters;
import com.sequenceiq.environment.api.v1.environment.model.request.yarn.YarnEnvironmentParameters;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "EnvironmentEditV1Request")
public class EnvironmentEditRequest {

    @Size(max = 1000)
    @ApiModelProperty(ModelDescriptions.DESCRIPTION)
    private String description;

    @ApiModelProperty(EnvironmentModelDescription.REGIONS)
    private Set<String> regions = new HashSet<>();

    @ApiModelProperty(EnvironmentModelDescription.LOCATION)
    private LocationRequest location;

    @ApiModelProperty(EnvironmentModelDescription.NETWORK)
    private EnvironmentNetworkRequest network;

    @ApiModelProperty(EnvironmentModelDescription.AUTHENTICATION)
    private @Valid EnvironmentAuthenticationRequest authentication;

    @ApiModelProperty(EnvironmentModelDescription.TELEMETRY)
    private TelemetryRequest telemetry;

    @ApiModelProperty(EnvironmentModelDescription.SECURITY_ACCESS)
    private @Valid SecurityAccessRequest securityAccess;

    @ApiModelProperty(EnvironmentModelDescription.IDBROKER_MAPPING_SOURCE)
    private IdBrokerMappingSource idBrokerMappingSource;

    @ApiModelProperty(EnvironmentModelDescription.ADMIN_GROUP_NAME)
    private String adminGroupName;

    @Valid
    @ApiModelProperty(EnvironmentModelDescription.AWS_PARAMETERS)
    private AwsEnvironmentParameters aws;

    @Valid
    @ApiModelProperty(EnvironmentModelDescription.AZURE_PARAMETERS)
    private AzureEnvironmentParameters azure;

    @Valid
    @ApiModelProperty(EnvironmentModelDescription.GCP_PARAMETERS)
    private GcpEnvironmentParameters gcp;

    @Valid
    @ApiModelProperty(EnvironmentModelDescription.YARN_PARAMETERS)
    private YarnEnvironmentParameters yarn;

    @Valid
    @ApiModelProperty(EnvironmentModelDescription.CUMULUS_PARAMETERS)
    private CumulusEnvironmentParameters cumulus;

    @Valid
    @ApiModelProperty(EnvironmentModelDescription.OPENSTACK_PARAMETERS)
    private OpenstackEnvironmentParameters openstack;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getRegions() {
        return regions;
    }

    public void setRegions(Set<String> regions) {
        this.regions = regions;
    }

    public LocationRequest getLocation() {
        return location;
    }

    public void setLocation(LocationRequest location) {
        this.location = location;
    }

    public EnvironmentNetworkRequest getNetwork() {
        return network;
    }

    public void setNetwork(EnvironmentNetworkRequest network) {
        this.network = network;
    }

    public EnvironmentAuthenticationRequest getAuthentication() {
        return authentication;
    }

    public void setAuthentication(EnvironmentAuthenticationRequest authentication) {
        this.authentication = authentication;
    }

    public TelemetryRequest getTelemetry() {
        return telemetry;
    }

    public void setTelemetry(TelemetryRequest telemetry) {
        this.telemetry = telemetry;
    }

    public SecurityAccessRequest getSecurityAccess() {
        return securityAccess;
    }

    public void setSecurityAccess(SecurityAccessRequest securityAccess) {
        this.securityAccess = securityAccess;
    }

    public IdBrokerMappingSource getIdBrokerMappingSource() {
        return idBrokerMappingSource;
    }

    public void setIdBrokerMappingSource(IdBrokerMappingSource idBrokerMappingSource) {
        this.idBrokerMappingSource = idBrokerMappingSource;
    }

    public String getAdminGroupName() {
        return adminGroupName;
    }

    public void setAdminGroupName(String adminGroupName) {
        this.adminGroupName = adminGroupName;
    }

    public AwsEnvironmentParameters getAws() {
        return aws;
    }

    public void setAws(AwsEnvironmentParameters aws) {
        this.aws = aws;
    }

    public AzureEnvironmentParameters getAzure() {
        return azure;
    }

    public void setAzure(AzureEnvironmentParameters azure) {
        this.azure = azure;
    }

    public GcpEnvironmentParameters getGcp() {
        return gcp;
    }

    public void setGcp(GcpEnvironmentParameters gcp) {
        this.gcp = gcp;
    }

    public YarnEnvironmentParameters getYarn() {
        return yarn;
    }

    public void setYarn(YarnEnvironmentParameters yarn) {
        this.yarn = yarn;
    }

    public CumulusEnvironmentParameters getCumulus() {
        return cumulus;
    }

    public void setCumulus(CumulusEnvironmentParameters cumulus) {
        this.cumulus = cumulus;
    }

    public OpenstackEnvironmentParameters getOpenstack() {
        return openstack;
    }

    public void setOpenstack(OpenstackEnvironmentParameters openstack) {
        this.openstack = openstack;
    }
}
