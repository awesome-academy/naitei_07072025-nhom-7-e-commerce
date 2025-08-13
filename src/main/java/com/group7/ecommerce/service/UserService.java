package com.group7.ecommerce.service;

import com.group7.ecommerce.dto.request.UserRegistrationDto;
import com.group7.ecommerce.dto.request.VerifyOtpDto;

public interface UserService {

    /**
     * Đăng ký user mới
     * @param dto thông tin đăng ký
     * @return thông báo kết quả
     */
    String registerUser(UserRegistrationDto dto);

    /**
     * Xác thực OTP
     * @param dto thông tin OTP
     * @return thông báo kết quả
     */
    String verifyOtp(VerifyOtpDto dto);

    /**
     * Gửi lại OTP
     * @param email email cần gửi lại OTP
     * @return thông báo kết quả
     */
    String resendOtp(String email);
}