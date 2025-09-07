package com.raksmey.dev.tracking.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRspDto {
    private String userId;
    private String firstName;
    private String lastName;
    private String userName;
    private String gender;
    private Integer age;

    @JsonCreator
    public UserRspDto(
            @JsonProperty("userId") String userId,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("userName") String userName,
            @JsonProperty("gender") String gender,
            @JsonProperty("age") int age
    ) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.gender = gender;
        this.age = age;
    }
}
