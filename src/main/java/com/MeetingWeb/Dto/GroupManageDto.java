package com.MeetingWeb.Dto;

import com.MeetingWeb.Entity.Groups;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class GroupManageDto {
    private Long groupId;
    private String groupName;
    private String createdBy;
    private LocalDateTime createdAt;

    public static GroupManageDto of(Groups group){
        GroupManageDto dto = new GroupManageDto();
        dto.setCreatedBy(group.getCreatedBy().getName());
        dto.setGroupId(group.getGroupId());
        dto.setGroupName(group.getName());
        dto.setCreatedAt(group.getCreatedAt());

        return dto;
    }
}
