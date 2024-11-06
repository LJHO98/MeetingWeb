package com.MeetingWeb.Repository;

import com.MeetingWeb.Constant.Role;
import com.MeetingWeb.Entity.Tournaments;
import com.MeetingWeb.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TournamentRepository extends JpaRepository<Tournaments,Long> {
    List<Tournaments> findAllByOrderByCreatedAtDesc();
}