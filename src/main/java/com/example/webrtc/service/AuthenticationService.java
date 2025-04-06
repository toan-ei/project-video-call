package com.example.webrtc.service;

import com.example.webrtc.entity.LogoutToken;
import com.example.webrtc.entity.User;
import com.example.webrtc.repository.LogoutRepository;
import com.example.webrtc.repository.UserRepository;
import com.example.webrtc.request.AuthenticationRequest;
import com.example.webrtc.request.CheckTokenRequest;
import com.example.webrtc.request.LogoutRequest;
import com.example.webrtc.request.RefreshTokenRequest;
import com.example.webrtc.response.AuthenticationResponse;
import com.example.webrtc.response.CheckTokenResponse;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LogoutRepository logoutRepository;
    @Value("${jwt.signerKey}")
    private String signerKey;

    public AuthenticationResponse login(AuthenticationRequest request){
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("khong tim thay user"));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authentication = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authentication) {
            throw new RuntimeException("2 password khong giong nhau");
        }
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authentication(true)
                .build();
    }

    public CheckTokenResponse checkToken(CheckTokenRequest request){
        String token = request.getToken();
        boolean valid = true;
        try{
            verifyToken(token);
        }
        catch (Exception e){
            valid = false;
        }
        return CheckTokenResponse.builder()
                .authentication(valid)
                .build();
    }

    public void logoutToken(LogoutRequest request) throws ParseException, JOSEException {
        var token = verifyToken(request.getToken());
        String tokenId = token.getJWTClaimsSet().getJWTID();
        Date expire = token.getJWTClaimsSet().getExpirationTime();
        logoutRepository.save(LogoutToken.builder()
                        .id(tokenId)
                        .expire(expire)
                .build());
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        var token = verifyToken(request.getToken());
        String tokenId = token.getJWTClaimsSet().getJWTID();
        Date expireTime = token.getJWTClaimsSet().getExpirationTime();
        logoutRepository.save(LogoutToken.builder()
                        .expire(expireTime)
                        .id(tokenId)
                .build());

        var username = token.getJWTClaimsSet().getSubject();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("khong tim thay user qua username"));
        String tokenNew = generateToken(user);
        return AuthenticationResponse.builder()
                .token(tokenNew)
                .authentication(true)
                .build();
    }

    private String generateToken(User user){
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("emtoan")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier jwsVerifier = new MACVerifier(signerKey.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verifier = signedJWT.verify(jwsVerifier);

        if (!(verifier && expityTime.after(new Date()))){
            throw new JOSEException("khong xac thuc");
        }
        if(logoutRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new RuntimeException("token da nam trong bang invalidated");
        return signedJWT;
    }
}
