package io.muzoo.ssc.project.backend.Teams;

import io.muzoo.ssc.project.backend.Team;
import io.muzoo.ssc.project.backend.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TeamInvitationDTO {
    private Long id;
    private Long teamId;
    private String teamName;
    private Long invitedUserId;
    private String invitedUsername;
    private String invitedUserDisplayName;
    private Long inviterId;
    private String inviterUsername;
    private String inviterDisplayName;
    private LocalDateTime invitationDate;
    private boolean accepted;
    private boolean rejected;
    private LocalDateTime responseDate;
    
    public static TeamInvitationDTO fromEntity(TeamInvitation invitation) {
        Team team = invitation.getTeam();
        User invitedUser = invitation.getInvitedUser();
        User inviter = invitation.getInviter();
        
        return TeamInvitationDTO.builder()
                .id(invitation.getId())
                .teamId(team.getId())
                .teamName(team.getName())
                .invitedUserId(invitedUser.getId())
                .invitedUsername(invitedUser.getUsername())
                .invitedUserDisplayName(invitedUser.getDisplayName())
                .inviterId(inviter.getId())
                .inviterUsername(inviter.getUsername())
                .inviterDisplayName(inviter.getDisplayName())
                .invitationDate(invitation.getInvitationDate())
                .accepted(invitation.isAccepted())
                .rejected(invitation.isRejected())
                .responseDate(invitation.getResponseDate())
                .build();
    }
} 