package io.muzoo.ssc.project.backend.Tickets;

import io.muzoo.ssc.project.backend.Team;
import io.muzoo.ssc.project.backend.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByTeam(Team team);
    List<Ticket> findByAssignee(User assignee);
    List<Ticket> findByCreator(User creator);
    List<Ticket> findByTeamAndStatus(Team team, TicketStatus status);
    List<Ticket> findByFlow(TicketFlow flow);
    List<Ticket> findByDependenciesContaining(Ticket ticket);
} 