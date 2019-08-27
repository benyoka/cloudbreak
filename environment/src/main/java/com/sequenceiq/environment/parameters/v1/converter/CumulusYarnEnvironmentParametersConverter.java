package com.sequenceiq.environment.parameters.v1.converter;

import static com.sequenceiq.environment.CloudPlatform.CUMULUS_YARN;

import org.springframework.stereotype.Component;

import com.sequenceiq.environment.CloudPlatform;
import com.sequenceiq.environment.environment.domain.Environment;
import com.sequenceiq.environment.parameters.dao.domain.BaseParameters;
import com.sequenceiq.environment.parameters.dao.domain.CumulusYarnParameters;
import com.sequenceiq.environment.parameters.dto.CumulusYarnParametersDto;
import com.sequenceiq.environment.parameters.dto.ParametersDto;
import com.sequenceiq.environment.parameters.dto.ParametersDto.Builder;

@Component
public class CumulusYarnEnvironmentParametersConverter extends BaseEnvironmentParametersConverter {

    @Override
    protected BaseParameters createInstance() {
        return new CumulusYarnParameters();
    }

    @Override
    public CloudPlatform getCloudPlatform() {
        return CUMULUS_YARN;
    }

    @Override
    protected void postConvert(BaseParameters baseParameters, Environment environment, ParametersDto parametersDto) {
        super.postConvert(baseParameters, environment, parametersDto);
    }

    @Override
    protected void postConvertToDto(Builder builder, BaseParameters source) {
        super.postConvertToDto(builder, source);
        builder.withCumulusYarnParameters(CumulusYarnParametersDto.builder().build());
    }
}
