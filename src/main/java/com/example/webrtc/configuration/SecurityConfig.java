package com.example.webrtc.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final String[] PUBLIC_API = {
            "/users/createUser",
            "/authentication/login",
            "/authentication/checkToken",
    };
    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth ->
                auth.requestMatchers(PUBLIC_API).permitAll()
                        .requestMatchers("/", "/index.html", "/style.css", "/main.js", "login.js", "register.js", "myinfor.js" , "refreshToken.js",  "/images/avt.jpg", "/socket").permitAll()
                        .anyRequest().authenticated());

        httpSecurity.oauth2ResourceServer(oauth -> oauth.jwt(jwtConfigurer ->
                        jwtConfigurer.decoder(customJwtDecoder)));

        httpSecurity.csrf(csrf -> csrf.disable());
        return httpSecurity.build();
    }
}
