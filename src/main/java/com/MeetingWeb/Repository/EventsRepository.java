package com.MeetingWeb.Repository;

import com.MeetingWeb.Entity.Events;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventsRepository extends JpaRepository<Events, Long> {

    List<Events> findByGroup_GroupId(Long groupId);
}
