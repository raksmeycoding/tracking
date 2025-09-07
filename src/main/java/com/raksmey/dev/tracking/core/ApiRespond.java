package com.raksmey.dev.tracking.core;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiRespond<T> {
    private int code;
    private String message;
    private T data;

    public static <O> ApiRespond<O> success(O o) {
        return ApiRespond.<O>builder()
                .code(200)
                .message("success")
                .data(o)
                .build();
    }

    public static <O> ApiRespond<O> success(O o, String message) {
        return ApiRespond.<O>builder()
                .code(200)
                .message(message)
                .data(o)
                .build();
    }
}
