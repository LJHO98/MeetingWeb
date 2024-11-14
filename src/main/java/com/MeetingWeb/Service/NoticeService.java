package com.MeetingWeb.Service;

import com.MeetingWeb.Dto.NoticeDto;
import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.Notice;
import com.MeetingWeb.Repository.GroupRepository;
import com.MeetingWeb.Repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final GroupRepository groupRepository;

    // 공지사항 저장
    public void saveNotice(String content, Long groupId) {
        Notice notice = new Notice();
        Groups gruop = groupRepository.findByGroupId(groupId);
        notice.setGroup(gruop);
        notice.setContent(content);
        noticeRepository.save(notice);
    }

    // 공지사항 삭제
    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }
    // 공지사항 목록
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    public List<NoticeDto> getGroupNotices(Long groupId) {
        List<Notice> groupNotices = noticeRepository.findAllByGroup_GroupId(groupId);
        return groupNotices.stream().map(NoticeDto::of).collect(Collectors.toList());
    }
}
