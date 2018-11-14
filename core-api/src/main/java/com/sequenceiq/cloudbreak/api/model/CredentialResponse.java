package com.sequenceiq.cloudbreak.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sequenceiq.cloudbreak.api.model.users.WorkspaceResourceResponse;
import com.sequenceiq.cloudbreak.doc.ModelDescriptions;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class CredentialResponse extends CredentialBase {

    @ApiModelProperty(ModelDescriptions.ID)
    private Long id;

    @ApiModelProperty(ModelDescriptions.PUBLIC_IN_ACCOUNT)
    private boolean publicInAccount = true;

    @ApiModelProperty(ModelDescriptions.ATTRIBUTES)
    private SecretResponse attributes;

    @ApiModelProperty
    private WorkspaceResourceResponse workspace;

    @JsonProperty("public")
    public boolean isPublicInAccount() {
        return publicInAccount;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPublicInAccount(boolean publicInAccount) {
        this.publicInAccount = publicInAccount;
    }

    public SecretResponse getAttributes() {
        return attributes;
    }

    public void setAttributes(SecretResponse attributes) {
        this.attributes = attributes;
    }

    public WorkspaceResourceResponse getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceResourceResponse workspace) {
        this.workspace = workspace;
    }

}
