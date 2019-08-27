package com.sequenceiq.environment.network.v1.converter;

import static com.sequenceiq.environment.CloudPlatform.GCP;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.model.CloudSubnet;
import com.sequenceiq.cloudbreak.cloud.model.network.CreatedCloudNetwork;
import com.sequenceiq.cloudbreak.cloud.model.network.CreatedSubnet;
import com.sequenceiq.environment.CloudPlatform;
import com.sequenceiq.environment.network.dao.domain.BaseNetwork;
import com.sequenceiq.environment.network.dao.domain.GcpNetwork;
import com.sequenceiq.environment.network.dao.domain.RegistrationType;
import com.sequenceiq.environment.network.dto.GcpParams;
import com.sequenceiq.environment.network.dto.NetworkDto;

@Component
public class GcpEnvironmentNetworkConverter extends EnvironmentBaseNetworkConverter {

    @Override
    BaseNetwork createProviderSpecificNetwork(NetworkDto network) {
        GcpNetwork gcpNetwork = new GcpNetwork();
        GcpParams gcpParams = network.getGcp();
        if (gcpParams != null) {
            gcpNetwork.setNetworkId(gcpParams.getNetworkId());
            gcpNetwork.setNoFirewallRules(gcpParams.getNoFirewallRules());
            gcpNetwork.setNoPublicIp(gcpParams.getNoPublicIp());
            gcpNetwork.setSharedProjectId(gcpParams.getSharedProjectId());
        }
        return gcpNetwork;
    }

    @Override
    public BaseNetwork setProviderSpecificNetwork(BaseNetwork baseNetwork, CreatedCloudNetwork createdCloudNetwork) {
        GcpNetwork gcpNetwork = (GcpNetwork) baseNetwork;
        gcpNetwork.setNetworkId(gcpNetwork.getNetworkId());

        Map<String, Object> properties = createdCloudNetwork.getProperties();
        if (properties == null) {
            properties = new HashMap<>();
        }

        Object sharedProjectId = properties.get("sharedProjectId");
        gcpNetwork.setSharedProjectId(sharedProjectId == null ? null : sharedProjectId.toString());

        Object noFirewallRules = properties.get("noFirewallRules");
        gcpNetwork.setNoFirewallRules(noFirewallRules == null ? false : Boolean.valueOf(noFirewallRules.toString()));

        Object noPublicIp = properties.get("noPublicIp");
        gcpNetwork.setNoPublicIp(noPublicIp == null ? false : Boolean.valueOf(noPublicIp.toString()));

        gcpNetwork.setSubnetIds(createdCloudNetwork.getSubnets().stream().map(CreatedSubnet::getSubnetId).collect(Collectors.toSet()));

        gcpNetwork.setSubnetMetas(createdCloudNetwork.getSubnets().stream()
                .collect(Collectors.toMap(
                        CreatedSubnet::getSubnetId, subnet -> new CloudSubnet(
                                subnet.getSubnetId(),
                                subnet.getSubnetId(),
                                subnet.getAvailabilityZone(),
                                subnet.getCidr(),
                                subnet.isPrivateSubnet(),
                                subnet.isMapPublicIpOnLaunch(),
                                subnet.isIgwAvailable())
                        )
                )
        );
        return gcpNetwork;
    }

    @Override
    NetworkDto setProviderSpecificFields(NetworkDto.Builder builder, BaseNetwork network) {
        GcpNetwork gcpNetwork = (GcpNetwork) network;
        GcpParams.GcpParamsBuilder gcpParamsBuilder = GcpParams.GcpParamsBuilder.aGcpParams();
        Optional.ofNullable(gcpNetwork.getNetworkId()).ifPresent(gcpParamsBuilder::withNetworkId);
        Optional.ofNullable(gcpNetwork.getNoFirewallRules()).ifPresent(gcpParamsBuilder::withNoFirewallRules);
        Optional.ofNullable(gcpNetwork.getNoPublicIp()).ifPresent(gcpParamsBuilder::withNoPublicIp);
        Optional.ofNullable(gcpNetwork.getSharedProjectId()).ifPresent(gcpParamsBuilder::withSharedProjectId);
        return builder
                .withGcp(gcpParamsBuilder.build())
                .build();
    }

    @Override
    void setRegistrationType(BaseNetwork result, NetworkDto networkDto) {
        if (isExistingNetworkSpecified(networkDto)) {
            result.setRegistrationType(RegistrationType.EXISTING);
        } else {
            result.setRegistrationType(RegistrationType.CREATE_NEW);
        }
    }

    private boolean isExistingNetworkSpecified(NetworkDto networkDto) {
        return networkDto.getGcp() != null && networkDto.getGcp().getNetworkId() != null;
    }

    @Override
    public CloudPlatform getCloudPlatform() {
        return GCP;
    }

    @Override
    public boolean hasExistingNetwork(BaseNetwork baseNetwork) {
        return Optional.ofNullable((GcpNetwork) baseNetwork).map(GcpNetwork::getNetworkId).isPresent();
    }
}
