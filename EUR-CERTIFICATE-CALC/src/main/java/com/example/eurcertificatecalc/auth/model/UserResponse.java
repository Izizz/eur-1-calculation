package com.example.eurcertificatecalc.auth.model;

import com.example.eurcertificatecalc.auth.data.entity.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String uid;

    private String name;


    private LocalDateTime createdAt;

    public static UserResponse instance(User user) {

        return UserResponse.builder()
                .uid(user.getId().toString())
                .name(user.getUsername())
                .build();
    }

    public static List<UserResponse> instance(List<User> users) {

        return users.stream()
                .map(UserResponse::instance)
                .collect(Collectors.toList());
    }
}