package com.sequenceiq.environment.network.dao.repository;

import javax.transaction.Transactional;

import com.sequenceiq.authorization.resource.AuthorizationResource;
import com.sequenceiq.authorization.resource.AuthorizationResourceType;
import com.sequenceiq.environment.network.dao.domain.OpenstackNetwork;

@Transactional(Transactional.TxType.REQUIRED)
@AuthorizationResourceType(resource = AuthorizationResource.ENVIRONMENT)
public interface OpenstackNetworkRepository extends BaseNetworkRepository<OpenstackNetwork> {
}
