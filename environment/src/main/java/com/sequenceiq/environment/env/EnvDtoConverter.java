package com.sequenceiq.environment.env;

import org.springframework.stereotype.Component;

import com.sequenceiq.environment.api.environment.model.request.EnvironmentV1Request;
import com.sequenceiq.environment.api.environment.model.response.DetailedEnvironmentV1Response;
import com.sequenceiq.environment.env.service.EnvironmentDto;
import com.sequenceiq.environment.env.service.EnvironmentStatus;
import com.sequenceiq.environment.network.NetworkDto;

@Component
public class EnvDtoConverter {

    public EnvironmentDto requestToDto(EnvironmentV1Request request) {
        return EnvironmentDto.EnvironmentDtoBuilder.anEnvironmentDto()
                .withName(request.getName())
                .withCloudPlatform(request.getCloudPlatform())
                .withStatus(EnvironmentStatus.CREATION_INITIATED)
                .withVpcDto(NetworkDto.NetworkDtoBuilder.aVpcDto()
                        .withStatus("Hello")
                        .build())
                .build();
    }

    public DetailedEnvironmentV1Response dtoToResponse(EnvironmentDto created) {
        return DetailedEnvironmentV1Response.DetailedEnvironmentV1ResponseBuilder.aDetailedEnvironmentV1Response()
                .withName(created.getName())
                .withId(created.getId())
                .withCloudPlatform(created.getCloudPlatform())
                .build();
    }
}
