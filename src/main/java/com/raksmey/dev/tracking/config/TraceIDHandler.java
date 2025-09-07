package com.raksmey.dev.tracking.config;

import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TraceIDHandler {
    private final Tracer tracer;

    public TraceIDHandler(Tracer tracer) {
        this.tracer = tracer;
    }

    public String getTraceId() {
        return Optional.of(tracer).map(Tracer::currentTraceContext).map(CurrentTraceContext::context).map(TraceContext::traceId).orElse("");
    }
}