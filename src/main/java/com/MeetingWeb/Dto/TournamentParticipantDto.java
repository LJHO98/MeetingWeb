package com.MeetingWeb.Dto;

import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.TournamentParticipant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TournamentParticipantDto {
    private Long groupId;
    private String name;
    private String profileImgUrl;
    private int matchNumber;
    private int score;

    public static TournamentParticipantDto of(Groups group, TournamentParticipant tournamentParticipant) {
        TournamentParticipantDto dto = new TournamentParticipantDto();
        dto.setGroupId(group.getGroupId());
        dto.setName(group.getName());
        dto.setProfileImgUrl(group.getProfileImgUrl());
        dto.setMatchNumber(tournamentParticipant.getMatchNumber());
        dto.setScore(tournamentParticipant.getScore());
        return dto;
    }
}


