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

    Optional<Groups> findByCreatedById(Long id);
    //모임카테고리에 있는 카테고리명 모임리스트에서 전부 가져오기
    List<Groups> findByCreatedBy(User user);

    @Query("SELECT gm.group FROM GroupMember gm WHERE gm.user = :user")
    List<Groups> findMyGroups(@Param("user") User user);
}
