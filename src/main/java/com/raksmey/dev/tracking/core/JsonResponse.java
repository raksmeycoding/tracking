package com.raksmey.dev.tracking.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonResponse<T> {
    @JsonProperty("status")
    private int status;
    @JsonProperty("message")
    private String message;
    @JsonProperty("data")
    private T data;
}
