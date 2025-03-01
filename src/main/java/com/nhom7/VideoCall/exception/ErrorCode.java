package com.nhom7.VideoCall.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    LOI_NGAYSINH(2007, "Ngày sinh phải là một ngày trong quá khứ"),
    GIOI_HAN_TU_PASSWORD(2006, "Mật khẩu phải có ít nhất 8 ký tự"),
    GIOI_HAN_TU_USERNAME(2005, "username phải từ 5 đến 20 kí tự"),
    DE_TRONG_NGAYSINH(2004, "Ngày sinh không được để trống"),
    DE_TRONG_FULLNAME(2003, "Họ và tên không được để trống"),
    DE_TRONG_PASSWORD(2002, "Mật khẩu không được để trống"),
    DE_TRONG_USERNAME(2001, "username không được để trống"),
    CANNOT_CREATE_TOKEN(1005, "không thể tạo token"),
    UNAUTHENTICATED(1004, "xác thực thất bại"),
    INVALID_KEY(1003, "từ khóa rạng buộc dữ liệu không hợp lệ"),
    KHONG_TIM_THAY_USER(1002, "không tìm thấy user"),
    USERNAME_DA_TON_TAI(1001, "username đã tồn tại"),
    EXCEPTION_CHUA_BAT(9999, "lỗi chưa được customize");


    private int code;
    private String message;

    ErrorCode(int code, String message){
        this.code = code;
        this.message = message;
    }
}
