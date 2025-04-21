package io.muzoo.ssc.project.backend.auth;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import io.muzoo.ssc.project.backend.User;
import io.muzoo.ssc.project.backend.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/api/test")
    public String test() {
        return "If this message is show, it means login is successful because we didn't set to permit this paths.";
    }

    @PostMapping("/api/login")
    public SimpleResponseDTO login(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                request.logout();
            }
            request.login(username, password);
            return SimpleResponseDTO
                    .builder()
                    .success(true)
                    .message("Login successful")
                    .build();
        } catch (ServletException e) {
            return SimpleResponseDTO
                    .builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @GetMapping("api/logout")
    public SimpleResponseDTO logout(HttpServletRequest request) {
        try {
            request.logout();
            return SimpleResponseDTO
                    .builder()
                    .success(true)
                    .message("Logout Successful")
                    .build();
        } catch (ServletException e) {
            return SimpleResponseDTO
                    .builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @PostMapping("api/register")
    public SimpleResponseDTO register(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return SimpleResponseDTO.builder()
                    .success(false)
                    .message("Username cannot be empty")
                    .build();
        }

        String username = user.getUsername().trim().toLowerCase();

        if (!username.matches("^[a-z0-9._-]+$")) {
            return SimpleResponseDTO.builder()
                    .success(false)
                    .message("Username can only contain lowercase letters, numbers, dots, underscores, and dashes")
                    .build();
        }

        if (userRepository.findByUsername(username) != null) {
            return SimpleResponseDTO.builder()
                    .success(false)
                    .message("Username is already taken")
                    .build();
        }

        if (user.getPassword() == null || user.getPassword().length() < 5) {
            return SimpleResponseDTO.builder()
                    .success(false)
                    .message("Password must be at least 5 characters long")
                    .build();
        }

        try {
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole("USER");
            userRepository.save(user);

            return SimpleResponseDTO.builder()
                    .success(true)
                    .message("User registered successfully")
                    .build();
        } catch (Exception e) {
            return SimpleResponseDTO.builder()
                    .success(false)
                    .message("An error occurred while registering. Please try again.")
                    .build();
        }
    }

    @GetMapping("/api/auth/check")
    public SimpleResponseDTO checkAuthentication() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            return SimpleResponseDTO.builder()
                    .success(true)
                    .message("User is authenticated")
                    .build();
        } else {
            return SimpleResponseDTO.builder()
                    .success(false)
                    .message("User is not authenticated")
                    .build();
        }
    }
}
