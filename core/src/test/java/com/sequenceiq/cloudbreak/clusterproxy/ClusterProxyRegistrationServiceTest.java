package com.sequenceiq.cloudbreak.clusterproxy;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sequenceiq.cloudbreak.common.json.JsonUtil;

@RunWith(MockitoJUnitRunner.class)
public class ClusterProxyRegistrationServiceTest {
    private static final String CLUSTER_IDENTIFIER = "cluster-identifier";

    private static final String KNOX_URI = "https://10.10.10.10:8443/test-cluster";

    private static final String CLUSTER_PROXY_URL = "http://localhost:10080/cluster-proxy";

    private static final String REGISTER_CONFIG_PATH = "/rpc/registerConfig";

    private static final String UPDATE_CONFIG_PATH = "/rpc/updateConfig";

    private static final String REMOVE_CONFIG_PATH = "/rpc/removeConfig";

    private MockRestServiceServer mockServer;

    private ClusterProxyRegistrationService service;

    @Before
    public void setup() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        service = new ClusterProxyRegistrationService(restTemplate);
        ReflectionTestUtils.setField(service, "clusterProxyUrl", CLUSTER_PROXY_URL);
        ReflectionTestUtils.setField(service, "registerConfigPath", REGISTER_CONFIG_PATH);
        ReflectionTestUtils.setField(service, "updateConfigPath", UPDATE_CONFIG_PATH);
        ReflectionTestUtils.setField(service, "removeConfigPath", REMOVE_CONFIG_PATH);
    }

    @Test
    public void shouldRegisterProxyConfigurationWithClusterProxy() throws URISyntaxException, JsonProcessingException {
        ClusterServiceConfig clusterServiceConfig = clusterServiceConfig();
        ConfigRegistrationResponse response = new ConfigRegistrationResponse();
        response.setX509Unwrapped("X509PublicKey");
        mockServer.expect(once(), MockRestRequestMatchers.requestTo(new URI(CLUSTER_PROXY_URL + REGISTER_CONFIG_PATH)))
                .andExpect(content().json(configRegistrationRequest(CLUSTER_IDENTIFIER, clusterServiceConfig)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(JsonUtil.writeValueAsStringSilent(response)));

        ConfigRegistrationResponse registrationResponse = service.registerCluster(CLUSTER_IDENTIFIER, asList(clusterServiceConfig));
        assertEquals("X509PublicKey", registrationResponse.getX509Unwrapped());
    }

    @Test
    public void shouldUpdateKnoxUrlWithClusterProxy() throws URISyntaxException, JsonProcessingException {
        mockServer.expect(once(), MockRestRequestMatchers.requestTo(new URI(CLUSTER_PROXY_URL + UPDATE_CONFIG_PATH)))
                .andExpect(content().json(configUpdateRequest(CLUSTER_IDENTIFIER, KNOX_URI)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK));

        service.registerGateway(CLUSTER_IDENTIFIER, KNOX_URI);
    }

    @Test
    public void shouldDeregisterCluster() throws URISyntaxException, JsonProcessingException {
        mockServer.expect(once(), MockRestRequestMatchers.requestTo(new URI(CLUSTER_PROXY_URL + REMOVE_CONFIG_PATH)))
                .andExpect(content().json(JsonUtil.writeValueAsStringSilent(of("clusterCrn", CLUSTER_IDENTIFIER))))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK));

        service.deregisterCluster(CLUSTER_IDENTIFIER);
    }

    private ClusterServiceConfig clusterServiceConfig() {
        ClusterServiceCredential cloudbreakUser = new ClusterServiceCredential("cloudbreak", "/cb/test-data/secret/cbpassword:secret");
        ClusterServiceCredential dpUser = new ClusterServiceCredential("cmmgmt", "/cb/test-data/secret/dppassword:secret", true);
        return new ClusterServiceConfig("cloudera-manager",
                List.of("https://10.10.10.10/clouderamanager"), asList(cloudbreakUser, dpUser));
    }

    private String configRegistrationRequest(String clusterIdentifier, ClusterServiceConfig serviceConfig) {
        return JsonUtil.writeValueAsStringSilent(new ConfigRegistrationRequest(clusterIdentifier, List.of(serviceConfig)));
    }

    private String configUpdateRequest(String clusterIdentifier, String knoxUri) {
        return JsonUtil.writeValueAsStringSilent(of("clusterCrn", clusterIdentifier,
                "uriOfKnox", knoxUri));
    }

    @After
    public void teardown() {
        mockServer.verify();
    }
}