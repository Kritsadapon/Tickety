package io.muzoo.ssc.project.backend.Teams;

import io.muzoo.ssc.project.backend.Team;
import io.muzoo.ssc.project.backend.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRoleRepository extends JpaRepository<TeamRole, Long> {
    List<TeamRole> findByTeam(Team team);
    List<TeamRole> findByUser(User user);
    Optional<TeamRole> findByTeamAndUser(Team team, User user);
    boolean existsByTeamAndUserAndRoleType(Team team, User user, RoleType roleType);
} 