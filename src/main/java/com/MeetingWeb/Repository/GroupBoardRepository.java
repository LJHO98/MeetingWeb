package com.MeetingWeb.Repository;

import com.MeetingWeb.Entity.GroupBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupBoardRepository extends JpaRepository<GroupBoard, Long> {
    List<GroupBoard> findAllByGroup_GroupId(Long groupId);
}
