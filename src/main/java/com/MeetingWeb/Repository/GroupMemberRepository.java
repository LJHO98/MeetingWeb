package com.MeetingWeb.Repository;

import com.MeetingWeb.Entity.GroupMember;
import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    boolean existsByGroupAndUser(Groups group, User user); // 특정 모임과 사용자로 이미 가입되어 있는지 확인

}
