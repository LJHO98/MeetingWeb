package com.MeetingWeb.Dto;

import com.MeetingWeb.Entity.GroupCategory;
import com.MeetingWeb.Entity.TournamentCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupCategoryDto {
    private Long groupCategoryId;
    private String category;


    // 엔티티 -> DTO 변환 메서드
    public static GroupCategoryDto of(GroupCategory category) {
        GroupCategoryDto dto = new GroupCategoryDto();
        dto.setGroupCategoryId(category.getGroupCategoryId());
        dto.setCategory(category.getCategory());
        return dto;
    }

    public static GroupCategory toEntity(GroupCategoryDto dto) {
        GroupCategory entity = new GroupCategory();
        entity.setGroupCategoryId(dto.getGroupCategoryId());
        entity.setCategory(dto.getCategory());
        return entity;
    }

}
