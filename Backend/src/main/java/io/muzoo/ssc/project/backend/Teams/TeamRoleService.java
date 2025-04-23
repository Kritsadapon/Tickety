package io.muzoo.ssc.project.backend.Teams;

import io.muzoo.ssc.project.backend.Team;
import io.muzoo.ssc.project.backend.TeamRepository;
import io.muzoo.ssc.project.backend.User;
import io.muzoo.ssc.project.backend.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TeamRoleService {

    @Autowired
    private TeamRoleRepository teamRoleRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    // Get currently authenticated user
    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username);
    }

    // Grant a role to a user in a team
    @Transactional
    public TeamRole grantRole(Long teamId, String username, RoleType roleType) {
        User currentUser = getAuthenticatedUser();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Only team owner can grant roles
        if (!team.getOwner().equals(currentUser)) {
            throw new RuntimeException("Only team owner can grant roles");
        }

        User userToGrant = userRepository.findByUsername(username);
        if (userToGrant == null) {
            throw new RuntimeException("User not found");
        }

        if (!team.getMembers().contains(userToGrant)) {
            throw new RuntimeException("User is not a member of this team");
        }

        // Check if user already has a role
        Optional<TeamRole> existingRole = teamRoleRepository.findByTeamAndUser(team, userToGrant);
        if (existingRole.isPresent()) {
            TeamRole role = existingRole.get();
            role.setRoleType(roleType);
            role.setGrantedBy(currentUser);
            return teamRoleRepository.save(role);
        }

        // Create new role
        TeamRole role = new TeamRole();
        role.setTeam(team);
        role.setUser(userToGrant);
        role.setRoleType(roleType);
        role.setGrantedBy(currentUser);

        return teamRoleRepository.save(role);
    }

    // Revoke a role from a user in a team
    @Transactional
    public void revokeRole(Long teamId, String username) {
        User currentUser = getAuthenticatedUser();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Only team owner can revoke roles
        if (!team.getOwner().equals(currentUser)) {
            throw new RuntimeException("Only team owner can revoke roles");
        }

        User userToRevoke = userRepository.findByUsername(username);
        if (userToRevoke == null) {
            throw new RuntimeException("User not found");
        }

        if (userToRevoke.equals(team.getOwner())) {
            throw new RuntimeException("Cannot revoke role from team owner");
        }

        teamRoleRepository.findByTeamAndUser(team, userToRevoke)
                .ifPresent(teamRoleRepository::delete);
    }

    // Get all roles in a team
    public List<TeamRole> getTeamRoles(Long teamId) {
        User currentUser = getAuthenticatedUser();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Only team members can view roles
        if (!team.getMembers().contains(currentUser)) {
            throw new RuntimeException("You don't have permission to view roles in this team");
        }

        return teamRoleRepository.findByTeam(team);
    }

    // Get a user's role in a team
    public Optional<TeamRole> getUserRole(Long teamId, String username) {
        User currentUser = getAuthenticatedUser();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Only team members can view roles
        if (!team.getMembers().contains(currentUser)) {
            throw new RuntimeException("You don't have permission to view roles in this team");
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return teamRoleRepository.findByTeamAndUser(team, user);
    }
} 