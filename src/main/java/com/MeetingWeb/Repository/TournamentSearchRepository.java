package com.MeetingWeb.Repository;

import com.MeetingWeb.Entity.Tournaments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentSearchRepository extends JpaRepository<Tournaments,Long> {

}
