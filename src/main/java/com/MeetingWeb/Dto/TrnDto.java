package com.MeetingWeb.Dto;

import com.MeetingWeb.Entity.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
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
    private Long category;

    @NotNull
    @FutureOrPresent(message = "접수일은 현재 이후 날짜여야 합니다.(제출 완료되는 시점이 현재입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime receiptStart;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime receiptEnd;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;

    private String status;
    private int format;
    private List<String> imgList;
    private Long createdBy;


    public Tournaments toEntity(String tournamentImgUrl, User createdBy, TournamentCategory tournamentCategory) {
        Tournaments tournaments = new Tournaments();

        tournaments.setId(this.tournamentId);
        tournaments.setTournamentImgUrl(tournamentImgUrl);
        tournaments.setTitle(this.title);
        tournaments.setDescription(this.description);
        tournaments.setCategory(tournamentCategory);
        tournaments.setReceiptStart(receiptStart);
        tournaments.setReceiptEnd(receiptEnd);
        tournaments.setStartDate(startDate);
        tournaments.setEndDate(endDate);
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
        trnDto.category = tournaments.getCategory().getTournamentCategoryId();
        trnDto.receiptStart = tournaments.getReceiptStart();
        trnDto.receiptEnd = tournaments.getReceiptEnd();
        trnDto.startDate = tournaments.getStartDate();
        trnDto.endDate = tournaments.getEndDate();
        trnDto.status = tournaments.getStatus();
        trnDto.format = tournaments.getFormat();

        return trnDto;

    }

    @AssertTrue(message = "접수 마감일은 접수 시작일 이후여야 합니다.")
    public boolean isReceiptEndDateValid() {
        return receiptEnd == null || receiptStart == null || !receiptEnd.isBefore(receiptStart);
    }

    @AssertTrue(message = "대회 종료일은 대회 시작일 이후여야 합니다.")
    public boolean isEndDateValid() {
        return endDate == null || startDate == null || !endDate.isBefore(startDate);
    }
}
