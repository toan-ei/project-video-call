package com.example.webrtc.controller;

import com.example.webrtc.request.AuthenticationRequest;
import com.example.webrtc.request.CheckTokenRequest;
import com.example.webrtc.request.LogoutRequest;
import com.example.webrtc.request.RefreshTokenRequest;
import com.example.webrtc.response.AuthenticationResponse;
import com.example.webrtc.response.CheckTokenResponse;
import com.example.webrtc.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/authentication")
@CrossOrigin(origins = "*")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest request){
        return authenticationService.login(request);
    }

    @PostMapping("/checkToken")
    public CheckTokenResponse checkToken(@RequestBody CheckTokenRequest request){
        return authenticationService.checkToken(request);
    }

    @PostMapping("/logout")
    public void logoutToken(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logoutToken(request);
    }

    @PostMapping("/refresh")
    public AuthenticationResponse refreshToken(@RequestBody RefreshTokenRequest request) throws ParseException, JOSEException {
        return authenticationService.refreshToken(request);
    }
}
