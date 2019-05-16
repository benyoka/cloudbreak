package com.sequenceiq.environment.api.environment.doc;

public class EnvironmentModelDescription {

    public static final String LDAP_CONFIG_NAME = "LDAP config name for the cluster";
    public static final String RDSCONFIG_NAMES = "RDS configuration names for the cluster";
    public static final String KERBEROSCONFIG_NAME = "Kerberos config name for the cluster";
    public static final String AMBARI_URL = "Ambari url";
    public static final String SERVICE_DESCRIPTORS = "Descriptors of the datalake services";
    public static final String SERVICE_NAME = "Name of the datalake service";
    public static final String BLUEPRINT_PARAMS = "Bluepirnt parameters from the datalake services";
    public static final String COMPONENT_HOSTS = "Component hosts of the datalake services";

    public static final String LDAP_REQUEST = "LDAP config request";
    public static final String LDAP_RESPONSE = "LDAP config response";
    public static final String RDS_RESPONSE = "RDS config response";
    public static final String KERBEROS_RESPONSE = "Kerberos config response";

    public static final String SUBNET_IDS = "Subnet ids of the specified networks";
    public static final String AWS_SPECIFIC_PARAMETERS = "Subnet ids of the specified networks";
    public static final String AZURE_SPECIFIC_PARAMETERS = "Subnet ids of the specified networks";
    public static final String AWS_VPC_ID = "Subnet ids of the specified networks";
    public static final String AZURE_RESOURCE_GROUP_NAME = "Subnet ids of the specified networks";
    public static final String AZURE_NETWORK_ID = "Subnet ids of the specified networks";
    public static final String AZURE_NO_PUBLIC_IP = "Subnet ids of the specified networks";
    public static final String AZURE_NO_FIREWALL_RULES = "Subnet ids of the specified networks";

    public static final String CREDENTIAL_NAME_REQUEST = "Name of the credential of the environment. If the name is given, "
            + "the detailed credential is ignored in the request.";
    public static final String CREDENTIAL_REQUEST = "If credentialName is not specified, the credential is used to create the new credential for "
            + "the environment.";
    public static final String INTERACTIVE_LOGIN_CREDENTIAL_VERIFICATION_URL = "The url provided by Azure where the user have to use the given user code "
            + "to sign in";
    public static final String INTERACTIVE_LOGIN_CREDENTIAL_USER_CODE = "The user code what has to be used for the sign-in process on the Azure portal";
    public static final String PROXY_CONFIGS_REQUEST = "Name of the proxy configurations to be attached to the environment.";
    public static final String RDS_CONFIGS_REQUEST = "Name of the RDS configurations to be attached to the environment.";
    public static final String KUBERNETES_CONFIGS_REQUEST = "Name of the Kubernetes configurations to be attached to the environment.";
    public static final String LDAP_CONFIGS_REQUEST = "Name of the LDAP configurations to be attached to the environment.";
    public static final String REGIONS = "Regions of the environment.";
    public static final String REGION_DISPLAYNAMES = "regions with displayNames";
    public static final String LOCATION = "Location of the environment.";
    public static final String LONGITUDE = "Location longitude of the environment.";
    public static final String LATITUDE = "Location latitude of the environment.";
    public static final String KERBEROS_CONFIGS_REQUEST = "Name of Kerberos configs to be attached to the environment.";
    public static final String LOCATION_DISPLAY_NAME = "Display name of the location of the environment.";
    public static final String NETWORK = "Network related specifics of the environment.";

    public static final String CREDENTIAL_NAME_RESPONSE = "Name of the credential of the environment.";
    public static final String CREDENTIAL_RESPONSE = "Credential of the environment.";
    public static final String PROXY_CONFIGS_RESPONSE = "Proxy configurations in the environment.";
    public static final String RDS_CONFIGS_RESPONSE = "RDS configurations in the environment.";
    public static final String KUBERNETES_CONFIGS_RESPONSE = "Kubernetes configurations in the environment.";
    public static final String LDAP_CONFIGS_RESPONSE = "LDAP configurations in the environment.";
    public static final String CLOUD_PLATFORM = "Cloud platform of the environment.";
    public static final String WORKLOAD_CLUSTERS = "Workload clusters created in the environment.";
    public static final String WORKLOAD_CLUSTER_NAMES = "Names of the workload clusters created in the environment.";
    public static final String DATALAKE_CLUSTERS = "Datalake clusters created in the environment.";
    public static final String DATALAKE_CLUSTER_NAMES = "Names of the datalake clusters created in the environment.";
    public static final String DATALAKE_RESOURCES_NAMES = "Datalake cluster resources registered to the environment.";
    public static final String DATALAKE_RESOURCES = "Datalake cluster resources registered to the environment.";
    public static final String KERBEROS_CONFIGS_RESPONSE = "Kerberos configs in the environment.";
    public static final String STATUS = "Status of the environment.";

    private EnvironmentModelDescription() {
    }
}
