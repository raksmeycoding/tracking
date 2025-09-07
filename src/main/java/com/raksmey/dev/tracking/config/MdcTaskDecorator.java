package com.raksmey.dev.tracking.config;

import io.micrometer.context.ContextRegistry;
import io.micrometer.context.ContextSnapshot;
import io.micrometer.context.ContextSnapshotFactory;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

public class MdcTaskDecorator implements TaskDecorator {

    private final ContextSnapshotFactory contextSnapshotFactory = ContextSnapshotFactory.builder()
            .contextRegistry(ContextRegistry.getInstance())
            .build();

    @Override
    public Runnable decorate(Runnable runnable) {
        // Capture both MDC and Micrometer context
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        ContextSnapshot contextSnapshot = contextSnapshotFactory.captureAll();


        return () -> {
            try {
                // Restore MDC context
                if (mdcContext != null) {
                    MDC.setContextMap(mdcContext);
                }

                // Restore Micrometer context (CRITICAL for tracing)
                try (ContextSnapshot.Scope scope = contextSnapshot.setThreadLocals()) {
                    runnable.run();

                }

            } finally {
                MDC.clear();
                // Micrometer context is automatically cleaned up by try-with-resources
            }
        };
    }
}