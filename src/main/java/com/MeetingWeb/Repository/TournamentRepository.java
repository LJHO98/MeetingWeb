package com.MeetingWeb.Repository;

import com.MeetingWeb.Constant.Role;
import com.MeetingWeb.Entity.Tournaments;
import com.MeetingWeb.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<Tournaments,Long> {



}