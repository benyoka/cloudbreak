package com.sequenceiq.it.cloudbreak.mock.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.cloudera.api.swagger.model.ApiAuthRoleMetadataList;
import com.cloudera.api.swagger.model.ApiAuthRoleRef;
import com.cloudera.api.swagger.model.ApiCommand;
import com.cloudera.api.swagger.model.ApiCommandList;
import com.cloudera.api.swagger.model.ApiConfigList;
import com.cloudera.api.swagger.model.ApiEcho;
import com.cloudera.api.swagger.model.ApiHost;
import com.cloudera.api.swagger.model.ApiHostList;
import com.cloudera.api.swagger.model.ApiRemoteDataContext;
import com.cloudera.api.swagger.model.ApiRoleTypeList;
import com.cloudera.api.swagger.model.ApiService;
import com.cloudera.api.swagger.model.ApiServiceState;
import com.cloudera.api.swagger.model.ApiUser2;
import com.cloudera.api.swagger.model.ApiUser2List;
import com.sequenceiq.cloudbreak.cloud.model.CloudVmMetaDataStatus;
import com.sequenceiq.it.cloudbreak.mock.AbstractModelMock;
import com.sequenceiq.it.cloudbreak.mock.DefaultModel;
import com.sequenceiq.it.cloudbreak.spark.DynamicRouteStack;
import com.sequenceiq.it.cloudbreak.spark.StatefulRoute;
import com.sequenceiq.it.util.HostNameUtil;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Service;

public class ClouderaManagerMock extends AbstractModelMock {

    public static final String PROFILE_RETURN_HTTP_500 = "cmHttp500";

    public static final String API_ROOT = "/api/v31";

    public static final String ECHO = API_ROOT + "/tools/echo";

    public static final String USERS = API_ROOT + "/users";

    public static final String USERS_USER = API_ROOT + "/users/:user";

    public static final String IMPORT_CLUSTERTEMPLATE = API_ROOT + "/cm/importClusterTemplate";

    public static final String COMMANDS_COMMAND = API_ROOT + "/commands/:commandId";

    public static final String COMMANDS_STOP = API_ROOT + "/clusters/:cluster/commands/stop";

    public static final String COMMANDS_START = API_ROOT + "/clusters/:cluster/commands/start";

    public static final String HOSTS = API_ROOT + "/hosts";

    public static final String BEGIN_FREE_TRIAL = API_ROOT + "/cm/trial/begin";

    public static final String MANAGEMENT_SERVICE = API_ROOT + "/cm/service";

    public static final String START_MANAGEMENT_SERVICE = API_ROOT + "/cm/service/commands/start";

    public static final String ACTIVE_COMMANDS = API_ROOT + "/cm/service/commands";

    public static final String ROLE_TYPES = API_ROOT + "/cm/service/roleTypes";

    public static final String ROLES = API_ROOT + "/cm/service/roles";

    public static final String CONFIG = API_ROOT + "/cm/config";

    public static final String LIST_COMMANDS = API_ROOT + "/cm/commands";

    public static final String READ_AUTH_ROLES = API_ROOT + "/authRoles/metadata";

    public static final String CDP_REMOTE_CONTEXT_BY_CLUSTER_CLUSTER_NAME = "/api/cdp/remoteContext/byCluster/:clusterName";

    public static final String CDP_REMOTE_CONTEXT = "/api/cdp/remoteContext";

    private DynamicRouteStack dynamicRouteStack;

    private final List<String> activeProfiles;

    public ClouderaManagerMock(Service sparkService, DefaultModel defaultModel, List<String> activeProfiles) {
        super(sparkService, defaultModel);
        dynamicRouteStack = new DynamicRouteStack(sparkService, defaultModel);
        this.activeProfiles = activeProfiles;
    }

    public DynamicRouteStack getDynamicRouteStack() {
        return dynamicRouteStack;
    }

    public void addClouderaManagerMappings() {
        getEcho();
        getUsers();
        putUser();
        postUser();
        postImportClusterTemplate();
        getCommand();
        postStopCommand();
        getHosts();
        postStartCommand();
        postBeginTrial();
        addManagementService();
        getManagementService();
        createRoles();
        listRoleTypes();
        listRoles();
        cmConfig();
        updateCmConfig();
        startManagementService();
        listCommands();
        listActiveCommands();
        readAuthRoles();
        getCdpRemoteContext();
        postCdpRemoteContext();
    }

    public static class ErroneousStatefulRoute implements StatefulRoute {

        private final Route happyResponseHandler;

        private final StatefulRoute statefulHappyResponseHandler;

        private final List<String> activeProfiles;

        private int callCounter;

        public ErroneousStatefulRoute(Route happyResponseHandler, List<String> activeProfiles) {
            this.happyResponseHandler = happyResponseHandler;
            statefulHappyResponseHandler = null;
            this.activeProfiles = activeProfiles;
        }

        public ErroneousStatefulRoute(StatefulRoute statefulHappyResponseHandler, List<String> activeProfiles) {
            happyResponseHandler = null;
            this.statefulHappyResponseHandler = statefulHappyResponseHandler;
            this.activeProfiles = activeProfiles;
        }

        @Override
        public Object handle(Request request, Response response, DefaultModel model) throws Exception {
            if (activeProfiles.contains(PROFILE_RETURN_HTTP_500) && callCounter == 0) {
                response.body("Mocked HTTP 500.");
                response.status(500);
                callCounter++;
                return response;
            } else {
                callCounter++;
                return happyResponseHandler == null ? statefulHappyResponseHandler.handle(request, response, model)
                        : happyResponseHandler.handle(request, response);
            }
        }
    }

    private void readAuthRoles() {
        dynamicRouteStack.get(READ_AUTH_ROLES,
                new ErroneousStatefulRoute((request, response) -> new ApiAuthRoleMetadataList(), activeProfiles));
    }

    private void getEcho() {
        dynamicRouteStack.get(ECHO, new ErroneousStatefulRoute((request, response) -> {
            String message = request.queryMap("message").value();
            message = message == null ? "Hello World!" : message;
            return new ApiEcho().message(message);
        }, activeProfiles));
    }

    private void getUsers() {
        dynamicRouteStack.get(USERS, new ErroneousStatefulRoute((request, response) -> getUserList(), activeProfiles));
    }

    private void putUser() {
        dynamicRouteStack.put(USERS_USER, new ErroneousStatefulRoute((request, response)
                -> new ApiUser2().name(request.params("user")), activeProfiles));
    }

    private void postUser() {
        dynamicRouteStack.post(USERS, new ErroneousStatefulRoute((request, response) -> getUserList(), activeProfiles));
    }

    private ApiUser2List getUserList() {
        ApiUser2List apiUser2List = new ApiUser2List();
        ApiAuthRoleRef authRoleRef = new ApiAuthRoleRef().displayName("Full Administrator").uuid(UUID.randomUUID().toString());
        apiUser2List.addItemsItem(new ApiUser2().name("admin").addAuthRolesItem(authRoleRef));
        apiUser2List.addItemsItem(new ApiUser2().name("cloudbreak").addAuthRolesItem(authRoleRef));
        return apiUser2List;
    }

    private void postImportClusterTemplate() {
        dynamicRouteStack.post(IMPORT_CLUSTERTEMPLATE, new ErroneousStatefulRoute((request, response)
                        -> new ApiCommand().id(BigDecimal.ONE).name("Import ClusterTemplate").active(Boolean.TRUE), activeProfiles));
    }

    private void getCommand() {
        dynamicRouteStack.get(COMMANDS_COMMAND, new ErroneousStatefulRoute((request, response)
                -> new ApiCommand().id(new BigDecimal(request.params("commandId"))).active(Boolean.FALSE).success(Boolean.TRUE), activeProfiles));
    }

    private void postStopCommand() {
        dynamicRouteStack.post(COMMANDS_STOP, new ErroneousStatefulRoute((request, response)
                -> new ApiCommand().id(BigDecimal.ONE).active(Boolean.TRUE).name("Stop"), activeProfiles));
    }

    private void postStartCommand() {
        dynamicRouteStack.post(COMMANDS_START, new ErroneousStatefulRoute((request, response)
                -> new ApiCommand().id(BigDecimal.ONE).active(Boolean.TRUE).name("Start"), activeProfiles));
    }

    private void postBeginTrial() {
        dynamicRouteStack.post(BEGIN_FREE_TRIAL, new ErroneousStatefulRoute((request, response) -> null, activeProfiles));
    }

    private void addManagementService() {
        dynamicRouteStack.put(MANAGEMENT_SERVICE, new ErroneousStatefulRoute((request, response) -> new ApiService(), activeProfiles));
    }

    private void getManagementService() {
        dynamicRouteStack.get(MANAGEMENT_SERVICE, new ErroneousStatefulRoute((request, response)
                -> new ApiService().serviceState(ApiServiceState.STARTED), activeProfiles));
    }

    private void startManagementService() {
        dynamicRouteStack.post(START_MANAGEMENT_SERVICE, new ErroneousStatefulRoute((request, response) -> new ApiService(), activeProfiles));
    }

    private void listRoleTypes() {
        dynamicRouteStack.get(ROLE_TYPES, new ErroneousStatefulRoute((request, response)
                -> new ApiRoleTypeList().items(new ArrayList<>()), activeProfiles));
    }

    private void listRoles() {
        dynamicRouteStack.get(ROLES, new ErroneousStatefulRoute((request, response)
                -> new ApiRoleTypeList().items(new ArrayList<>()), activeProfiles));
    }

    private void createRoles() {
        dynamicRouteStack.post(ROLES, new ErroneousStatefulRoute((request, response)
                -> new ApiRoleTypeList().items(new ArrayList<>()), activeProfiles));
    }

    private void listActiveCommands() {
        dynamicRouteStack.get(ACTIVE_COMMANDS, new ErroneousStatefulRoute((request, response) -> new ApiCommandList().items(
                        List.of(new ApiCommand().id(new BigDecimal(1)).active(Boolean.FALSE).success(Boolean.TRUE))), activeProfiles));
    }

    private void listCommands() {
        dynamicRouteStack.get(LIST_COMMANDS, new ErroneousStatefulRoute((request, response) -> new ApiCommandList().items(
                        List.of(new ApiCommand().id(new BigDecimal(1)).active(Boolean.FALSE).success(Boolean.TRUE))), activeProfiles));
    }

    private void cmConfig() {
        dynamicRouteStack.get(CONFIG, new ErroneousStatefulRoute((request, response)
                -> new ApiConfigList().items(new ArrayList<>()), activeProfiles));
    }

    private void updateCmConfig() {
        dynamicRouteStack.put(CONFIG, new ErroneousStatefulRoute((request, response)
                -> new ApiConfigList().items(new ArrayList<>()), activeProfiles));
    }

    private void getCdpRemoteContext() {
        dynamicRouteStack.get(CDP_REMOTE_CONTEXT_BY_CLUSTER_CLUSTER_NAME,
                new ErroneousStatefulRoute((req, res) -> new ApiRemoteDataContext(), activeProfiles));
    }

    private void postCdpRemoteContext() {
        dynamicRouteStack.post(CDP_REMOTE_CONTEXT,
                new ErroneousStatefulRoute((req, res) -> new ApiRemoteDataContext(), activeProfiles));
    }

    private void getHosts() {
        dynamicRouteStack.get(HOSTS, new ErroneousStatefulRoute((request, response, model) -> {
            Map<String, CloudVmMetaDataStatus> instanceMap = model.getInstanceMap();
            ApiHostList apiHostList = new ApiHostList();
            for (Map.Entry<String, CloudVmMetaDataStatus> entry : instanceMap.entrySet()) {
                ApiHost apiHost = new ApiHost()
                        .hostId(entry.getValue().getCloudVmInstanceStatus().getCloudInstance().getInstanceId())
                        .hostname(HostNameUtil.generateHostNameByIp(entry.getValue().getMetaData().getPrivateIp()))
                        .ipAddress(entry.getValue().getMetaData().getPrivateIp());
                apiHostList.addItemsItem(apiHost);
            }
            return apiHostList;
        }, activeProfiles));
    }

}
