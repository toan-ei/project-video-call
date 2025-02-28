package com.nhom7.VideoCall.service;

import com.nhom7.VideoCall.dto.request.UserCreateRequest;
import com.nhom7.VideoCall.dto.request.UserUpdateRequest;
import com.nhom7.VideoCall.dto.response.UserResponse;
import com.nhom7.VideoCall.entity.User;
import com.nhom7.VideoCall.enums.UserRole;
import com.nhom7.VideoCall.enums.UserStatus;
import com.nhom7.VideoCall.exception.ApplicationException;
import com.nhom7.VideoCall.exception.ErrorCode;
import com.nhom7.VideoCall.mapper.UserMapper;
import com.nhom7.VideoCall.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreateRequest request){
        if(userRepository.existsByUsername(request.getUsername()))
            throw new ApplicationException(ErrorCode.USERNAME_DA_TON_TAI);
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Set<String> roles = new HashSet<>();
        roles.add(UserRole.USER.name());
        user.setRole(roles);
        user.setAvatarUrl("/uploads/avatar-goc.jpg");
        user.setStatus(UserStatus.ONLINE);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public List<UserResponse> getUsers(){
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    public UserResponse getUser(String userId){
        return userMapper.toUserResponse(userRepository.findById(userId).orElseThrow(() -> new ApplicationException(ErrorCode.KHONG_TIM_THAY_USER)));
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request){
        User user = userRepository.findById(userId).orElseThrow(() -> new ApplicationException(ErrorCode.KHONG_TIM_THAY_USER));

        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }
}
