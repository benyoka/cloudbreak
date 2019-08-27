package com.sequenceiq.environment.parameters.dao.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("CUMULUS_YARN")
public class CumulusYarnParameters extends BaseParameters {

}
