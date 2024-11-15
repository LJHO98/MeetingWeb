package com.MeetingWeb.Dto;

import com.MeetingWeb.Entity.GroupApplication;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupApplicationDto {
    private Long applicationId; //모임신청아이디

    private String reason; //가입이유
    private String say; //하고싶은말
    private UserDto user; //유저정보

    public static GroupApplication toEntity(GroupApplicationDto dto) {//아직사용안함
        GroupApplication entity = new GroupApplication();
        entity.setReason(dto.getReason());
        entity.setSay(dto.getSay());
        return entity;
    }
    public static GroupApplicationDto of(GroupApplication groupApplication) {
        GroupApplicationDto dto = new GroupApplicationDto();

        dto.setReason(groupApplication.getReason());
        dto.setSay(groupApplication.getSay());
        dto.user = UserDto.of(groupApplication.getUser()); // 유저 정보
        return dto;
    }
}
