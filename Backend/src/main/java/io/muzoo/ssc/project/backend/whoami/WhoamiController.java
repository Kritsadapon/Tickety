package io.muzoo.ssc.project.backend.whoami;

import io.muzoo.ssc.project.backend.User;
import io.muzoo.ssc.project.backend.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * A controller to retrieve currently logged in user
 */
@RestController
public class WhoamiController {

    @Autowired
    private UserRepository userRepository;



    /*
     * Make suer that API path begin with /api. This will be useful when we do proxy
     */
    @GetMapping("/api/whoami")
    public WhoamiDTO whoami() {
        // Put try around the statement because we use nested dot notation which could raise a NullPointerEception.
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(principal instanceof org.springframework.security.core.userdetails.User) {
                // user is logged in
                org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) principal;
                User u = userRepository.findByUsername(user.getUsername());
                return WhoamiDTO.builder()
                        .loggedIn(true)
                        .name(u.getDisplayName())
                        .role(u.getRole())
                        .username(u.getUsername())
                        .profilePicture(u.getProfilePicture())
                        .id(u.getId())
                        .build();
            }
        } catch (Exception ignored) {
        }
        // user is not logged in
        return WhoamiDTO.builder()
                .loggedIn(false)
                .build();
    }
}
