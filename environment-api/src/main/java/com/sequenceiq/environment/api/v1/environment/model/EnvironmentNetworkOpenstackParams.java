package com.sequenceiq.environment.api.v1.environment.model;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sequenceiq.environment.api.doc.environment.EnvironmentModelDescription;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "EnvironmentNetworkOpenstackV1Params")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class EnvironmentNetworkOpenstackParams {

    @Size(max = 255)
    @ApiModelProperty(value = EnvironmentModelDescription.AWS_VPC_ID, required = true)
    private String vpcId;

    public String getVpcId() {
        return vpcId;
    }

    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }

    public static final class EnvironmentNetworkOpenstackParamsBuilder {
        private String vpcId;

        private EnvironmentNetworkOpenstackParamsBuilder() {
        }

        public static EnvironmentNetworkOpenstackParamsBuilder anEnvironmentNetworkOpenstackParamsBuilder() {
            return new EnvironmentNetworkOpenstackParamsBuilder();
        }

        public EnvironmentNetworkOpenstackParamsBuilder withVpcId(String vpcId) {
            this.vpcId = vpcId;
            return this;
        }

        public EnvironmentNetworkOpenstackParams build() {
            EnvironmentNetworkOpenstackParams environmentNetworkAwsParams = new EnvironmentNetworkOpenstackParams();
            environmentNetworkAwsParams.setVpcId(vpcId);
            return environmentNetworkAwsParams;
        }
    }
}
