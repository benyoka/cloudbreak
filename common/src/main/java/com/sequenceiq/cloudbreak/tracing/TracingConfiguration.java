package com.sequenceiq.cloudbreak.tracing;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sequenceiq.cloudbreak.common.json.Json;
import com.sequenceiq.cloudbreak.logger.MDCBuilder;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.contrib.jaxrs2.client.ClientSpanDecorator;
import io.opentracing.contrib.jaxrs2.client.ClientTracingFeature;
import io.opentracing.contrib.jaxrs2.server.ServerSpanDecorator;
import io.opentracing.contrib.jaxrs2.server.ServerTracingDynamicFeature;
import io.opentracing.contrib.jdbc.TracingDriver;
import io.opentracing.util.GlobalTracer;

@Configuration
public class TracingConfiguration {

    private final Tracer tracer;

    @Value("${opentracing.jdbc.enabled:true}")
    private boolean jdbcEnabled;

    public TracingConfiguration(Tracer tracer) {
        this.tracer = tracer;
        GlobalTracer.registerIfAbsent(tracer);
    }

    @PostConstruct
    public void initTracingProperties() {
        TracingDriver.setTraceEnabled(jdbcEnabled);
        TracingDriver.setInterceptorProperty(true);
        TracingDriver.setInterceptorMode(true);
    }

    @Bean
    public ServerTracingDynamicFeature serverTracingDynamicFeature() {
        ServerTracingDynamicFeature.Builder serverTracingFeatureBuilder = new ServerTracingDynamicFeature.Builder(tracer);
        serverTracingFeatureBuilder.withTraceSerialization(false);
        serverTracingFeatureBuilder.withDecorators(List.of(new SpanDecorator()));
        return serverTracingFeatureBuilder.build();
    }

    @Bean
    public ClientTracingFeature clientTracingFeature() {
        ClientTracingFeature.Builder clientTracingFeatureBuilder = new ClientTracingFeature.Builder(tracer);
        clientTracingFeatureBuilder.withTraceSerialization(false);
        clientTracingFeatureBuilder.withDecorators(List.of(new SpanDecorator()));
        return clientTracingFeatureBuilder.build();
    }

    public static class SpanDecorator implements ServerSpanDecorator, ClientSpanDecorator {

        @Override
        public void decorateRequest(ContainerRequestContext requestContext, Span span) {
            MDCBuilder.addTraceId(span.context().toTraceId());
            MDCBuilder.addSpanId(span.context().toSpanId());
            TracingUtil.setTagsFromMdc(span);
            span.setTag(TracingUtil.HEADERS, Json.silent(requestContext.getHeaders()).getValue());
        }

        @Override
        public void decorateResponse(ContainerResponseContext responseContext, Span span) {
            Response.StatusType statusInfo = responseContext.getStatusInfo();
            if (statusInfo.getFamily() != Response.Status.Family.SUCCESSFUL) {
                span.setTag(TracingUtil.ERROR, true);
                span.log(Map.of(TracingUtil.RESPONSE_CODE, statusInfo.getStatusCode(), "reasonPhrase", statusInfo.getReasonPhrase()));
            }
        }

        @Override
        public void decorateRequest(ClientRequestContext requestContext, Span span) {
            MDCBuilder.addTraceId(span.context().toTraceId());
            MDCBuilder.addSpanId(span.context().toSpanId());
            TracingUtil.setTagsFromMdc(span);
            span.setTag(TracingUtil.HEADERS, Json.silent(requestContext.getHeaders()).getValue());
        }

        @Override
        public void decorateResponse(ClientResponseContext responseContext, Span span) {
            Response.StatusType statusInfo = responseContext.getStatusInfo();
            if (statusInfo.getFamily() != Response.Status.Family.SUCCESSFUL) {
                span.setTag(TracingUtil.ERROR, true);
                span.log(Map.of(TracingUtil.RESPONSE_CODE, statusInfo.getStatusCode(), "reasonPhrase", statusInfo.getReasonPhrase()));
            }
        }
    }
}
