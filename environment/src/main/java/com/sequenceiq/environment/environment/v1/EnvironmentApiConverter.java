package com.sequenceiq.environment.environment.v1;

import static com.sequenceiq.cloudbreak.util.NullUtil.getIfNotNull;
import static com.sequenceiq.environment.environment.dto.EnvironmentChangeCredentialDto.EnvironmentChangeCredentialDtoBuilder.anEnvironmentChangeCredentialDto;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.auth.ThreadBasedUserCrnProvider;
import com.sequenceiq.cloudbreak.util.NullUtil;
import com.sequenceiq.environment.api.v1.environment.model.EnvironmentNetworkAwsParams;
import com.sequenceiq.environment.api.v1.environment.model.EnvironmentNetworkAzureParams;
import com.sequenceiq.environment.api.v1.environment.model.request.EnvironmentChangeCredentialRequest;
import com.sequenceiq.environment.api.v1.environment.model.request.EnvironmentEditRequest;
import com.sequenceiq.environment.api.v1.environment.model.request.EnvironmentNetworkRequest;
import com.sequenceiq.environment.api.v1.environment.model.request.EnvironmentRequest;
import com.sequenceiq.environment.api.v1.environment.model.request.LocationRequest;
import com.sequenceiq.environment.api.v1.environment.model.response.DetailedEnvironmentResponse;
import com.sequenceiq.environment.api.v1.environment.model.response.EnvironmentNetworkResponse;
import com.sequenceiq.environment.api.v1.environment.model.response.EnvironmentStatus;
import com.sequenceiq.environment.api.v1.environment.model.response.LocationResponse;
import com.sequenceiq.environment.environment.dto.EnvironmentChangeCredentialDto;
import com.sequenceiq.environment.environment.dto.EnvironmentCreationDto;
import com.sequenceiq.environment.environment.dto.EnvironmentDto;
import com.sequenceiq.environment.environment.dto.EnvironmentEditDto;
import com.sequenceiq.environment.environment.dto.LocationDto;
import com.sequenceiq.environment.environment.v1.converter.RegionConverter;
import com.sequenceiq.environment.network.dto.AwsParams;
import com.sequenceiq.environment.network.dto.AzureParams;
import com.sequenceiq.environment.network.dto.NetworkDto;

@Component
public class EnvironmentApiConverter {

    private final ThreadBasedUserCrnProvider threadBasedUserCrnProvider;

    private final RegionConverter regionConverter;

    public EnvironmentApiConverter(ThreadBasedUserCrnProvider threadBasedUserCrnProvider, RegionConverter regionConverter) {
        this.threadBasedUserCrnProvider = threadBasedUserCrnProvider;
        this.regionConverter = regionConverter;
    }

    public EnvironmentCreationDto initCreationDto(EnvironmentRequest request) {
        EnvironmentCreationDto.EnvironmentCreationDtoBuilder builder = EnvironmentCreationDto.EnvironmentCreationDtoBuilder.anEnvironmentCreationDto()
                .withAccountId(threadBasedUserCrnProvider.getAccountId())
                .withName(request.getName())
                .withDescription(request.getDescription())
                .withCloudPlatform(request.getCloudPlatform())
                .withCredential(request)
                .withLocation(locationRequestToDto(request.getLocation()))
                .withRegions(request.getRegions());

        NullUtil.doIfNotNull(request.getNetwork(), network -> builder.withNetwork(networkRequestToDto(network)));
        return builder.build();
    }

    public LocationDto locationRequestToDto(LocationRequest location) {
        return LocationDto.LocationDtoBuilder.aLocationDto()
                .withName(location.getName())
                .withLatitude(location.getLatitude())
                .withLongitude(location.getLongitude())
                .withDisplayName(location.getName())
                .build();
    }

    public NetworkDto networkRequestToDto(EnvironmentNetworkRequest network) {
        NetworkDto.NetworkDtoBuilder builder = NetworkDto.NetworkDtoBuilder.aNetworkDto();
        if (network.getAws() != null) {
            AwsParams awsParams = new AwsParams();
            awsParams.setVpcId(network.getAws().getVpcId());
            builder.withAws(awsParams);
        }
        if (network.getAzure() != null) {
            AzureParams azureParams = new AzureParams();
            azureParams.setNetworkId(network.getAzure().getNetworkId());
            azureParams.setNoFirewallRules(network.getAzure().getNoFirewallRules());
            azureParams.setNoPublicIp(network.getAzure().getNoPublicIp());
            azureParams.setResourceGroupName(network.getAzure().getResourceGroupName());
            builder.withAzure(azureParams);
        }
        return builder
                .withSubnetIds(network.getSubnetIds())
                .build();
    }

    public DetailedEnvironmentResponse dtoToDetailedResponse(EnvironmentDto environmentDto) {
        DetailedEnvironmentResponse.Builder builder = DetailedEnvironmentResponse.Builder.aDetailedEnvironmentResponse()
                .withCrn(environmentDto.getResourceCrn())
                .withName(environmentDto.getName())
                .withDescription(environmentDto.getDescription())
                .withCloudPlatform(environmentDto.getCloudPlatform())
                .withCredentialName(environmentDto.getCredential().getName())
                .withEnvironmentStatus(convertEnvStatus(environmentDto.getEnvironmentStatus()))
                .withLocation(locationDtoToResponse(environmentDto.getLocation()))
                .withRegions(regionConverter.convertRegions(environmentDto.getRegionSet()));

        NullUtil.doIfNotNull(environmentDto.getNetwork(), network -> builder.withNetwork(networkDtoToResponse(network)));
        return builder.build();
    }

    public EnvironmentNetworkResponse networkDtoToResponse(NetworkDto network) {
        return EnvironmentNetworkResponse.EnvironmentNetworkResponseBuilder.anEnvironmentNetworkResponse()
                .withCrn(network.getResourceCrn())
                .withSubnetIds(network.getSubnetIds())
                .withSubnetMetas(network.getSubnetMetas())
                .withAws(EnvironmentNetworkAwsParams.EnvironmentNetworkAwsParamsBuilder.anEnvironmentNetworkAwsParams()
                        .withVpcId(getIfNotNull(network.getAws(), AwsParams::getVpcId))
                        .build())
                .withAzure(EnvironmentNetworkAzureParams.EnvironmentNetworkAzureParamsBuilder.anEnvironmentNetworkAzureParams()
                        .withNetworkId(getIfNotNull(network.getAzure(), AzureParams::getNetworkId))
                        .withResourceGroupName(getIfNotNull(network.getAzure(), AzureParams::getResourceGroupName))
                        .withNoFirewallRules(getIfNotNull(network.getAzure(), AzureParams::isNoFirewallRules))
                        .withNoPublicIp(getIfNotNull(network.getAzure(), AzureParams::isNoPublicIp))
                        .build())
                .build();
    }

    public LocationResponse locationDtoToResponse(LocationDto locationDto) {
        return LocationResponse.LocationResponseBuilder.aLocationResponse()
                .withName(locationDto.getName())
                .withDisplayName(locationDto.getDisplayName())
                .withLatitude(locationDto.getLatitude())
                .withLongitude(locationDto.getLongitude())
                .build();
    }

    public EnvironmentStatus convertEnvStatus(com.sequenceiq.environment.environment.EnvironmentStatus environmentStatus) {
        switch (environmentStatus) {
            case ARCHIVED:
                return EnvironmentStatus.ARCHIVED;
            case AVAILABLE:
                return EnvironmentStatus.AVAILABLE;
            case CORRUPTED:
                return EnvironmentStatus.CORRUPTED;
            case CREATION_INITIATED:
                return EnvironmentStatus.CREATION_INITIATED;
            case RDBMS_CREATION_IN_PROGRESS:
                return EnvironmentStatus.RDBMS_CREATION_IN_PROGRESS;
            case DELETE_INITIATED:
                return EnvironmentStatus.DELETE_INITIATED;
            case RDBMS_DELETE_IN_PROGRESS:
                return EnvironmentStatus.RDBMS_DELETE_IN_PROGRESS;
            case FREEIPA_DELETE_IN_PROGRESS:
                return EnvironmentStatus.FREEIPA_DELETE_IN_PROGRESS;
            case NETWORK_DELETE_IN_PROGRESS:
                return EnvironmentStatus.NETWORK_DELETE_IN_PROGRESS;
            case FREEIPA_CREATION_IN_PROGRESS:
                return EnvironmentStatus.FREEIPA_CREATION_IN_PROGRESS;
            default:
            case NETWORK_CREATION_IN_PROGRESS:
                return EnvironmentStatus.NETWORK_CREATION_IN_PROGRESS;
        }
    }

    public EnvironmentEditDto initEditDto(EnvironmentEditRequest request) {
        EnvironmentEditDto.EnvironmentEditDtoBuilder builder = EnvironmentEditDto.EnvironmentEditDtoBuilder.anEnvironmentEditDto()
                .withDescription(request.getDescription())
                .withAccountId(threadBasedUserCrnProvider.getAccountId())
                .withRegions(request.getRegions());
        NullUtil.doIfNotNull(request.getNetwork(), network -> builder.withNetwork(networkRequestToDto(network)));
        NullUtil.doIfNotNull(request.getLocation(), location -> builder.withLocation(locationRequestToDto(location)));
        return builder.build();
    }

    public EnvironmentChangeCredentialDto convertEnvironmentChangeCredentialDto(EnvironmentChangeCredentialRequest request) {
        return anEnvironmentChangeCredentialDto()
                .withCredentialName(request.getCredential() != null ? request.getCredential().getName() : request.getCredentialName())
                .build();
    }

}
