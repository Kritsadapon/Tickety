package io.muzoo.ssc.project.backend.Teams;

import io.muzoo.ssc.project.backend.Team;
import io.muzoo.ssc.project.backend.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long> {
    List<TeamInvitation> findByInvitedUserAndAcceptedFalseAndRejectedFalse(User user);
    List<TeamInvitation> findByTeamAndInvitedUser(Team team, User user);
    boolean existsByTeamAndInvitedUserAndAcceptedFalseAndRejectedFalse(Team team, User user);
} 