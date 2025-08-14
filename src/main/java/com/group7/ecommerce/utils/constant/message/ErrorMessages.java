package com.group7.ecommerce.utils.constant.message;

public class ErrorMessages {
    public static final String EMAIL_EXISTS = "Email đã được đăng ký";
    public static final String USER_NOT_FOUND = "Không tìm thấy tài khoản với email này";
    public static final String OTP_EXPIRED = "OTP đã hết hạn";
    public static final String OTP_INVALID = "OTP không đúng";
    public static final String EMAIL_ALREADY_VERIFIED = "Email đã được xác nhận";
    public static final String LOGIN_DATA_NULL = "Thông tin đăng nhập không được để trống";
    public static final String EMAIL_NOT_VERIFIED = "Tài khoản chưa được xác thực email";

    private ErrorMessages() {
        // Utility class
    }
}
