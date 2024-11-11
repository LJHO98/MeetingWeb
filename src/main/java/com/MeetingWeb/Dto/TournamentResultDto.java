package com.MeetingWeb.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TournamentResultDto {
    private Long tournamentId;
    private Long GroupId;
    private int firstScore;
    private int secondScore;

}
