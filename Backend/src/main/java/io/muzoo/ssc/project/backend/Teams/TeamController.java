package io.muzoo.ssc.project.backend.Teams;

import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import io.muzoo.ssc.project.backend.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    // Create a new team
    @PostMapping("/create")
    public ResponseEntity<?> createTeam(@RequestParam String teamName) {
        try {
            Team team = teamService.createTeam(teamName);
            return ResponseEntity.ok(team);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Invite a user to the team
    @PostMapping("/{teamId}/invite")
    public ResponseEntity<?> inviteUser(@PathVariable Long teamId, @RequestParam String username) {
        try {
            TeamInvitation invitation = teamService.inviteUserToTeam(teamId, username);
            return ResponseEntity.ok(TeamInvitationDTO.fromEntity(invitation));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }
    
    // Accept a team invitation
    @PostMapping("/invitations/{invitationId}/accept")
    public ResponseEntity<?> acceptInvitation(@PathVariable Long invitationId) {
        try {
            Team team = teamService.acceptTeamInvitation(invitationId);
            return ResponseEntity.ok(team);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }
    
    // Reject a team invitation
    @PostMapping("/invitations/{invitationId}/reject")
    public ResponseEntity<?> rejectInvitation(@PathVariable Long invitationId) {
        try {
            TeamInvitation invitation = teamService.rejectTeamInvitation(invitationId);
            return ResponseEntity.ok(TeamInvitationDTO.fromEntity(invitation));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }
    
    // Get pending invitations for the current user
    @GetMapping("/invitations/pending")
    public ResponseEntity<?> getPendingInvitations() {
        try {
            List<TeamInvitation> invitations = teamService.getPendingInvitations();
            List<TeamInvitationDTO> invitationDTOs = invitations.stream()
                    .map(TeamInvitationDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(invitationDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Remove a user from the team
    @PostMapping("/{teamId}/remove")
    public ResponseEntity<?> removeUser(@PathVariable Long teamId, @RequestParam String username) {
        try {
            Team team = teamService.removeUserFromTeam(teamId, username);
            return ResponseEntity.ok(team);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Get all teams for the current user
    @GetMapping("/my-teams")
    public ResponseEntity<?> getMyTeams() {
        try {
            List<Team> teams = teamService.getUserTeams();
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    // Get team by ID
    @GetMapping("/{teamId}")
    public ResponseEntity<?> getTeam(@PathVariable Long teamId) {
        try {
            Team team = teamService.getTeamById(teamId);
            return ResponseEntity.ok(team);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }
} 