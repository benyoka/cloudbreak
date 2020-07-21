package com.sequenceiq.datalake.service.sdx;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.Status;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.StackV4Endpoint;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.StackStatusV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.StackV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.cluster.ClusterV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.cluster.clouderamanager.ClouderaManagerProductV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.cluster.clouderamanager.ClouderaManagerV4Response;
import com.sequenceiq.datalake.controller.exception.BadRequestException;
import com.sequenceiq.datalake.entity.SdxCluster;
import com.sequenceiq.datalake.repository.SdxClusterRepository;
import com.sequenceiq.sdx.api.model.SdxClusterShape;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SdxUpgradeServiceTest {

    private static final String USER_CRN = "crn:cdp:iam:us-west-1:1234:user:1";

    private static final String CLUSTER_NAME = "coolStack";

    private static final String OS_UPGRADE_CHECK_EXCEPTION_MESSAGE = "OS upgrade can't be done because the cluster is not in an available state!";

    @InjectMocks
    private SdxUpgradeService underTest;

    @Mock
    private SdxService sdxService;

    @Mock
    private StackV4Endpoint stackV4Endpoint;

    @Mock
    private SdxClusterRepository sdxClusterRepository;

    @Mock
    private StackStatusV4Response mockStackStatusV4Response;

    @Captor
    private ArgumentCaptor<SdxCluster> sdxClusterArgumentCaptor;

    private SdxCluster sdxCluster;

    @BeforeEach
    public void setUp() {
        sdxCluster = getValidSdxCluster();
    }

    @Test
    @DisplayName("Test if the runtime is properly updated")
    public void testUpdateRuntimeVersionFromCloudbreak() {
        when(sdxService.getById(1L)).thenReturn(sdxCluster);
        when(stackV4Endpoint.get(0L, "test-sdx-cluster", Set.of())).thenReturn(getStackV4Response());

        underTest.updateRuntimeVersionFromCloudbreak(1L);

        verify(sdxClusterRepository, times(1)).save(sdxClusterArgumentCaptor.capture());
        assertEquals("7.2.1", sdxClusterArgumentCaptor.getValue().getRuntime());
    }

    @Test
    @DisplayName("Test if the runtime cannot be updated when there is no CDH product installed")
    public void testUpdateRuntimeVersionFromCloudbreakWithoutCDH() {
        when(sdxService.getById(1L)).thenReturn(sdxCluster);
        StackV4Response stackV4Response = getStackV4Response();
        ClouderaManagerProductV4Response spark3 = new ClouderaManagerProductV4Response();
        spark3.setName("SPARK3");
        spark3.setVersion("3.0.0.2.99.7110.0-18-1.p0.3525631");
        stackV4Response.getCluster().getCm().setProducts(List.of(spark3));
        when(stackV4Endpoint.get(0L, "test-sdx-cluster", Set.of())).thenReturn(stackV4Response);

        underTest.updateRuntimeVersionFromCloudbreak(1L);

        verify(sdxClusterRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Test if the runtime cannot be updated when there is no CM installed")
    public void testUpdateRuntimeVersionFromCloudbreakWithoutCM() {
        when(sdxService.getById(1L)).thenReturn(sdxCluster);
        StackV4Response stackV4Response = getStackV4Response();
        stackV4Response.getCluster().setCm(null);
        when(stackV4Endpoint.get(0L, "test-sdx-cluster", Set.of())).thenReturn(stackV4Response);

        underTest.updateRuntimeVersionFromCloudbreak(1L);

        verify(sdxClusterRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Test if the runtime cannot be updated when there is no cluster")
    public void testUpdateRuntimeVersionFromCloudbreakWithoutCluster() {
        when(sdxService.getById(1L)).thenReturn(sdxCluster);
        StackV4Response stackV4Response = getStackV4Response();
        stackV4Response.setCluster(null);
        when(stackV4Endpoint.get(0L, "test-sdx-cluster", Set.of())).thenReturn(stackV4Response);

        underTest.updateRuntimeVersionFromCloudbreak(1L);

        verify(sdxClusterRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Test if the runtime cannot be updated when there is no CDP version specified")
    public void testUpdateRuntimeVersionFromCloudbreakWithoutCDHVersion() {
        when(sdxService.getById(1L)).thenReturn(sdxCluster);
        StackV4Response stackV4Response = getStackV4Response();
        ClouderaManagerProductV4Response cdp = new ClouderaManagerProductV4Response();
        cdp.setName("CDH");
        stackV4Response.getCluster().getCm().setProducts(List.of(cdp));
        when(stackV4Endpoint.get(0L, "test-sdx-cluster", Set.of())).thenReturn(stackV4Response);

        underTest.updateRuntimeVersionFromCloudbreak(1L);

        verify(sdxClusterRepository, times(0)).save(any());
    }

    @Test
    void testTriggerOsUpgradeByNameCalledWhenStackIsNotAvailableThenBadRequestExceptionComes() {
        when(sdxService.getSdxByNameInAccount(USER_CRN, sdxCluster.getClusterName())).thenReturn(sdxCluster);

        when(stackV4Endpoint.getStatusByName(0L, sdxCluster.getClusterName())).thenReturn(mockStackStatusV4Response);
        when(mockStackStatusV4Response.getClusterStatus()).thenReturn(Status.STOPPED);

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                () -> underTest.triggerOsUpgradeByName(USER_CRN, sdxCluster.getClusterName()));

        Assertions.assertEquals(exception.getMessage(), OS_UPGRADE_CHECK_EXCEPTION_MESSAGE);
    }

    @Test
    void testTriggerOsUpgradeByCrnCalledWhenStackIsNotAvailableThenBadRequestExceptionComes() {
        when(sdxService.getByCrn(USER_CRN, sdxCluster.getCrn())).thenReturn(sdxCluster);

        when(stackV4Endpoint.getStatusByName(0L, sdxCluster.getClusterName())).thenReturn(mockStackStatusV4Response);
        when(mockStackStatusV4Response.getClusterStatus()).thenReturn(Status.STOPPED);

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                () -> underTest.triggerOsUpgradeByCrn(USER_CRN, sdxCluster.getCrn()));

        Assertions.assertEquals(exception.getMessage(), OS_UPGRADE_CHECK_EXCEPTION_MESSAGE);
    }

    private StackV4Response getStackV4Response() {
        ClouderaManagerProductV4Response cdp = new ClouderaManagerProductV4Response();
        cdp.setName("CDH");
        cdp.setVersion("7.2.1-1.cdh7.2.0.p0.3758356");

        ClouderaManagerProductV4Response cfm = new ClouderaManagerProductV4Response();
        cfm.setName("CFM");
        cfm.setVersion("2.0.0.0");

        ClouderaManagerProductV4Response spark3 = new ClouderaManagerProductV4Response();
        spark3.setName("SPARK3");
        spark3.setVersion("3.0.0.2.99.7110.0-18-1.p0.3525631");

        ClouderaManagerV4Response cm = new ClouderaManagerV4Response();
        cm.setProducts(List.of(cdp, cfm, spark3));

        ClusterV4Response clusterV4Response = new ClusterV4Response();
        clusterV4Response.setCm(cm);

        StackV4Response stackV4Response = new StackV4Response();
        stackV4Response.setName("test-sdx-cluster");
        stackV4Response.setCluster(clusterV4Response);

        return stackV4Response;
    }

    private SdxCluster getValidSdxCluster() {
        SdxCluster sdxCluster = new SdxCluster();
        sdxCluster.setClusterName("test-sdx-cluster");
        sdxCluster.setClusterShape(SdxClusterShape.LIGHT_DUTY);
        sdxCluster.setEnvName("test-env");
        sdxCluster.setCrn("crn:sdxcluster");
        sdxCluster.setRuntime("7.2.0");
        sdxCluster.setId(1L);
        return sdxCluster;
    }
}
