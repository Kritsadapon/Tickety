package io.muzoo.ssc.project.backend.Tickets;

import io.muzoo.ssc.project.backend.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketFlowRepository extends JpaRepository<TicketFlow, Long> {
    List<TicketFlow> findByTeam(Team team);
} 