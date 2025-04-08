package com.example.webrtc.configuration;

import com.example.webrtc.request.CheckTokenRequest;
import com.example.webrtc.response.CheckTokenResponse;
import com.example.webrtc.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    private String signerKey;
    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        CheckTokenResponse checkTokenResponse = authenticationService.checkToken(CheckTokenRequest.builder()
                        .token(token)
                .build());
        boolean valid = checkTokenResponse.isAuthentication();
        if(!valid){
            throw new RuntimeException("token false");
        }
        if(Objects.isNull(nimbusJwtDecoder)){
            SecretKeySpec spec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(spec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
        return nimbusJwtDecoder.decode(token);
    }
}
