package com.MeetingWeb.Dto;

import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.Tournaments;
import com.MeetingWeb.Entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TrnDto {
    private Long tournamentId;
    private MultipartFile tournamentImg;
    private String tournamentImgUrl;
    private String title;
    private String description;
    private String category;
    private String receipStart;
    private String receipEnd;
    private String startDate;
    private String endDate;
    private String status;
    private String format;
    private List<String> imgList;
    private Long createdBy;


    public Tournaments toEntity(String tournamentImgUrl, User createdBy){
        Tournaments tournaments = new Tournaments();
        tournaments.setId(tournamentId);
        tournaments.setTournamentImgUrl(tournamentImgUrl);
        tournaments.setTitle(title);
        tournaments.setDescription(description);
        tournaments.setCategory(category);
        tournaments.setReceipStart(LocalDateTime.parse(receipStart));
        tournaments.setReceipEnd(LocalDateTime.parse(receipEnd));
        tournaments.setStartDate(LocalDateTime.parse(startDate));
        tournaments.setEndDate(LocalDateTime.parse(endDate));
        tournaments.setStatus(status);
        tournaments.setFormat(format);
        tournaments.setCreatedBy(createdBy);
        return tournaments;
    }
    public static TrnDto of(Tournaments tournaments){
        TrnDto trnDto = new TrnDto();
        trnDto.tournamentId = tournaments.getId();
        trnDto.tournamentImgUrl = tournaments.getTournamentImgUrl();
        trnDto.title = tournaments.getTitle();
        trnDto.description = tournaments.getDescription();
        trnDto.category = tournaments.getCategory();
        trnDto.receipStart = tournaments.getReceipStart().toString();
        trnDto.receipEnd = tournaments.getReceipEnd().toString();
        trnDto.startDate = tournaments.getStartDate().toString();
        trnDto.endDate = tournaments.getEndDate().toString();
        trnDto.status = tournaments.getStatus();
        trnDto.format = tournaments.getFormat();

        return trnDto;

    }

}
