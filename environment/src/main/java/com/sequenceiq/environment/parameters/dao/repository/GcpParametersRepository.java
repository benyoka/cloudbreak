package com.sequenceiq.environment.parameters.dao.repository;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.sequenceiq.authorization.resource.AuthorizationResource;
import com.sequenceiq.authorization.resource.AuthorizationResourceType;
import com.sequenceiq.environment.parameters.dao.domain.GcpParameters;

@Transactional(TxType.REQUIRED)
@AuthorizationResourceType(resource = AuthorizationResource.ENVIRONMENT)
public interface GcpParametersRepository extends BaseParametersRepository<GcpParameters> {
}
