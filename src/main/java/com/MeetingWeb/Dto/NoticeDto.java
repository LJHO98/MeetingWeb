package com.MeetingWeb.Dto;

import com.MeetingWeb.Entity.Notice;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeDto {
    private Long id;
    private String content;

    public static NoticeDto of(Notice notice){
        NoticeDto noticeDto = new NoticeDto();
        noticeDto.setId(notice.getId());
        noticeDto.setContent(notice.getContent());
        return noticeDto;
    }
}
