package com.group7.ecommerce.dto.response;

public record UpdateProfileResponse(
        boolean success,
        String message,
        ShowProfileResponse updatedProfile
) {
    public static UpdateProfileResponse success(String message, ShowProfileResponse profile) {
        return new UpdateProfileResponse(true, message, profile);
    }

    public static UpdateProfileResponse error(String message) {
        return new UpdateProfileResponse(false, message, null);
    }
}
