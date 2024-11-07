package com.MeetingWeb.Repository;

import com.MeetingWeb.Constant.Role;
import com.MeetingWeb.Constant.TournamentStatus;
import com.MeetingWeb.Entity.Tournaments;
import com.MeetingWeb.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournaments,Long> {
    List<Tournaments> findAllByOrderByCreatedAtDesc();

    Optional<Tournaments> findById(Long id);
}