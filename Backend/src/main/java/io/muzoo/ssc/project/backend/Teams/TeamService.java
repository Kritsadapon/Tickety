package io.muzoo.ssc.project.backend.Teams;

import io.muzoo.ssc.project.backend.Team;
import io.muzoo.ssc.project.backend.TeamRepository;
import io.muzoo.ssc.project.backend.User;
import io.muzoo.ssc.project.backend.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeamInvitationRepository teamInvitationRepository;

    // Get currently authenticated user
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    // Create a new team
    public Team createTeam(String teamName) {
        User currentUser = getAuthenticatedUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Team team = new Team();
        team.setName(teamName);
        team.setOwner(currentUser);
        team.getMembers().add(currentUser);
        return teamRepository.save(team);
    }

    // Invite a user to the team
    public TeamInvitation inviteUserToTeam(Long teamId, String username) {
        User currentUser = getAuthenticatedUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        if (!team.getOwner().equals(currentUser)) {
            throw new RuntimeException("Only team owner can invite members");
        }

        User userToInvite = userRepository.findByUsername(username);
        if (userToInvite == null) {
            throw new RuntimeException("User not found");
        }
        
        // Check if user is already a member
        if (team.getMembers().contains(userToInvite)) {
            throw new RuntimeException("User is already a member of this team");
        }
        
        // Check if there's already a pending invitation
        if (teamInvitationRepository.existsByTeamAndInvitedUserAndAcceptedFalseAndRejectedFalse(team, userToInvite)) {
            throw new RuntimeException("User already has a pending invitation to this team");
        }
        
        // Create a new invitation
        TeamInvitation invitation = new TeamInvitation();
        invitation.setTeam(team);
        invitation.setInvitedUser(userToInvite);
        invitation.setInviter(currentUser);
        invitation.setInvitationDate(LocalDateTime.now());
        
        return teamInvitationRepository.save(invitation);
    }
    
    // Accept a team invitation
    public Team acceptTeamInvitation(Long invitationId) {
        User currentUser = getAuthenticatedUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        TeamInvitation invitation = teamInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));
                
        if (!invitation.getInvitedUser().equals(currentUser)) {
            throw new RuntimeException("You can only accept invitations sent to you");
        }
        
        if (invitation.isAccepted() || invitation.isRejected()) {
            throw new RuntimeException("This invitation has already been responded to");
        }
        
        // Mark invitation as accepted
        invitation.setAccepted(true);
        invitation.setResponseDate(LocalDateTime.now());
        teamInvitationRepository.save(invitation);
        
        // Add user to team
        Team team = invitation.getTeam();
        team.getMembers().add(currentUser);
        return teamRepository.save(team);
    }
    
    // Reject a team invitation
    public TeamInvitation rejectTeamInvitation(Long invitationId) {
        User currentUser = getAuthenticatedUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        TeamInvitation invitation = teamInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));
                
        if (!invitation.getInvitedUser().equals(currentUser)) {
            throw new RuntimeException("You can only reject invitations sent to you");
        }
        
        if (invitation.isAccepted() || invitation.isRejected()) {
            throw new RuntimeException("This invitation has already been responded to");
        }
        
        // Mark invitation as rejected
        invitation.setRejected(true);
        invitation.setResponseDate(LocalDateTime.now());
        return teamInvitationRepository.save(invitation);
    }
    
    // Get pending invitations for the current user
    public List<TeamInvitation> getPendingInvitations() {
        User currentUser = getAuthenticatedUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        return teamInvitationRepository.findByInvitedUserAndAcceptedFalseAndRejectedFalse(currentUser);
    }

    // Remove a user from the team
    public Team removeUserFromTeam(Long teamId, String username) {
        User currentUser = getAuthenticatedUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        if (!team.getOwner().equals(currentUser)) {
            throw new RuntimeException("Only team owner can remove members");
        }

        User userToRemove = userRepository.findByUsername(username);
        if (userToRemove == null) {
            throw new RuntimeException("User not found");
        }

        if (userToRemove.equals(team.getOwner())) {
            throw new RuntimeException("Cannot remove team owner");
        }

        team.getMembers().remove(userToRemove);
        return teamRepository.save(team);
    }

    // Get all teams for the current user
    public List<Team> getUserTeams() {
        User currentUser = getAuthenticatedUser();
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }
        return teamRepository.findByMembers(currentUser);
    }

    // Get team by ID
    public Team getTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
    }
} 