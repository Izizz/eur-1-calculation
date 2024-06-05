package com.example.eurcertificatecalc.auth.service;

import com.example.eurcertificatecalc.auth.model.UserRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserService userService;

    @PostConstruct
    public void initialize(){

        UserRequest request = UserRequest.builder()
                .username("admin")
                .password("admin")
                .build();
        if(!userService.checkIfExistsByName(request.getUsername())){
            userService.create(request);
        }
    }
}
