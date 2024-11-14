package com.MeetingWeb.Repository;

import com.MeetingWeb.Entity.GroupMember;
import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    boolean existsByGroupAndUser(Groups group, User user); // 특정 모임과 사용자로 이미 가입되어 있는지 확인
    List<GroupMember> findByGroup(Groups group);
    List<GroupMember> findByUser(User user);

    //그룹에있는 회원조회
    Optional<GroupMember> findByGroup_GroupIdAndUser_Id(Long groupId, Long userId);


    boolean existsByUserAndGroup(User user, Groups group);

    //멤버수 카운트
    @Query("SELECT COUNT(gm) FROM GroupMember gm WHERE gm.group = :group")
    int countByGroup(@Param("group") Groups group);

}
