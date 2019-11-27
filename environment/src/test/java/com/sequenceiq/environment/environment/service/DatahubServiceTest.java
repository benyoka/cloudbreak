package com.sequenceiq.environment.environment.service;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.Status;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.StackV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.StackViewV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.StackViewV4Responses;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.cluster.ClusterV4Response;
import com.sequenceiq.cloudbreak.common.exception.WebApplicationExceptionMessageExtractor;
import com.sequenceiq.distrox.api.v1.distrox.endpoint.DistroXV1Endpoint;
import com.sequenceiq.environment.environment.poller.ClusterPollerResultEvaluator;
import com.sequenceiq.environment.environment.poller.DatahubPollerProvider;

class DatahubServiceTest {

    private static final String ENV_CRN = "envCrn";

    private static final String STACK_CRN = "stackCrn";

    private DistroXV1Endpoint distroXV1Endpoint = Mockito.mock(DistroXV1Endpoint.class);

    private DatahubPollerProvider datahubPollerProvider = new DatahubPollerProvider(distroXV1Endpoint, new ClusterPollerResultEvaluator());

    private DatahubService underTest = new DatahubService(distroXV1Endpoint, datahubPollerProvider, new WebApplicationExceptionMessageExtractor());

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(underTest, "attemptCount", 5);
        ReflectionTestUtils.setField(underTest, "sleepTime", 1);
    }

    @Test
    void testStopAttachedDatahubWhenNoAttachedDatahub() {
        when(distroXV1Endpoint.list(null, ENV_CRN)).thenReturn(new StackViewV4Responses(Collections.emptySet()));

        underTest.stopAttachedDatahubClusters(1L, ENV_CRN);

        verify(distroXV1Endpoint, times(0)).putStopByCrns(anyList());
    }

    @Test
    void testStopAttachedDatahubWhenDatahubIsAvailable() {
        StackViewV4Response stackView = getStackView(Status.AVAILABLE);
        when(distroXV1Endpoint.list(null, ENV_CRN)).thenReturn(new StackViewV4Responses(Set.of(stackView)));
        when(distroXV1Endpoint.getByCrn(anyString(), anySet()))
                .thenReturn(getStack(Status.AVAILABLE), getStack(Status.AVAILABLE), getStack(Status.STOPPED));

        underTest.stopAttachedDatahubClusters(1L, ENV_CRN);

        verify(distroXV1Endpoint, times(1)).putStopByCrns(anyList());
    }

    @Test
    void testStartAttachedDatahubWhenNoAttachedDatahub() {
        when(distroXV1Endpoint.list(null, ENV_CRN)).thenReturn(new StackViewV4Responses(Collections.emptySet()));

        underTest.startAttachedDatahubClusters(1L, ENV_CRN);

        verify(distroXV1Endpoint, times(0)).putStartByCrns(anyList());
    }

    @Test
    void testStartAttachedDatahubWhenDatahubIsStopped() {
        StackViewV4Response stackView = getStackView(Status.STOPPED);
        when(distroXV1Endpoint.list(null, ENV_CRN)).thenReturn(new StackViewV4Responses(Set.of(stackView)));
        when(distroXV1Endpoint.getByCrn(anyString(), anySet()))
                .thenReturn(getStack(Status.STOPPED), getStack(Status.STOPPED), getStack(Status.AVAILABLE));

        underTest.startAttachedDatahubClusters(1L, ENV_CRN);

        verify(distroXV1Endpoint, times(1)).putStartByCrns(anyList());
    }

    private StackV4Response getStack(Status status) {
        StackV4Response stack = new StackV4Response();
        stack.setStatus(status);
        stack.setCrn(STACK_CRN);
        ClusterV4Response cluster = new ClusterV4Response();
        cluster.setStatus(status);
        stack.setCluster(cluster);
        return stack;
    }

    private StackViewV4Response getStackView(Status status) {
        StackViewV4Response stack = new StackViewV4Response();
        stack.setCrn(STACK_CRN);
        stack.setStatus(status);
        return stack;
    }
}
