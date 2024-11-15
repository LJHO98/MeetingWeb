package com.MeetingWeb.Repository;

import com.MeetingWeb.Entity.GroupApplication;
import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupApplicationRepository extends JpaRepository<GroupApplication, Long> {
    boolean existsByGroupAndUser(Groups group, User user); // 특정 모임과 사용자로 이미 가입
    List<GroupApplication> findByGroup_GroupId(Long groupId);//해당그룹의 신청목록

    GroupApplication findByUser_Id(Long userId);
}
