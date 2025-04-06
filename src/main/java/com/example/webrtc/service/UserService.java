package com.example.webrtc.service;

import com.example.webrtc.entity.User;
import com.example.webrtc.mapper.UserMapper;
import com.example.webrtc.repository.UserRepository;
import com.example.webrtc.request.UserCreateRequest;
import com.example.webrtc.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserResponse createUser(UserCreateRequest request){
        if(userRepository.existsByUsername(request.getUsername())) throw new RuntimeException("username da ton tai");
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus("ONLINE");
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public List<UserResponse> getAll(){
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    public UserResponse getUser(String username){
        return userMapper.toUserResponse(userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("loi tim username")));
    }

    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        System.out.println("context: " + context);
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("khong tim thay user thong qua username"));

        return userMapper.toUserResponse(user);
    }
}
