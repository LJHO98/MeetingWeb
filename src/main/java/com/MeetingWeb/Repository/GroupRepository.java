package com.MeetingWeb.Repository;

import com.MeetingWeb.Entity.GroupCategory;
import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.Tournaments;
import com.MeetingWeb.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Groups, Long> {
    Groups findByGroupId(Long id);
    List<Groups> findAllByOrderByCreatedAtDesc();
    List<Groups> findByCategoryIn(List<GroupCategory> categories);

    //만드사람 userId로 모임 찾기
    Optional<Groups> findByCreatedById(Long id);


    //만든사람 객체로 모임 찾기
    List<Groups> findByCreatedBy(User user);

    @Query("SELECT gm.group FROM GroupMember gm WHERE gm.user = :user")
    List<Groups> findMyGroups(@Param("user") User user);

    // 전체 회원 수 카운트
    @Query("SELECT COUNT(g) FROM Groups g")
    long countTotalGroups();
}
