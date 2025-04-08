package com.example.webrtc.mapper;

import com.example.webrtc.entity.User;
import com.example.webrtc.request.UserCreateRequest;
import com.example.webrtc.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface UserMapper {
    User toUser(UserCreateRequest request);
    UserResponse toUserResponse(User user);
}
