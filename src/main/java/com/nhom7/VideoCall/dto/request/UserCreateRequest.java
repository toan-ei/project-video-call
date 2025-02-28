package com.nhom7.VideoCall.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    @NotBlank(message = "DE_TRONG_USERNAME")
    @Size(min = 5, max = 20, message = "GIOI_HAN_TU_USERNAME")
    private String username;
    @NotBlank(message = "DE_TRONG_PASSWORD")
    @Size(min = 8, message = "GIOI_HAN_TU_PASSWORD")
    private String password;
    @NotBlank(message = "DE_TRONG_FULLNAME")
    private String fullName;
    @NotNull(message = "DE_TRONG_NGAYSINH")
    @Past(message = "LOI_NGAYSINH")
    private LocalDate dob;
}
