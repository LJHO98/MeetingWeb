package com.MeetingWeb.Repository;

import com.MeetingWeb.Entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findAllByGroup_GroupId(Long groupId);
}
