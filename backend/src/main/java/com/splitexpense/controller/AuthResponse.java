package com.splitexpense.controller;

import com.splitexpense.model.User;

public record AuthResponse(
        int id,
        String name,
        String email
) {

    public static AuthResponse from(User user) {
        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}