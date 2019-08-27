package com.sequenceiq.environment.api.v1.environment.model;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sequenceiq.environment.api.doc.environment.EnvironmentModelDescription;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "EnvironmentNetworkGcpV1Params")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class EnvironmentNetworkGcpParams {

    @Size(max = 255)
    @ApiModelProperty(value = EnvironmentModelDescription.AWS_VPC_ID, required = true)
    private String vpcId;

    public String getVpcId() {
        return vpcId;
    }

    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }

    public static final class EnvironmentNetworkGcpParamsBuilder {
        private String vpcId;

        private EnvironmentNetworkGcpParamsBuilder() {
        }

        public static EnvironmentNetworkGcpParamsBuilder anEnvironmentNetworkGcpParamsBuilder() {
            return new EnvironmentNetworkGcpParamsBuilder();
        }

        public EnvironmentNetworkGcpParamsBuilder withVpcId(String vpcId) {
            this.vpcId = vpcId;
            return this;
        }

        public EnvironmentNetworkGcpParams build() {
            EnvironmentNetworkGcpParams environmentNetworkAwsParams = new EnvironmentNetworkGcpParams();
            environmentNetworkAwsParams.setVpcId(vpcId);
            return environmentNetworkAwsParams;
        }
    }
}
