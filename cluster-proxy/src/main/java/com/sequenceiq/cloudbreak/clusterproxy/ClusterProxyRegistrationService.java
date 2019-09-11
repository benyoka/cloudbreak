package com.sequenceiq.cloudbreak.clusterproxy;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sequenceiq.cloudbreak.common.json.JsonUtil;
import com.sequenceiq.cloudbreak.service.secret.vault.VaultConfigException;
import com.sequenceiq.cloudbreak.service.secret.vault.VaultSecret;

@Component
public class ClusterProxyRegistrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterProxyRegistrationService.class);

    private RestTemplate restTemplate;

    @Value("${clusterProxy.url:}")
    private String clusterProxyUrl;

    @Value("${clusterProxy.registerConfigPath:}")
    private String registerConfigPath;

    @Value("${clusterProxy.updateConfigPath:}")
    private String updateConfigPath;

    @Value("${clusterProxy.removeConfigPath:}")
    private String removeConfigPath;

    @Autowired
    ClusterProxyRegistrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ConfigRegistrationResponse registerCluster(String clusterIdentifier, List<ClusterServiceConfig> serviceConfigs, List<String> certificates) {
        try {
            ConfigRegistrationRequest proxyConfigRequest = new ConfigRegistrationRequest(clusterIdentifier, serviceConfigs, certificates);
            LOGGER.debug("Cluster Proxy config request: {}", proxyConfigRequest);
            ResponseEntity<ConfigRegistrationResponse> response = restTemplate.postForEntity(clusterProxyUrl + registerConfigPath,
                    requestEntity(proxyConfigRequest), ConfigRegistrationResponse.class);

            LOGGER.debug("Cluster Proxy config response: {}", response);
            return response.getBody();
        } catch (Exception e) {
            String message = String.format("Error registering proxy configuration for cluster identifier '%s' with Cluster Proxy. URL: '%s'",
                    clusterIdentifier, clusterProxyUrl + registerConfigPath);
            LOGGER.error(message, e);
            throw new ClusterProxyException(message, e);

        }
    }

    public void registerGateway(String clusterIdentifier, String uriOfKnox) {
        try {

            ConfigUpdateRequest request = new ConfigUpdateRequest(clusterIdentifier, uriOfKnox);
            LOGGER.debug("Cluster Proxy config update request: {}", request);
            ResponseEntity<ConfigRegistrationResponse> response = restTemplate.postForEntity(clusterProxyUrl + updateConfigPath,
                    requestEntity(request), ConfigRegistrationResponse.class);
            LOGGER.debug("Cluster Proxy config update response: {}", response);
        } catch (Exception e) {
            String message = String.format("Error registering gateway configuration for cluster identifier '%s' with Cluster Proxy. URL: '%s'",
                    clusterIdentifier, clusterProxyUrl + updateConfigPath);
            LOGGER.error(message, e);
            throw new ClusterProxyException(message, e);
        }
    }

    public void deregisterCluster(String clusterIdentifier) {
        try {
            LOGGER.debug("Removing cluster proxy configuration for cluster identifier: {}", clusterIdentifier);
            restTemplate.postForEntity(clusterProxyUrl + removeConfigPath,
                    requestEntity(new ConfigDeleteRequest(clusterIdentifier)), ConfigRegistrationResponse.class);
            LOGGER.debug("Removed cluster proxy configuration for cluster identifier: {}", clusterIdentifier);
        } catch (Exception e) {
            String message = String.format("Error de-registering proxy configuration for cluster identifier '%s' from Cluster Proxy. URL: '%s'",
                    clusterIdentifier, clusterProxyUrl + removeConfigPath);
            LOGGER.error(message, e);
            throw new ClusterProxyException(message, e);
        }
    }

    private HttpEntity<String> requestEntity(ConfigRegistrationRequest proxyConfigRequest) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(JsonUtil.writeValueAsString(proxyConfigRequest), headers);
    }

    private HttpEntity<String> requestEntity(ConfigUpdateRequest proxyConfigRequest) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(JsonUtil.writeValueAsString(proxyConfigRequest), headers);
    }

    private HttpEntity<String> requestEntity(ConfigDeleteRequest proxyConfigRequest) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(JsonUtil.writeValueAsString(proxyConfigRequest), headers);
    }

    // TODO pull this out of this module? or to a helper method?
    private String vaultPath(String vaultSecretJsonString) {
        try {
            return JsonUtil.readValue(vaultSecretJsonString, VaultSecret.class).getPath() + ":secret";
        } catch (IOException e) {
            throw new VaultConfigException(String.format("Could not parse vault secret string '%s'", vaultSecretJsonString), e);
        }
    }
}
