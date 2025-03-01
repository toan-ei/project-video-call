package com.nhom7.VideoCall.exception;

import com.nhom7.VideoCall.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handleGenericException(Exception exception){
        System.out.println(">>> bat loi ngoai le khong mong muon " + exception.getClass().getName());
        exception.printStackTrace();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(ErrorCode.EXCEPTION_CHUA_BAT.getMessage());
        apiResponse.setCode(ErrorCode.EXCEPTION_CHUA_BAT.getCode());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = ApplicationException.class)
    ResponseEntity<ApiResponse> handleApplicationException(ApplicationException applicationException){
        ErrorCode errorCode = applicationException.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException methodArgumentNotValidException){
        System.out.println(">>> Bắt lỗi validation!");

        methodArgumentNotValidException.getFieldErrors().forEach(error -> {
            System.out.println("Field: " + error.getField() + " | Default Message: " + error.getDefaultMessage());
        });
        String key = methodArgumentNotValidException.getFieldError().getDefaultMessage();
        ErrorCode errorCode = Arrays.stream(ErrorCode.values())
                .filter(errorCode1 -> errorCode1.name().equals(key))
                .findFirst()
                .orElse(ErrorCode.INVALID_KEY);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(errorCode.getMessage());
        apiResponse.setCode(errorCode.getCode());
        return ResponseEntity.badRequest().body(apiResponse);
    }
}
