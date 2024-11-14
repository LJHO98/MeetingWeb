package com.MeetingWeb.Dto;

import com.MeetingWeb.Constant.TournamentStatus;
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
    private String categoryName;
    private int capacity;

    @NotNull
    @FutureOrPresent(message = "접수시작일은 현재 이후 날짜(시간)여야 합니다.(제출 완료되는 시점이 현재입니다.")
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
    private TournamentStatus status;
    private int format;
    private List<String> imgList;
    private Long createdBy;
    private Long created;
    private String organizer;


    public Tournaments toEntity(String tournamentImgUrl, User createdBy, TournamentCategory tournamentCategory, Groups group) {
        Tournaments tournaments = new Tournaments();

        tournaments.setId(this.tournamentId);
        tournaments.setTournamentImgUrl(tournamentImgUrl);
        tournaments.setTitle(this.title);
        tournaments.setDescription(this.description);
        tournaments.setCategory(tournamentCategory);
        tournaments.setReceiptStart(this.receiptStart);
        tournaments.setReceiptEnd(this.receiptEnd);
        tournaments.setStartDate(this.startDate);
        tournaments.setEndDate(this.endDate);
        tournaments.setStatus(this.status);
        tournaments.setFormat(this.format);
        tournaments.setCapacity(this.format);
        tournaments.setCreatedBy(createdBy);
        tournaments.setGroup(group);
        return tournaments;
    }
    public static TrnDto of(Tournaments tournaments){
        TrnDto trnDto = new TrnDto();
        trnDto.tournamentId = tournaments.getId();
        trnDto.tournamentImgUrl = tournaments.getTournamentImgUrl();
        trnDto.title = tournaments.getTitle();
        trnDto.description = tournaments.getDescription();
        trnDto.category = tournaments.getCategory().getTournamentCategoryId();
        trnDto.categoryName = tournaments.getCategory().getCategory();
        trnDto.receiptStart = tournaments.getReceiptStart();
        trnDto.receiptEnd = tournaments.getReceiptEnd();
        trnDto.startDate = tournaments.getStartDate();
        trnDto.endDate = tournaments.getEndDate();
        trnDto.status = tournaments.getStatus();
        trnDto.format = tournaments.getFormat();
        trnDto.capacity = tournaments.getCapacity();
        trnDto.createdBy = tournaments.getCreatedBy().getId();
        trnDto.organizer = tournaments.getCreatedBy().getName();

        return trnDto;

    }

    @AssertTrue(message = "접수 마감일은 접수 시작일 이후여야 합니다.")
    public boolean isReceiptEndDateValid() {
        // 접수 종료일과 시작일이 null이 아니고, 종료일이 시작일 이후여야 유효
        return receiptEnd != null && receiptStart != null && receiptEnd.isAfter(receiptStart);
    }

    @AssertTrue(message = "대회 종료일은 대회 시작일 이후여야 합니다.")
    public boolean isEndDateValid() {
        // 대회 종료일과 시작일이 null이 아니고, 종료일이 시작일 이후여야 유효
        return endDate != null && startDate != null && endDate.isAfter(startDate);
    }

    @AssertTrue(message = "대회 시작일은 접수 마감일 이후여야 하며, 두 날짜 모두 필수 입력입니다.")
    public boolean isStartDateValid() {
        // 두 날짜가 모두 null이 아니어야 하고, startDate는 receiptStart 이후여야 유효
        return startDate != null && receiptEnd != null && startDate.isAfter(receiptEnd);
    }

}
