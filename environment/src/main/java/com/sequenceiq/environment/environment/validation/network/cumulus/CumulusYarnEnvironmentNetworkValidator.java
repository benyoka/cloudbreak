package com.sequenceiq.environment.environment.validation.network.cumulus;

import static com.sequenceiq.environment.CloudPlatform.CUMULUS_YARN;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.util.ValidationResult;
import com.sequenceiq.environment.CloudPlatform;
import com.sequenceiq.environment.environment.validation.network.EnvironmentNetworkValidator;
import com.sequenceiq.environment.network.dto.NetworkDto;

@Component
public class CumulusYarnEnvironmentNetworkValidator implements EnvironmentNetworkValidator {
    @Override
    public void validate(NetworkDto networkDto, ValidationResult.ValidationResultBuilder resultBuilder) {
        if (networkDto != null) {

        }
    }

    @Override
    public CloudPlatform getCloudPlatform() {
        return CUMULUS_YARN;
    }

}
