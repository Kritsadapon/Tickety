package io.muzoo.ssc.project.backend.Users;

import io.muzoo.ssc.project.backend.User;
import io.muzoo.ssc.project.backend.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.rmi.ServerException;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get currently authenticated user
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    // Get user by username
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Change user display name (only for authenticated user)
    public User changeUserDisplayName(String displayName) {
        User currentUser = getAuthenticatedUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }
        currentUser.setDisplayName(displayName);
        return userRepository.save(currentUser);
    }

    // Change user password
    public void changeUserPassword(String oldPassword, String newPassword) throws ServerException {
        User currentUser = getAuthenticatedUser();
        if (currentUser == null) {
            throw new ServerException("User not authenticated");
        }
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            throw new ServerException("Old password is incorrect");
        }
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
    }

    public User uploadProfilePicture(byte[] imageBytes, String contentType) {
        User currentUser = getAuthenticatedUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        // Store the image bytes directly in the database
        currentUser.setProfilePicture(imageBytes);
        return userRepository.save(currentUser);
    }
}
