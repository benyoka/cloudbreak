package com.sequenceiq.cloudbreak.cmtemplate.configproviders.kafka;

import com.cloudera.api.swagger.model.ApiClusterTemplateConfig;
import com.google.common.annotations.VisibleForTesting;
import com.sequenceiq.cloudbreak.api.endpoint.v4.common.StackType;
import com.sequenceiq.cloudbreak.cmtemplate.CmHostGroupRoleConfigProvider;
import com.sequenceiq.cloudbreak.cmtemplate.CmTemplateProcessor;
import com.sequenceiq.cloudbreak.cmtemplate.configproviders.ConfigUtils;
import com.sequenceiq.cloudbreak.cmtemplate.configproviders.kafka.KafkaConfigProviderUtils.CdhVersionForStreaming;
import com.sequenceiq.cloudbreak.template.TemplatePreparationObject;
import com.sequenceiq.cloudbreak.template.views.HostgroupView;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.sequenceiq.cloudbreak.cmtemplate.configproviders.ConfigUtils.config;
import static com.sequenceiq.cloudbreak.cmtemplate.configproviders.kafka.KafkaConfigProviderUtils.getCdhVersionForStreaming;

@Component
public class KafkaRangerPluginZookeeperSslConfigProvider implements CmHostGroupRoleConfigProvider {

    static final String KAFKA_ZOOKEEPER_SSL = "zookeeper.secure.connection.enable";

    static final String RANGER_KAFKA_SECURITY_XML_ROLE_SAFETY_VALVE = "ranger-kafka-security.xml_role_safety_valve";

    static final String RANGER_KAFKA_ZOOKEEPER = "xasecure.audit.destination.solr.zookeepers";

    @Override
    public List<ApiClusterTemplateConfig> getRoleConfigs(String roleType, HostgroupView hostGroupView, TemplatePreparationObject source) {
        CdhVersionForStreaming cdhVersion = getCdhVersionForStreaming(source);
        CmTemplateProcessor processor = (CmTemplateProcessor) source.getBlueprintView().getProcessor();
        if (StackType.WORKLOAD.equals(source.getStackType())
                && cdhVersion.needsZkSslWorkaround()
                && isKafkaZookeeperSSLEnabled(processor)
                && source.getSharedServiceConfigs().isPresent()
                && null != source.getSharedServiceConfigs().get().getDatalakeClusterManagerFqdn()) {
            String zkHost = source.getSharedServiceConfigs().get().getDatalakeClusterManagerFqdn();
            return List.of(
                    config(RANGER_KAFKA_SECURITY_XML_ROLE_SAFETY_VALVE,
                            ConfigUtils.getSafetyValveProperty(RANGER_KAFKA_ZOOKEEPER, zkHost + ":2082/solr")));
        } else {
            return List.of();
        }
    }

    @VisibleForTesting
    static boolean isKafkaZookeeperSSLEnabled(CmTemplateProcessor templateProcessor) {
        return templateProcessor.getTemplate().getServices().stream()
                .filter(s -> "KAFKA".equals(s.getServiceType()))
                .flatMap(s -> streamFromNullable(s.getServiceConfigs()).filter(conf -> KAFKA_ZOOKEEPER_SSL.equals(conf.getName())))
                .map(conf -> Boolean.parseBoolean(conf.getValue()))
                .findFirst()
                // default is enabled
                .orElse(true);
    }

    private static <T> Stream<T> streamFromNullable(Collection<T> input) {
        return input != null ? input.stream() : Stream.empty();
    }

    @Override
    public String getServiceType() {
        return KafkaRoles.KAFKA_SERVICE;
    }

    @Override
    public Set<String> getRoleTypes() {
        return Set.of(KafkaRoles.KAFKA_BROKER);
    }

}
