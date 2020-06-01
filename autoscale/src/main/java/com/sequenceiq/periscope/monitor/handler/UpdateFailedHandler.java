package com.sequenceiq.periscope.monitor.handler;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.logger.MDCBuilder;
import com.sequenceiq.periscope.api.model.ClusterState;
import com.sequenceiq.periscope.domain.Cluster;
import com.sequenceiq.periscope.monitor.event.UpdateFailedEvent;
import com.sequenceiq.periscope.service.ClusterService;

@Component
public class UpdateFailedHandler implements ApplicationListener<UpdateFailedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateFailedHandler.class);

    private static final int RETRY_THRESHOLD = 5;

    @Inject
    private ClusterService clusterService;

    private final Map<Long, Integer> updateFailures = new ConcurrentHashMap<>();

    @Override
    public void onApplicationEvent(UpdateFailedEvent event) {
        long autoscaleClusterId = event.getClusterId();
        Cluster cluster = clusterService.findById(autoscaleClusterId);
        if (cluster == null) {
            return;
        }
        MDCBuilder.buildMdcContext(cluster);
        Integer failed = Optional.ofNullable(updateFailures.get(autoscaleClusterId))
                .map(failedCount -> failedCount + 1)
                .orElse(1);
        if (failed < RETRY_THRESHOLD) {
            updateFailures.put(autoscaleClusterId, failed);
            LOGGER.debug("Increased failed count '{}' for cluster '{}'", failed, cluster.getStackCrn());
        } else {
            suspendCluster(cluster);
            updateFailures.remove(autoscaleClusterId);
            LOGGER.debug("Suspended cluster monitoring for cluster '{}' due to failing update attempts", cluster.getStackCrn());
        }
    }

    private void suspendCluster(Cluster cluster) {
        if (!cluster.getState().equals(ClusterState.SUSPENDED)) {
            clusterService.setState(cluster.getId(), ClusterState.SUSPENDED);
        }
    }
}