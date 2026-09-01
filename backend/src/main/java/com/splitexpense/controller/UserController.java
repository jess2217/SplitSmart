package com.splitexpense.controller;

import com.splitexpense.model.User;
import com.splitexpense.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // =========================================
    // UPDATE PROFILE
    // =========================================

    @PutMapping("/{userId}")
    public User updateProfile(
            @PathVariable int userId,
            @RequestBody UpdateProfileRequest request) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Profile data is required."
            );
        }

        if (request.name() == null ||
                request.name().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Name is required."
            );
        }

        if (request.email() == null ||
                request.email().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email is required."
            );
        }

        String name = request.name().trim();
        String email = request.email().trim().toLowerCase();

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found."
                        )
                );

        /*
         * Prevent duplicate email addresses.
         *
         * Allow the user to keep their own
         * existing email.
         */
        if (!email.equalsIgnoreCase(user.getEmail())
                && userRepository.existsByEmail(email)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered."
            );
        }

        user.setName(name);
        user.setEmail(email);

        /*
         * Keep the linked Student synchronized.
         *
         * This is important for SplitSmart because
         * groups and expenses use Student records.
         */
        if (user.getStudent() != null) {
            user.getStudent().setName(name);
            user.getStudent().setEmail(email);
        }

        return userRepository.save(user);
    }

    // =========================================
    // REQUEST DTO
    // =========================================

    public record UpdateProfileRequest(
            String name,
            String email
    ) {
    }
}