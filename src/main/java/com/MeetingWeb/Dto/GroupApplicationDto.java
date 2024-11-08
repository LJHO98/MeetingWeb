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

    public static GroupApplication toEntity(GroupApplicationDto dto) {//아직사용안함
        GroupApplication entity = new GroupApplication();
        entity.setReason(dto.getReason());
        entity.setSay(dto.getSay());
        return entity;
    }
}
