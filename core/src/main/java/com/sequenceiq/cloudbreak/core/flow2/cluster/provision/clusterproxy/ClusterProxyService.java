package com.sequenceiq.cloudbreak.core.flow2.cluster.provision.clusterproxy;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.clusterproxy.ClusterProxyRegistrationService;
import com.sequenceiq.cloudbreak.clusterproxy.ClusterServiceConfig;
import com.sequenceiq.cloudbreak.clusterproxy.ClusterServiceCredential;
import com.sequenceiq.cloudbreak.clusterproxy.ConfigRegistrationResponse;
import com.sequenceiq.cloudbreak.common.json.JsonUtil;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.domain.stack.cluster.Cluster;
import com.sequenceiq.cloudbreak.service.secret.vault.VaultConfigException;
import com.sequenceiq.cloudbreak.service.secret.vault.VaultSecret;
import com.sequenceiq.cloudbreak.service.stack.StackService;

@Component
public class ClusterProxyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterProxyService.class);

    private StackService stackService;

    private ClusterProxyRegistrationService clusterProxyRegistrationService;

    @Autowired
    ClusterProxyService(StackService stackService, ClusterProxyRegistrationService clusterProxyRegistrationService) {
        this.stackService = stackService;
        this.clusterProxyRegistrationService = clusterProxyRegistrationService;
    }

    public ConfigRegistrationResponse registerCluster(Stack stack) {
        LOGGER.debug("Registering cluster with crn: {}", stack.getResourceCrn());
        List<ClusterServiceConfig> serviceConfigList = asList(createServiceConfig(stack));
        // Registering twice - once with Stack CRN and another time with Cluster Id. This is
        // for backwards compatibility. Will remove this after all consumers start using Stack CRN instead of Cluster Id.
        clusterProxyRegistrationService.registerCluster(clusterId(stack.getCluster()), serviceConfigList, null);
        return clusterProxyRegistrationService.registerCluster(stack.getResourceCrn(), serviceConfigList, null);
    }

    public void registerGatewayConfiguration(Long stackId) {
        Stack stack = stackService.getByIdWithListsInTransaction(stackId);
        if  (!stack.getCluster().hasGateway()) {
            LOGGER.warn("Cluster {} with crn {} in environment {} not configured with Gateway (Knox). Not updating Cluster Proxy with Gateway url.",
                    stack.getCluster().getName(), stack.getResourceCrn(), stack.getEnvironmentCrn());
            return;
        }
        LOGGER.debug("Registering gateway for cluster with crn: {}", stack.getResourceCrn());
        String knoxUri = getProxyConfigFromStack(stack);
        // Registering twice - once with Stack CRN and another time with Cluster Id. This is
        // for backwards compatibility. Will remove this after all consumers start using Stack CRN instead of Cluster Id.
            clusterProxyRegistrationService.registerGateway(clusterId(stack.getCluster()), knoxUri);
            clusterProxyRegistrationService.registerGateway(stack.getResourceCrn(), knoxUri);
    }

    public void deregisterCluster(Stack stack) {
        LOGGER.debug("Deregistering cluster with crn: {}", stack.getResourceCrn());
        // De-registering twice - once with Stack CRN and another time with Cluster Id. This is
        // for backwards compatibility. Will remove this after all consumers start using Stack CRN instead of Cluster Id.
        clusterProxyRegistrationService.deregisterCluster(clusterId(stack.getCluster()));
        clusterProxyRegistrationService.deregisterCluster(stack.getResourceCrn());
    }

    private ClusterServiceConfig createServiceConfig(Stack stack) {
        Cluster cluster = stack.getCluster();

        String cloudbreakUser = cluster.getCloudbreakAmbariUser();
        String cloudbreakPasswordVaultPath = vaultPath(cluster.getCloudbreakAmbariPasswordSecret());

        String dpUser = cluster.getDpAmbariUser();
        String dpPasswordVaultPath = vaultPath(cluster.getDpAmbariPasswordSecret());

        List<ClusterServiceCredential> credentials = asList(new ClusterServiceCredential(cloudbreakUser, cloudbreakPasswordVaultPath),
                new ClusterServiceCredential(dpUser, dpPasswordVaultPath, true));
        return new ClusterServiceConfig("cloudera-manager", singletonList(clusterManagerUrl(stack)), credentials, null);
    }

    private String getProxyConfigFromStack(Stack stack) {
        String gatewayIp = stack.getPrimaryGatewayInstance().getPublicIpWrapper();
        Cluster cluster = stack.getCluster();
        return String.format("https://%s:8443/%s", gatewayIp, cluster.getGateway().getPath());
    }

    private String clusterId(Cluster cluster) {
        return cluster.getId().toString();
    }

    private String clusterManagerUrl(Stack stack) {
        String gatewayIp = stack.getPrimaryGatewayInstance().getPublicIpWrapper();
        return String.format("https://%s/clouderamanager", gatewayIp);
    }

    private String vaultPath(String vaultSecretJsonString) {
        try {
            return JsonUtil.readValue(vaultSecretJsonString, VaultSecret.class).getPath() + ":secret";
        } catch (IOException e) {
            throw new VaultConfigException(String.format("Could not parse vault secret string '%s'", vaultSecretJsonString), e);
        }
    }
}
