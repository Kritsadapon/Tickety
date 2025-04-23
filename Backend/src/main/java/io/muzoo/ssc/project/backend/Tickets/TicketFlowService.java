package io.muzoo.ssc.project.backend.Tickets;

import io.muzoo.ssc.project.backend.Team;
import io.muzoo.ssc.project.backend.TeamRepository;
import io.muzoo.ssc.project.backend.Teams.RoleType;
import io.muzoo.ssc.project.backend.Teams.TeamRoleRepository;
import io.muzoo.ssc.project.backend.User;
import io.muzoo.ssc.project.backend.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketFlowService {

    @Autowired
    private TicketFlowRepository ticketFlowRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRoleRepository teamRoleRepository;

    // Get currently authenticated user
    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username);
    }

    // Check if user has required role in team
    private boolean hasRole(User user, Team team, RoleType requiredRole) {
        return teamRoleRepository.findByTeamAndUser(team, user)
                .map(role -> role.getRoleType().ordinal() <= requiredRole.ordinal())
                .orElse(false);
    }

    // Create a new ticket flow
    @Transactional
    public TicketFlow createFlow(Long teamId, String name, String description) {
        User currentUser = getAuthenticatedUser();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        if (!hasRole(currentUser, team, RoleType.MANAGER)) {
            throw new RuntimeException("You don't have permission to create flows in this team");
        }

        TicketFlow flow = new TicketFlow();
        flow.setName(name);
        flow.setDescription(description);
        flow.setTeam(team);
        flow.setCreator(currentUser);

        return ticketFlowRepository.save(flow);
    }

    // Update a ticket flow
    @Transactional
    public TicketFlow updateFlow(Long flowId, String name, String description) {
        User currentUser = getAuthenticatedUser();
        TicketFlow flow = ticketFlowRepository.findById(flowId)
                .orElseThrow(() -> new RuntimeException("Flow not found"));

        if (!hasRole(currentUser, flow.getTeam(), RoleType.MANAGER)) {
            throw new RuntimeException("You don't have permission to update flows in this team");
        }

        flow.setName(name);
        flow.setDescription(description);

        return ticketFlowRepository.save(flow);
    }

    // Delete a ticket flow
    @Transactional
    public void deleteFlow(Long flowId) {
        User currentUser = getAuthenticatedUser();
        TicketFlow flow = ticketFlowRepository.findById(flowId)
                .orElseThrow(() -> new RuntimeException("Flow not found"));

        if (!hasRole(currentUser, flow.getTeam(), RoleType.MANAGER)) {
            throw new RuntimeException("You don't have permission to delete flows in this team");
        }

        ticketFlowRepository.delete(flow);
    }

    // Get all flows for a team
    public List<TicketFlow> getTeamFlows(Long teamId) {
        User currentUser = getAuthenticatedUser();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        if (!hasRole(currentUser, team, RoleType.MEMBER)) {
            throw new RuntimeException("You don't have permission to view flows in this team");
        }

        return ticketFlowRepository.findByTeam(team);
    }

    // Get a specific flow
    public TicketFlow getFlow(Long flowId) {
        User currentUser = getAuthenticatedUser();
        TicketFlow flow = ticketFlowRepository.findById(flowId)
                .orElseThrow(() -> new RuntimeException("Flow not found"));

        if (!hasRole(currentUser, flow.getTeam(), RoleType.MEMBER)) {
            throw new RuntimeException("You don't have permission to view this flow");
        }

        return flow;
    }
} 