package com.sequenceiq.datalake.flow.start.event;

import com.sequenceiq.datalake.flow.SdxContext;
import com.sequenceiq.datalake.flow.SdxEvent;

public class SdxSyncWaitRequest extends SdxEvent {

    public SdxSyncWaitRequest(Long sdxId, String userId, String requestId) {
        super(sdxId, userId, requestId);
    }

    public static SdxSyncWaitRequest from(SdxContext context) {
        return new SdxSyncWaitRequest(context.getSdxId(), context.getUserId(), context.getRequestId());
    }

    @Override
    public String selector() {
        return "SdxSyncWaitRequest";
    }

}
