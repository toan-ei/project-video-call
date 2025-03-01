package com.nhom7.VideoCall.configuration;

import com.nhom7.VideoCall.entity.User;
import com.nhom7.VideoCall.enums.UserStatus;
import com.nhom7.VideoCall.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@Slf4j
public class ApplicationInitConfig {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
            if(userRepository.findByUsername("admin").isEmpty()){
                 var role = new HashSet<String>();
                 role.add("ADMIN");
                 User user = User.builder()
                         .username("admin")
                         .password(passwordEncoder.encode("admin"))
                         .role(role)
                         .avatarUrl("/uploads/avatar-goc.jpg")
                         .status(UserStatus.ONLINE)
                         .build();
                 userRepository.save(user);
                 log.warn("đã tạo thành công user admin với password mặc định : admin, hay thay đổi nó");
            }
        };
    }
}
