package com.MeetingWeb.Dto;

import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.TournamentCategory;
import com.MeetingWeb.Entity.Tournaments;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TournamentManageDto {
    private Long Id;
    private String title;
    private String groupName;
    private String category;
    private LocalDateTime createdAt;

    public static TournamentManageDto of(Tournaments tournaments){
        TournamentManageDto dto = new TournamentManageDto();
        dto.setId(tournaments.getId());
        dto.setTitle(tournaments.getTitle());
        dto.setGroupName(tournaments.getGroup().getName());
        dto.setCategory(tournaments.getCategory().getCategory());
        dto.setCreatedAt(tournaments.getCreatedAt());

        return dto;
    }
}
