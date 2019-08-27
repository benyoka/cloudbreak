package com.sequenceiq.environment.api.v1.environment.model.request.cumulus;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "CumulusEnvironmentV1Parameters")
public class CumulusEnvironmentParameters {

    public CumulusEnvironmentParameters() {
    }

    private CumulusEnvironmentParameters(CumulusEnvironmentParameters.Builder builder) {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        public CumulusEnvironmentParameters build() {
            return new CumulusEnvironmentParameters(this);
        }
    }
}
