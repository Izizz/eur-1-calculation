package com.example.eurcertificatecalc.auth.service.impl;

import com.example.eurcertificatecalc.auth.data.service.UserDataService;
import com.example.eurcertificatecalc.auth.model.UserAuthenticationRequest;
import com.example.eurcertificatecalc.auth.model.UserAuthenticationResponse;
import com.example.eurcertificatecalc.auth.model.UserRequest;
import com.example.eurcertificatecalc.auth.model.UserResponse;
import com.example.eurcertificatecalc.auth.service.JwtService;
import com.example.eurcertificatecalc.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDataService userDataService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserAuthenticationResponse authorize(UserAuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var user = userDataService.findByName(request.getUsername());
        var jwtToken = jwtService.generateToken(user);

        return UserAuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public UserResponse create(UserRequest request) {
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        return userDataService.create(request);
    }

    @Override
    public UserResponse findById(Long userUid) {
        return userDataService.findById(userUid);
    }


    @Override
    public List<UserResponse> findAll() {
        return userDataService.findAll();
    }

    @Override
    public boolean checkIfExistsByName(String name) {
        return userDataService.checkExistsByName(name);
    }

    @Override
    public UserResponse update(UserRequest request, Long userUid) {

        request.setPassword(passwordEncoder.encode(request.getPassword()));

        return userDataService.update(request, userUid);
    }


    @Override
    public void delete(Long userUid) {
        userDataService.delete(userUid);
    }
}
