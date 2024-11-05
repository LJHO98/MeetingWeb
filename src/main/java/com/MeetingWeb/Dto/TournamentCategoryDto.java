package com.MeetingWeb.Dto;

import com.MeetingWeb.Entity.TournamentCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TournamentCategoryDto {
    private Long tournamentCategoryId;
    private String category;


    // 엔티티 -> DTO 변환 메서드
    public static TournamentCategoryDto of(TournamentCategory category) {
        TournamentCategoryDto dto = new TournamentCategoryDto();
        dto.setTournamentCategoryId(category.getTournamentCategoryId());
        dto.setCategory(category.getCategory());
        return dto;
    }


}
