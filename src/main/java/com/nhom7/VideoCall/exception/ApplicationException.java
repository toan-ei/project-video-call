package com.nhom7.VideoCall.exception;

import lombok.Data;

@Data
public class ApplicationException extends RuntimeException {
    private ErrorCode errorCode;

    public ApplicationException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
