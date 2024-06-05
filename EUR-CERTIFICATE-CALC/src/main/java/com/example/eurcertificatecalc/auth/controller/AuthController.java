package com.example.eurcertificatecalc.auth.controller;

import com.example.eurcertificatecalc.auth.model.UserAuthenticationRequest;
import com.example.eurcertificatecalc.auth.model.UserAuthenticationResponse;
import com.example.eurcertificatecalc.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserAuthenticationResponse> authenticate(@Valid @RequestBody UserAuthenticationRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation failed for request. " + bindingResult.getAllErrors());
        }
        log.info("Authenticating user: [{}]", request.getUsername());

        return ResponseEntity.ok(userService.authorize(request));
    }

}
