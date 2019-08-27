package com.sequenceiq.cloudbreak.cloud.gcp;

import static com.sequenceiq.common.api.type.ResourceType.GCP_NETWORK;
import static com.sequenceiq.common.api.type.ResourceType.GCP_SUBNET;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.Subnetwork;
import com.sequenceiq.cloudbreak.cloud.NetworkConnector;
import com.sequenceiq.cloudbreak.cloud.gcp.util.GcpStackUtil;
import com.sequenceiq.cloudbreak.cloud.model.CloudResource;
import com.sequenceiq.cloudbreak.cloud.model.Platform;
import com.sequenceiq.cloudbreak.cloud.model.Variant;
import com.sequenceiq.cloudbreak.cloud.model.network.CreatedCloudNetwork;
import com.sequenceiq.cloudbreak.cloud.model.network.CreatedSubnet;
import com.sequenceiq.cloudbreak.cloud.model.network.NetworkCreationRequest;
import com.sequenceiq.cloudbreak.cloud.model.network.NetworkDeletionRequest;
import com.sequenceiq.common.api.type.ResourceType;

@Component
public class GcpNetworkConnector extends AbstractGcpResourceBuilder implements NetworkConnector {

    @Inject
    private GcpCloudSubnetProvider gcpCloudSubnetProvider;

    @Override
    public CreatedCloudNetwork createNetworkWithSubnets(NetworkCreationRequest networkRequest) {
        Compute compute = GcpStackUtil.buildCompute(networkRequest.getCloudCredential());
        String projectId = GcpStackUtil.getProjectId(networkRequest.getCloudCredential());
        String networkName = getResourceNameService().resourceName(GCP_NETWORK, networkRequest.getEnvName());
        CloudResource networkNamedResource = createNamedResource(GCP_NETWORK, networkName);
        try {
            createNetwork(networkRequest, compute, projectId, networkNamedResource);

            List<CreatedSubnet> subnetList = getCloudSubNets(networkRequest);
            for (CreatedSubnet createdSubnet : subnetList) {
                String subnetName = getResourceNameService().resourceName(GCP_SUBNET, networkRequest.getEnvName());
                createdSubnet.setSubnetId(subnetName);
                createSubnet(networkRequest, compute, projectId, networkNamedResource, createdSubnet);
            }
            return new CreatedCloudNetwork(networkRequest.getEnvName(), networkName, getCreatedSubnets(subnetList));
        } catch (GoogleJsonResponseException e) {
            throw new GcpResourceException(checkException(e), GCP_NETWORK, networkRequest.getEnvName());
        } catch (IOException e) {
            throw new GcpResourceException("sdfsdfsdfsdfsdf", GCP_NETWORK, networkRequest.getEnvName());
        }
    }

    private void createSubnet(NetworkCreationRequest networkRequest, Compute compute, String projectId, CloudResource network, CreatedSubnet createdSubnet) throws IOException {
        CloudResource subnetResource = createNamedResource(GCP_SUBNET, createdSubnet.getSubnetId());

        Subnetwork gcpSubnet = new Subnetwork();
        gcpSubnet.setName(createdSubnet.getSubnetId());
        gcpSubnet.setIpCidrRange(createdSubnet.getCidr());
        String networkName = network.getName();
        gcpSubnet.setNetwork(String.format("https://www.googleapis.com/compute/v1/projects/%s/global/networks/%s", projectId, networkName));
        Compute.Subnetworks.Insert snInsert = compute.subnetworks().insert(projectId, networkRequest.getRegion().getRegionName(), gcpSubnet);
        try {
            Operation operation = snInsert.execute();
            if (operation.getHttpErrorStatusCode() != null) {
                throw new GcpResourceException(operation.getHttpErrorMessage(), GCP_SUBNET, createdSubnet.getSubnetId());
            }
            createOperationAwareCloudResource(subnetResource, operation);
        } catch (GoogleJsonResponseException e) {
            throw new GcpResourceException(checkException(e), GCP_SUBNET, createdSubnet.getSubnetId());
        }
    }

    private void createNetwork(NetworkCreationRequest networkRequest, Compute compute, String projectId, CloudResource namedResource) throws IOException {
        com.google.api.services.compute.model.Network gcpNetwork = new com.google.api.services.compute.model.Network();
        gcpNetwork.setName(namedResource.getName());
        gcpNetwork.setAutoCreateSubnetworks(false);

        Compute.Networks.Insert networkInsert = compute.networks().insert(projectId, gcpNetwork);
        Operation operation = networkInsert.execute();
        if (operation.getHttpErrorStatusCode() != null) {
            throw new GcpResourceException(operation.getHttpErrorMessage(),
                    GCP_NETWORK, networkRequest.getStackName());
        }

        createOperationAwareCloudResource(namedResource, operation);
    }

    @Override
    public void deleteNetworkWithSubnets(NetworkDeletionRequest networkDeletionRequest) {
        Compute compute = GcpStackUtil.buildCompute(networkDeletionRequest.getCloudCredential());
        String projectId = GcpStackUtil.getProjectId(networkDeletionRequest.getCloudCredential());

        try {
            for (String subnetId : networkDeletionRequest.getSubnetIds()) {
                deleteSubnet(networkDeletionRequest, compute, projectId, subnetId);
            }
            deleteNetwork(networkDeletionRequest, compute, projectId);
        } catch (GoogleJsonResponseException e) {
            exceptionHandler(e, networkDeletionRequest.getStackName(), GCP_NETWORK);
        } catch (IOException e) {
            throw new GcpResourceException("sdfsdfsdfsdfsdf", GCP_NETWORK, networkDeletionRequest.getStackName());
        }
    }

    private void deleteNetwork(NetworkDeletionRequest networkDeletionRequest, Compute compute, String projectId) throws IOException {
        CloudResource networkResource = createNamedResource(GCP_NETWORK, networkDeletionRequest.getNetworkId());
        Operation operation = compute.networks().delete(projectId, networkDeletionRequest.getNetworkId()).execute();
        if (operation.getHttpErrorStatusCode() != null) {
            throw new GcpResourceException(operation.getHttpErrorMessage(), GCP_NETWORK, networkDeletionRequest.getNetworkId());
        }
        createOperationAwareCloudResource(networkResource, operation);
    }

    private void deleteSubnet(NetworkDeletionRequest networkDeletionRequest, Compute compute, String projectId, String subnetId) throws IOException {
        CloudResource subnetResource = createNamedResource(GCP_SUBNET, subnetId);
        Operation operation = compute.subnetworks().delete(projectId, networkDeletionRequest.getRegion(), subnetId).execute();
        createOperationAwareCloudResource(subnetResource, operation);

    }

    @Override
    public Platform platform() {
        return GcpConstants.GCP_PLATFORM;
    }

    @Override
    public Variant variant() {
        return GcpConstants.GCP_VARIANT;
    }

    private List<CreatedSubnet> getCloudSubNets(NetworkCreationRequest networkRequest) throws IOException {
        return gcpCloudSubnetProvider.provide(networkRequest, new ArrayList<>(networkRequest.getSubnetCidrs()));
    }

    protected CloudResource createNamedResource(ResourceType type, String name) {
        return new CloudResource.Builder().type(type).name(name).build();
    }

    protected String checkException(GoogleJsonResponseException execute) {
        return execute.getDetails().getMessage();
    }

    private Set<CreatedSubnet> getCreatedSubnets(List<CreatedSubnet> createdSubnetList) {
        Set<CreatedSubnet> subnets = new HashSet<>();
        for (int i = 0; i < createdSubnetList.size(); i++) {
            CreatedSubnet createdSubnetIndexed = createdSubnetList.get(i);
            CreatedSubnet createdSubnet = new CreatedSubnet();
            createdSubnet.setSubnetId(createdSubnetIndexed.getSubnetId());
            createdSubnet.setCidr(createdSubnetList.get(i).getCidr());
            createdSubnet.setAvailabilityZone(createdSubnetList.get(i).getAvailabilityZone());
            createdSubnet.setPrivateSubnet(false);
            createdSubnet.setMapPublicIpOnLaunch(true);
            createdSubnet.setIgwAvailable(true);
            subnets.add(createdSubnet);
        }
        return subnets;
    }
}
