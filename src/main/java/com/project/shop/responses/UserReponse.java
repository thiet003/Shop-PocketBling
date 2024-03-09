package com.project.shop.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shop.models.Product;
import com.project.shop.models.Role;
import com.project.shop.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserReponse {

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber;


    private String address;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    public static UserReponse fromUser(User user)
    {
        UserReponse userReponse =
                UserReponse.builder()
                        .fullName(user.getFullName())
                        .phoneNumber(user.getPhoneNumber())
                        .address(user.getAddress())
                        .dateOfBirth(user.getDateOfBirth())
                        .build();
        return userReponse;
    }
}
