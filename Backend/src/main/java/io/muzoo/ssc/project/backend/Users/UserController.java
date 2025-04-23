package io.muzoo.ssc.project.backend.Users;

import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import io.muzoo.ssc.project.backend.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.rmi.ServerException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Get the currently authenticated user's information
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        try {
            User user = userService.getAuthenticatedUser();
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    // Change the authenticated user's display name
    @PostMapping("/change-display-name")
    public ResponseEntity<SimpleResponseDTO> changeDisplayName(@RequestParam String displayName) {
        try {
            User updatedUser = userService.changeUserDisplayName(displayName);
            return ResponseEntity.ok(SimpleResponseDTO.builder()
                    .success(true)
                    .message("Display name updated successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Change password
    @PostMapping("/change-password")
    public ResponseEntity<SimpleResponseDTO> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        try {
            userService.changeUserPassword(oldPassword, newPassword);
            return ResponseEntity.ok(SimpleResponseDTO.builder()
                    .success(true)
                    .message("Password changed successfully")
                    .build());
        } catch (ServerException e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Upload profile picture (requires authentication)
    @PutMapping("/profile-picture")
    public ResponseEntity<User> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        try {
            byte[] imageBytes = file.getBytes();
            String contentType = file.getContentType();
            User updatedUser = userService.uploadProfilePicture(imageBytes, contentType);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Get current user's profile picture (requires authentication)
    @GetMapping("/profile-picture")
    public ResponseEntity<byte[]> getProfilePicture() {
        try {
            User currentUser = userService.getAuthenticatedUser();
            if (currentUser == null || currentUser.getProfilePicture() == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(currentUser.getProfilePicture());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Get any user's profile picture (public access)
    @GetMapping("/{userId}/profile-picture")
    public ResponseEntity<byte[]> getUserProfilePicture(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null || user.getProfilePicture() == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(user.getProfilePicture());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
