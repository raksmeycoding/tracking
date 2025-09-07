package com.raksmey.dev.tracking.util;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Component
public class AutoAsyncCompletableFuture {

    private final Executor asyncExecutor;


    public AutoAsyncCompletableFuture(@Qualifier("async") Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }


    public <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, asyncExecutor);
    }

    public CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, asyncExecutor);
    }
}
