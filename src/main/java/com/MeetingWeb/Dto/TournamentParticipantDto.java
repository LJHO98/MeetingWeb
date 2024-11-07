package com.MeetingWeb.Dto;

import com.MeetingWeb.Entity.Groups;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TournamentParticipantDto {
    private Long groupId;
    private String name;
    private String profileImgUrl;

    public static TournamentParticipantDto of(Groups group){
        TournamentParticipantDto dto = new TournamentParticipantDto();
        dto.setGroupId(group.getGroupId());
        dto.setName(group.getName());
        dto.setProfileImgUrl(group.getProfileImgUrl());

        return dto;
    }
}


