package io.muzoo.ssc.project.backend.Teams;

import io.muzoo.ssc.project.backend.Team;
import io.muzoo.ssc.project.backend.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "tbl_team_role")
public class TeamRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @ManyToOne
    @JoinColumn(name = "granted_by_id", nullable = false)
    private User grantedBy;

    @Column(nullable = false)
    private LocalDateTime grantedAt;

    @PrePersist
    protected void onCreate() {
        grantedAt = LocalDateTime.now();
    }
} 