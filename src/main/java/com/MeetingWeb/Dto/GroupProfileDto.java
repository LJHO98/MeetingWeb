package com.MeetingWeb.Dto;

import com.MeetingWeb.Entity.Groups;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupProfileDto {
    private String name;
    private String profileImgUrl;

    public static GroupProfileDto of(Groups group) {
        GroupProfileDto dto = new GroupProfileDto();
        dto.setName(group.getName());
        dto.setProfileImgUrl(group.getProfileImgUrl());
        return dto;
    }
}
