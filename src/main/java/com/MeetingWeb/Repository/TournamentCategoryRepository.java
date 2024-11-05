package com.MeetingWeb.Repository;

import com.MeetingWeb.Entity.TournamentCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TournamentCategoryRepository extends JpaRepository<TournamentCategory, Long> {
    List<TournamentCategory> findAllByOrderByTournamentCategoryIdAsc();
}
