package com.MeetingWeb.Repository;

import com.MeetingWeb.Constant.Role;
import com.MeetingWeb.Constant.TournamentStatus;
import com.MeetingWeb.Entity.Tournaments;
import com.MeetingWeb.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournaments, Long> {

    List<Tournaments> findAllByOrderByCreatedAtDesc();

    Optional<Tournaments> findById(Long id);

    List<Tournaments> findByCreatedBy(User user);

    @Query("SELECT e FROM Tournaments e WHERE e.id NOT IN :excludedIds")
    List<Tournaments> findAllExcludingIds(@Param("excludedIds") List<Long> excludedIds);

    @Query("SELECT t FROM Tournaments t " +
            "WHERE (:categoryId IS NULL OR t.category.tournamentCategoryId = :categoryId) " +
            "AND (:inputText IS NULL OR t.title LIKE %:inputText% OR t.description LIKE %:inputText%)")
    List<Tournaments> searchByCategoryAndText(@Param("categoryId") Long categoryId,
                                              @Param("inputText") String inputText);
}

