package com.raksmey.dev.tracking.config;

import brave.Tracer;
import io.micrometer.context.ContextExecutorService;
import io.micrometer.context.ContextRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(AsyncConfig.class);

    @Bean(name = "async")
    public Executor asyncExecutor(Tracer tracer) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);                 // Optimal for IO-bound tasks
        executor.setMaxPoolSize(100);                 // Scales under load
        executor.setQueueCapacity(50);                // Prevents OOM
        executor.setThreadNamePrefix("async-thread-"); // Debuggable threads
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // Handles overflow
        executor.setTaskDecorator(new MdcTaskDecorator()); // Preserves MDC logging context
        executor.setWaitForTasksToCompleteOnShutdown(true); // Graceful shutdown
        executor.setAwaitTerminationSeconds(30);       // Reasonable wait time
        executor.initialize(); // Initialize executor

        return executor;
    }

    /**
     * Handles uncaught exceptions from async methods.
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {
            @Override
            public void handleUncaughtException(Throwable ex, Method method, Object... params) {
                logger.error("Exception in async method: {} with params {}", method.getName(), params, ex);
            }
        };
    }
}
