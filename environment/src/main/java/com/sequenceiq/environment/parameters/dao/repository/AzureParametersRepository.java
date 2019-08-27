package com.sequenceiq.environment.parameters.dao.repository;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.sequenceiq.authorization.resource.AuthorizationResource;
import com.sequenceiq.authorization.resource.AuthorizationResourceType;
import com.sequenceiq.environment.parameters.dao.domain.AzureParameters;

@Transactional(TxType.REQUIRED)
@AuthorizationResourceType(resource = AuthorizationResource.ENVIRONMENT)
public interface AzureParametersRepository extends BaseParametersRepository<AzureParameters> {
}
