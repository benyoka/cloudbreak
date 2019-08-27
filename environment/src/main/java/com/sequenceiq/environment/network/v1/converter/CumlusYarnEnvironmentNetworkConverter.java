package com.sequenceiq.environment.network.v1.converter;

import static com.sequenceiq.environment.CloudPlatform.CUMULUS_YARN;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.model.network.CreatedCloudNetwork;
import com.sequenceiq.environment.CloudPlatform;
import com.sequenceiq.environment.network.dao.domain.BaseNetwork;
import com.sequenceiq.environment.network.dao.domain.CumulusYarnNetwork;
import com.sequenceiq.environment.network.dao.domain.RegistrationType;
import com.sequenceiq.environment.network.dto.CumulusYarnParams;
import com.sequenceiq.environment.network.dto.NetworkDto;

@Component
public class CumlusYarnEnvironmentNetworkConverter extends EnvironmentBaseNetworkConverter {

    @Override
    BaseNetwork createProviderSpecificNetwork(NetworkDto network) {
        CumulusYarnNetwork cumulusYarnNetwork = new CumulusYarnNetwork();
        if (network.getCumulus() != null) {
            cumulusYarnNetwork.setQueue(network.getCumulus().getQueue());
        }
        return cumulusYarnNetwork;
    }

    @Override
    public BaseNetwork setProviderSpecificNetwork(BaseNetwork baseNetwork, CreatedCloudNetwork createdCloudNetwork) {
        CumulusYarnNetwork cumulusYarnNetwork = (CumulusYarnNetwork) baseNetwork;
        Map<String, Object> properties = createdCloudNetwork.getProperties();
        cumulusYarnNetwork.setQueue((String) properties.getOrDefault("queue", null));
        return cumulusYarnNetwork;
    }

    @Override
    NetworkDto setProviderSpecificFields(NetworkDto.Builder builder, BaseNetwork network) {
        CumulusYarnNetwork cumulusYarnNetwork = (CumulusYarnNetwork) network;
        return builder.withCumulus(
                CumulusYarnParams.CumulusYarnParamsBuilder.aCumulusYarnParams()
                        .withQueue(cumulusYarnNetwork.getQueue())
                        .build())
                .build();
    }

    @Override
    void setRegistrationType(BaseNetwork result, NetworkDto networkDto) {
        result.setRegistrationType(RegistrationType.CREATE_NEW);
    }

    @Override
    public CloudPlatform getCloudPlatform() {
        return CUMULUS_YARN;
    }

    @Override
    public boolean hasExistingNetwork(BaseNetwork baseNetwork) {
        return false;
    }
}
