package com.nhom7.VideoCall.service;

import com.nhom7.VideoCall.dto.request.AuthenticationRequest;
import com.nhom7.VideoCall.dto.request.TestTokenRequest;
import com.nhom7.VideoCall.dto.response.AuthenticationResponse;
import com.nhom7.VideoCall.dto.response.TestTokenResponse;
import com.nhom7.VideoCall.entity.User;
import com.nhom7.VideoCall.exception.ApplicationException;
import com.nhom7.VideoCall.exception.ErrorCode;
import com.nhom7.VideoCall.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Slf4j
@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    @Value("${jwt.signerKey}")
    protected String signerKey;


    public TestTokenResponse introspect(TestTokenRequest testTokenRequest) throws JOSEException, ParseException {
        var token = testTokenRequest.getToken();

        JWSVerifier jwsVerifier = new MACVerifier(signerKey.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date date = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verifier = signedJWT.verify(jwsVerifier);
        return TestTokenResponse.builder()
                .valid(verifier && date.after(new Date()))
                .build();
    }

    public AuthenticationResponse authenticated(AuthenticationRequest authenticationRequest){
        var user = userRepository.findByUsername(authenticationRequest.getUsername()).orElseThrow(() -> new ApplicationException(ErrorCode.KHONG_TIM_THAY_USER));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
        if(!authenticated){
            throw new ApplicationException(ErrorCode.UNAUTHENTICATED);
        }
        var token = createToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    private String createToken(User user){
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("Video call")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try{
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        }
        catch (JOSEException e){
            log.error("không thể tạo token ", e);
            throw new ApplicationException(ErrorCode.CANNOT_CREATE_TOKEN);
        }

    }

    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getRole())){
            user.getRole().forEach(role -> stringJoiner.add(role));
        }
        return stringJoiner.toString();
    }


}
