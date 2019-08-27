package com.sequenceiq.environment.api.v1.environment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sequenceiq.environment.api.doc.environment.EnvironmentModelDescription;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "EnvironmentNetworkCumulusYarnV1Params")
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnvironmentNetworkCumulusYarnParams {
    @ApiModelProperty(value = EnvironmentModelDescription.QUEUE, required = true)
    private String queue;

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public static final class EnvironmentNetworkCumulusYarnParamsBuilder {
        private String queue;

        private EnvironmentNetworkCumulusYarnParamsBuilder() {
        }

        public static EnvironmentNetworkCumulusYarnParamsBuilder anEnvironmentNetworkCumulusYarnParams() {
            return new EnvironmentNetworkCumulusYarnParamsBuilder();
        }

        public EnvironmentNetworkCumulusYarnParamsBuilder withQueue(String queue) {
            this.queue = queue;
            return this;
        }

        public EnvironmentNetworkCumulusYarnParams build() {
            EnvironmentNetworkCumulusYarnParams environmentNetworkYarnParams = new EnvironmentNetworkCumulusYarnParams();
            environmentNetworkYarnParams.setQueue(queue);
            return environmentNetworkYarnParams;
        }
    }
}
