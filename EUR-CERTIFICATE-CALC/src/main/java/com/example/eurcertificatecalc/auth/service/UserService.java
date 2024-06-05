package com.example.eurcertificatecalc.auth.service;



import com.example.eurcertificatecalc.auth.model.UserAuthenticationRequest;
import com.example.eurcertificatecalc.auth.model.UserAuthenticationResponse;
import com.example.eurcertificatecalc.auth.model.UserRequest;
import com.example.eurcertificatecalc.auth.model.UserResponse;

import java.util.List;

public interface UserService {

    UserAuthenticationResponse authorize(UserAuthenticationRequest request);

    UserResponse create(UserRequest request);

    UserResponse findById(Long userUid);

    List<UserResponse> findAll();

    boolean checkIfExistsByName(String name);

    UserResponse update(UserRequest request, Long userUid);

    void delete(Long userUid);
}