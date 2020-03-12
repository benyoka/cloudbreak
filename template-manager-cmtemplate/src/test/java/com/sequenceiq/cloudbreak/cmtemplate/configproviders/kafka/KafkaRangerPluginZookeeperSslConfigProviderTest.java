package com.sequenceiq.cloudbreak.cmtemplate.configproviders.kafka;

import com.cloudera.api.swagger.model.ApiClusterTemplate;
import com.cloudera.api.swagger.model.ApiClusterTemplateConfig;
import com.cloudera.api.swagger.model.ApiClusterTemplateService;
import com.sequenceiq.cloudbreak.api.endpoint.v4.common.StackType;
import com.sequenceiq.cloudbreak.cmtemplate.CmTemplateProcessor;
import com.sequenceiq.cloudbreak.cmtemplate.configproviders.ConfigUtils;
import com.sequenceiq.cloudbreak.template.TemplatePreparationObject;
import com.sequenceiq.cloudbreak.template.TemplatePreparationObject.Builder;
import com.sequenceiq.cloudbreak.template.views.BlueprintView;
import com.sequenceiq.cloudbreak.template.views.SharedServiceConfigsView;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

import static com.sequenceiq.cloudbreak.api.endpoint.v4.common.StackType.WORKLOAD;
import static com.sequenceiq.cloudbreak.cmtemplate.configproviders.ConfigUtils.config;
import static com.sequenceiq.cloudbreak.cmtemplate.configproviders.kafka.KafkaRangerPluginZookeeperSslConfigProvider.RANGER_KAFKA_SECURITY_XML_ROLE_SAFETY_VALVE;
import static com.sequenceiq.cloudbreak.cmtemplate.configproviders.kafka.KafkaRangerPluginZookeeperSslConfigProvider.RANGER_KAFKA_ZOOKEEPER;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KafkaRangerPluginZookeeperSslConfigProviderTest {

    private static final String ZK_HOST = "master0.mydatalake.cloudera.site";

    private static final List<ApiClusterTemplateConfig> EXPECTED_SSL_WORKAROUND = List.of(
            config(RANGER_KAFKA_SECURITY_XML_ROLE_SAFETY_VALVE,
                    ConfigUtils.getSafetyValveProperty(RANGER_KAFKA_ZOOKEEPER, ZK_HOST + ":2082/solr")));

    private static final List<ApiClusterTemplateConfig> EMPTY_CONFIG = List.of();

    @ParameterizedTest
    @MethodSource("testArgsForGetRoleConfigs")
    void getRoleConfigs(TemplatePreparationObject source,
                        List<ApiClusterTemplateConfig> expected) {
        List<ApiClusterTemplateConfig> result = new KafkaRangerPluginZookeeperSslConfigProvider().getRoleConfigs(null, null, source);
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("testArgsForIsKafkaZookeeperSSLEnabled")
    void isKafkaZookeeperSSLEnabled(Map<String, Map<String, String>> services, Boolean expected) {
        CmTemplateProcessor processor = getTemplateProcessor(services, null);
        assertThat(KafkaRangerPluginZookeeperSslConfigProvider.isKafkaZookeeperSSLEnabled(processor))
                .as("services: %s, cdh version: %s", services)
                .isEqualTo(expected);
    }

    static Stream<Arguments> testArgsForGetRoleConfigs() {
        return Stream.of(
                Arguments.of(getTemplatePreparationObject(WORKLOAD, "7.1.0", true, true, true), EXPECTED_SSL_WORKAROUND),
                // CDH doesn't support SSL
                Arguments.of(getTemplatePreparationObject(WORKLOAD, "7.0.2", true, true, true), EMPTY_CONFIG),
                // SSL Workaround not needed as of CDH 7.1.1
                Arguments.of(getTemplatePreparationObject(WORKLOAD, "7.1.1", true, true, true), EMPTY_CONFIG),
                // Zookeeper SSL disabled
                Arguments.of(getTemplatePreparationObject(WORKLOAD, "7.1.1", false, true, true), EMPTY_CONFIG),
                // Shared services config not provided
                Arguments.of(getTemplatePreparationObject(WORKLOAD, "7.1.1", true, false, false), EMPTY_CONFIG),
                // Datalake master nor provided
                Arguments.of(getTemplatePreparationObject(WORKLOAD, "7.1.1", false, true, true), EMPTY_CONFIG));
    }

    static Stream<Arguments> testArgsForIsKafkaZookeeperSSLEnabled() {
        return Stream.of(
                Arguments.of(Map.of("KAFKA", Map.of()), true),
                Arguments.of(Map.of("KAFKA", Map.of("zookeeper.secure.connection.enable", "false")), false),
                Arguments.of(Map.of("KAFKA", Map.of("zookeeper.secure.connection.enable", "true")), true)
        );
    }

    private static TemplatePreparationObject getTemplatePreparationObject(StackType stackType,
            String cdhVersion,
            boolean zookeeperSslEnabled,
            boolean sharedServicesConfigPresent,
            boolean dataLakeMasterPresent) {
        Map<String, Map<String, String>> serviceConfigs = zookeeperSslEnabled ?
                Map.of("KAFKA", Map.of()) :
                Map.of("KAFKA", Map.of("zookeeper.secure.connection.enable", "false"));
        BlueprintView bp = new BlueprintView(null, null, null, getTemplateProcessor(serviceConfigs, cdhVersion));

        Builder tpo = Builder.builder().withStackType(stackType).withBlueprintView(bp);
        if (sharedServicesConfigPresent) {
            SharedServiceConfigsView sharedServiceConfigsView = new SharedServiceConfigsView();
            if (dataLakeMasterPresent) {
                sharedServiceConfigsView.setDatalakeClusterManagerFqdn(ZK_HOST);
            }
            tpo.withSharedServiceConfigs(sharedServiceConfigsView);
        }
        return tpo.build();
    }

    private static CmTemplateProcessor getTemplateProcessor(Map<String, Map<String, String>> services, String cdhVersion) {
        ApiClusterTemplate template = new ApiClusterTemplate();
        template.setServices(services.entrySet().stream().map(KafkaRangerPluginZookeeperSslConfigProviderTest::toService).collect(toList()));
        CmTemplateProcessor processor = mock(CmTemplateProcessor.class);
        when(processor.getTemplate()).thenReturn(template);
        when(processor.getVersion()).thenReturn(Optional.ofNullable(cdhVersion));
        return processor;
    }

    private static ApiClusterTemplateService toService(Entry<String, Map<String, String>> entry) {
        ApiClusterTemplateService service = new ApiClusterTemplateService();
        service.setServiceType(entry.getKey());
        service.setServiceConfigs(entry.getValue().entrySet().stream().map(KafkaRangerPluginZookeeperSslConfigProviderTest::toConfig).collect(toList()));
        return service;
    }

    private static ApiClusterTemplateConfig toConfig(Entry<String, String> entry) {
        ApiClusterTemplateConfig config = new ApiClusterTemplateConfig();
        config.setName(entry.getKey());
        config.setValue(entry.getValue());
        return config;
    }
}