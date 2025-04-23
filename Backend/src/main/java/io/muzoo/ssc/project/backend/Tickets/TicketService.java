package io.muzoo.ssc.project.backend.Tickets;

import io.muzoo.ssc.project.backend.Team;
import io.muzoo.ssc.project.backend.TeamRepository;
import io.muzoo.ssc.project.backend.Teams.TeamRole;
import io.muzoo.ssc.project.backend.Teams.TeamRoleRepository;
import io.muzoo.ssc.project.backend.Teams.RoleType;
import io.muzoo.ssc.project.backend.User;
import io.muzoo.ssc.project.backend.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketFlowRepository ticketFlowRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRoleRepository teamRoleRepository;

    @Autowired
    private TeamRepository teamRepository;

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

    // Create a new ticket
    @Transactional
    public Ticket createTicket(Long teamId, String title, String description, Long flowId, Set<Long> dependencyIds) {
        User currentUser = getAuthenticatedUser();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        if (!hasRole(currentUser, team, RoleType.MEMBER)) {
            throw new RuntimeException("You don't have permission to create tickets in this team");
        }

        Ticket ticket = new Ticket();
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setTeam(team);
        ticket.setCreator(currentUser);

        if (flowId != null) {
            TicketFlow flow = ticketFlowRepository.findById(flowId)
                    .orElseThrow(() -> new RuntimeException("Flow not found"));
            ticket.setFlow(flow);
            ticket.setSequenceInFlow(flow.getTickets().size() + 1);
        }

        if (dependencyIds != null && !dependencyIds.isEmpty()) {
            Set<Ticket> dependencies = dependencyIds.stream()
                    .map(id -> ticketRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Dependency ticket not found: " + id)))
                    .collect(Collectors.toSet());
            ticket.setDependencies(dependencies);
        }

        return ticketRepository.save(ticket);
    }

    // Assign a ticket to a user
    @Transactional
    public Ticket assignTicket(Long ticketId, String username) {
        User currentUser = getAuthenticatedUser();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (!hasRole(currentUser, ticket.getTeam(), RoleType.MEMBER)) {
            throw new RuntimeException("You don't have permission to assign tickets in this team");
        }

        User assignee = userRepository.findByUsername(username);
        if (assignee == null) {
            throw new RuntimeException("User not found");
        }

        if (!ticket.getTeam().getMembers().contains(assignee)) {
            throw new RuntimeException("User is not a member of this team");
        }

        ticket.setAssignee(assignee);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        return ticketRepository.save(ticket);
    }

    // Update ticket status
    @Transactional
    public Ticket updateTicketStatus(Long ticketId, TicketStatus newStatus) {
        User currentUser = getAuthenticatedUser();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (!hasRole(currentUser, ticket.getTeam(), RoleType.MEMBER)) {
            throw new RuntimeException("You don't have permission to update tickets in this team");
        }

        if (ticket.getAssignee() != null && !ticket.getAssignee().equals(currentUser)) {
            throw new RuntimeException("Only the assignee can update the ticket status");
        }

        // Check if all dependencies are completed
        if (newStatus == TicketStatus.IN_PROGRESS) {
            boolean allDependenciesDone = ticket.getDependencies().stream()
                    .allMatch(dep -> dep.getStatus() == TicketStatus.DONE);
            if (!allDependenciesDone) {
                throw new RuntimeException("Cannot start ticket: dependencies are not completed");
            }
        }

        ticket.setStatus(newStatus);
        return ticketRepository.save(ticket);
    }

    // Get available tickets for a team (tickets with completed dependencies)
    public List<Ticket> getAvailableTickets(Long teamId) {
        User currentUser = getAuthenticatedUser();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        if (!hasRole(currentUser, team, RoleType.MEMBER)) {
            throw new RuntimeException("You don't have permission to view tickets in this team");
        }

        return ticketRepository.findByTeam(team).stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.TODO)
                .filter(ticket -> ticket.getDependencies().stream()
                        .allMatch(dep -> dep.getStatus() == TicketStatus.DONE))
                .collect(Collectors.toList());
    }

    // Get tickets in a flow
    public List<Ticket> getTicketsInFlow(Long flowId) {
        User currentUser = getAuthenticatedUser();
        TicketFlow flow = ticketFlowRepository.findById(flowId)
                .orElseThrow(() -> new RuntimeException("Flow not found"));

        if (!hasRole(currentUser, flow.getTeam(), RoleType.MEMBER)) {
            throw new RuntimeException("You don't have permission to view tickets in this flow");
        }

        return ticketRepository.findByFlow(flow);
    }

    // Add dependency to a ticket
    @Transactional
    public Ticket addDependency(Long ticketId, Long dependencyId) {
        User currentUser = getAuthenticatedUser();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (!hasRole(currentUser, ticket.getTeam(), RoleType.MANAGER)) {
            throw new RuntimeException("You don't have permission to modify ticket dependencies");
        }

        Ticket dependency = ticketRepository.findById(dependencyId)
                .orElseThrow(() -> new RuntimeException("Dependency ticket not found"));

        if (!dependency.getTeam().equals(ticket.getTeam())) {
            throw new RuntimeException("Cannot add dependency from a different team");
        }

        ticket.getDependencies().add(dependency);
        return ticketRepository.save(ticket);
    }

    // Remove dependency from a ticket
    @Transactional
    public Ticket removeDependency(Long ticketId, Long dependencyId) {
        User currentUser = getAuthenticatedUser();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (!hasRole(currentUser, ticket.getTeam(), RoleType.MANAGER)) {
            throw new RuntimeException("You don't have permission to modify ticket dependencies");
        }

        Ticket dependency = ticketRepository.findById(dependencyId)
                .orElseThrow(() -> new RuntimeException("Dependency ticket not found"));

        ticket.getDependencies().remove(dependency);
        return ticketRepository.save(ticket);
    }
} 