package com.nhom7.VideoCall.mapper;

import com.nhom7.VideoCall.dto.request.UserCreateRequest;
import com.nhom7.VideoCall.dto.request.UserUpdateRequest;
import com.nhom7.VideoCall.dto.response.UserResponse;
import com.nhom7.VideoCall.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreateRequest request);
    UserResponse toUserResponse(User user);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
