package com.raksmey.dev.tracking.config;


import brave.Tracing;
import brave.context.slf4j.MDCScopeDecorator;
import brave.propagation.B3Propagation;
import brave.propagation.ThreadLocalCurrentTraceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrackingConfig {

//    @Bean
//    public Tracing braveTracing() {
//        return Tracing.newBuilder()
//                .propagationFactory(B3Propagation.newFactoryBuilder().injectFormat(B3Propagation.Format.MULTI).build())
//                .build();
//    }
    @Bean
    public Tracing tracing() {
        return Tracing.newBuilder()
                .currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder()
                        .addScopeDecorator(MDCScopeDecorator.get()) // Better MDC integration
                        .build())
                .build();
    }

}
