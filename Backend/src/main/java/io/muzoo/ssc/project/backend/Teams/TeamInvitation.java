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
@Table(name = "tbl_team_invitation")
public class TeamInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "invited_user_id", nullable = false)
    private User invitedUser;

    @ManyToOne
    @JoinColumn(name = "inviter_id", nullable = false)
    private User inviter;

    @Column(nullable = false)
    private LocalDateTime invitationDate;

    @Column(nullable = false)
    private boolean accepted = false;

    @Column(nullable = false)
    private boolean rejected = false;

    @Column
    private LocalDateTime responseDate;
} 