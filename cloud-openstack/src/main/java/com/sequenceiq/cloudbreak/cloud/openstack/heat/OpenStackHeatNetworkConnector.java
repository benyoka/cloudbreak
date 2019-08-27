package com.sequenceiq.cloudbreak.cloud.openstack.heat;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.NetworkConnector;
import com.sequenceiq.cloudbreak.cloud.model.Platform;
import com.sequenceiq.cloudbreak.cloud.model.Variant;
import com.sequenceiq.cloudbreak.cloud.model.network.CreatedCloudNetwork;
import com.sequenceiq.cloudbreak.cloud.model.network.NetworkCreationRequest;
import com.sequenceiq.cloudbreak.cloud.model.network.NetworkDeletionRequest;
import com.sequenceiq.cloudbreak.cloud.openstack.common.OpenStackConstants;

@Component
public class OpenStackHeatNetworkConnector implements NetworkConnector {
    @Override
    public CreatedCloudNetwork createNetworkWithSubnets(NetworkCreationRequest networkCreationRequest) {
        return null;
    }

    @Override
    public void deleteNetworkWithSubnets(NetworkDeletionRequest networkDeletionRequest) {

    }

    @Override
    public Platform platform() {
        return OpenStackConstants.OPENSTACK_PLATFORM;
    }

    @Override
    public Variant variant() {
        return OpenStackConstants.OpenStackVariant.HEAT.variant();
    }
}
