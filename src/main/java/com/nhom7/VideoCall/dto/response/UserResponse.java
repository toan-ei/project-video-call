package com.nhom7.VideoCall.dto.response;

import com.nhom7.VideoCall.enums.UserStatus;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private String fullName;
    private String avatarUrl;
    private LocalDate dob;
    private UserStatus status;
    private Set<String> role;
}
