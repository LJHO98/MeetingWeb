package com.MeetingWeb.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TournamentResultDto {
    private Long tournamentId;
    private Long windId;
    private int scoreA;
    private int scoreB;
    private int matchNumber;

}
