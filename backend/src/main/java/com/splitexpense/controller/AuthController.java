package com.splitexpense.controller;

import com.splitexpense.model.Student;
import com.splitexpense.model.User;
import com.splitexpense.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // SIGN UP
    // =========================

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse signup(
            @RequestBody SignupRequest request) {

        // -------------------------
        // VALIDATE REQUEST
        // -------------------------

        if (request == null ||
                request.name() == null ||
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

        if (request.password() == null ||
                request.password().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password is required."
            );
        }

        // -------------------------
        // CLEAN EMAIL
        // -------------------------

        String email =
                request.email()
                        .trim()
                        .toLowerCase();

        // -------------------------
        // CHECK DUPLICATE EMAIL
        // -------------------------

        if (userRepository.existsByEmail(email)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this email already exists."
            );
        }

        // -------------------------
        // CREATE STUDENT
        // -------------------------

        Student student =
                new Student(
                        request.name().trim(),
                        email,
                        ""
                );

        // -------------------------
        // HASH PASSWORD
        // -------------------------

        String hashedPassword =
                passwordEncoder.encode(
                        request.password()
                );

        // -------------------------
        // CREATE USER
        // -------------------------

        User user =
                new User(
                        request.name().trim(),
                        email,
                        hashedPassword
                );

        // -------------------------
        // LINK STUDENT TO USER
        // -------------------------

        user.setStudent(student);

        // -------------------------
        // SAVE USER + STUDENT
        // -------------------------

        User savedUser =
        userRepository.save(user);

return AuthResponse.from(savedUser);
    }

    // =========================
    // LOGIN
    // =========================

    @PostMapping("/login")
   public AuthResponse login(
        @RequestBody LoginRequest request) {

        // -------------------------
        // VALIDATE REQUEST
        // -------------------------

        if (request == null ||
                request.email() == null ||
                request.password() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email and password are required."
            );
        }

        // -------------------------
        // CLEAN EMAIL
        // -------------------------

        String email =
                request.email()
                        .trim()
                        .toLowerCase();

        // -------------------------
        // FIND USER
        // -------------------------

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Invalid email or password."
                                )
                        );

        // -------------------------
        // CHECK PASSWORD
        // -------------------------

        String storedPassword =
                user.getPassword();

        boolean passwordMatches;

        /*
         * New accounts have BCrypt passwords.
         */
        if (storedPassword != null &&
                storedPassword.startsWith("$2")) {

            passwordMatches =
                    passwordEncoder.matches(
                            request.password(),
                            storedPassword
                    );

        } else {

            /*
             * Existing accounts created before
             * BCrypt was introduced.
             */
            passwordMatches =
                    storedPassword != null &&
                    storedPassword.equals(
                            request.password()
                    );

            /*
             * Upgrade the old plaintext password
             * to BCrypt after successful login.
             */
            if (passwordMatches) {

                user.setPassword(
                        passwordEncoder.encode(
                                request.password()
                        )
                );

                userRepository.save(user);
            }
        }

        // -------------------------
        // LOGIN FAILED
        // -------------------------

        if (!passwordMatches) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password."
            );
        }

        // -------------------------
        // LOGIN SUCCESS
        // -------------------------

       return AuthResponse.from(user);
    }

    // =========================
    // REQUEST DTOs
    // =========================

    public record SignupRequest(
            String name,
            String email,
            String password
    ) {
    }

    public record LoginRequest(
            String email,
            String password
    ) {
    }
}