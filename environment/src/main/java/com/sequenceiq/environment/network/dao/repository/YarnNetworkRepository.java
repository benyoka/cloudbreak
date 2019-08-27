package com.sequenceiq.environment.network.dao.repository;

import javax.transaction.Transactional;

import com.sequenceiq.authorization.resource.AuthorizationResource;
import com.sequenceiq.authorization.resource.AuthorizationResourceType;
import com.sequenceiq.environment.network.dao.domain.YarnNetwork;

@Transactional(Transactional.TxType.REQUIRED)
@AuthorizationResourceType(resource = AuthorizationResource.ENVIRONMENT)
public interface YarnNetworkRepository extends BaseNetworkRepository<YarnNetwork> {
}
