package com.MeetingWeb.Dto;

import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.TournamentParticipant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TournamentParticipantDto {
    private Long groupId;
    private String name;
    private String profileImgUrl;
    private int matchNumber;

    public static TournamentParticipantDto of(Groups group, int matchNumber) {
        TournamentParticipantDto dto = new TournamentParticipantDto();
        dto.setGroupId(group.getGroupId());
        dto.setName(group.getName());
        dto.setProfileImgUrl(group.getProfileImgUrl());
        dto.setMatchNumber(matchNumber);

        return dto;
    }
}


