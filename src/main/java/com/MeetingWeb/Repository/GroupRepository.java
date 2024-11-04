package com.MeetingWeb.Repository;

import com.MeetingWeb.Entity.Groups;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Groups, Long> {
    Groups findByGroupId(Long id);
}
