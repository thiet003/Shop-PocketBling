package com.project.shop.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shop.models.User;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("user")
    private User user;
}
