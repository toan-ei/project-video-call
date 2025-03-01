package com.nhom7.VideoCall.controller;

import com.nhom7.VideoCall.dto.request.AuthenticationRequest;
import com.nhom7.VideoCall.dto.request.TestTokenRequest;
import com.nhom7.VideoCall.dto.response.ApiResponse;
import com.nhom7.VideoCall.dto.response.AuthenticationResponse;
import com.nhom7.VideoCall.dto.response.TestTokenResponse;
import com.nhom7.VideoCall.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@Slf4j
@RestController
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticated(@RequestBody AuthenticationRequest authenticationRequest){
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.authenticated(authenticationRequest))
                .build();
    }

    @PostMapping("/testToken")
    ApiResponse<TestTokenResponse> introspect(@RequestBody TestTokenRequest testTokenRequest) throws ParseException, JOSEException {
        return ApiResponse.<TestTokenResponse>builder()
                .result(authenticationService.introspect(testTokenRequest))
                .build();
    }
}
