package io.muzoo.ssc.project.backend.Teams;

import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/team-roles")
public class TeamRoleController {

    @Autowired
    private TeamRoleService teamRoleService;

    // Grant a role to a user in a team
    @PostMapping("/grant")
    public ResponseEntity<?> grantRole(
            @RequestParam Long teamId,
            @RequestParam String username,
            @RequestParam RoleType roleType) {
        try {
            TeamRole role = teamRoleService.grantRole(teamId, username, roleType);
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Revoke a role from a user in a team
    @PostMapping("/revoke")
    public ResponseEntity<?> revokeRole(
            @RequestParam Long teamId,
            @RequestParam String username) {
        try {
            teamRoleService.revokeRole(teamId, username);
            return ResponseEntity.ok(SimpleResponseDTO.builder()
                    .success(true)
                    .message("Role revoked successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Get all roles in a team
    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> getTeamRoles(@PathVariable Long teamId) {
        try {
            return ResponseEntity.ok(teamRoleService.getTeamRoles(teamId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Get a user's role in a team
    @GetMapping("/team/{teamId}/user/{username}")
    public ResponseEntity<?> getUserRole(
            @PathVariable Long teamId,
            @PathVariable String username) {
        try {
            return ResponseEntity.ok(teamRoleService.getUserRole(teamId, username));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }
} 