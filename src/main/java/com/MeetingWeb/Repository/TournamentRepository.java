package com.MeetingWeb.Repository;

import com.MeetingWeb.Constant.Role;
import com.MeetingWeb.Constant.TournamentStatus;
import com.MeetingWeb.Entity.Groups;
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

    //대회 검색 기능
    @Query("SELECT t FROM Tournaments t " +
            "WHERE (:categoryId IS NULL OR t.category.tournamentCategoryId = :categoryId) " +
            "AND (:inputText IS NULL OR t.title LIKE %:inputText% OR t.description LIKE %:inputText%)")
    List<Tournaments> searchByCategoryAndText(@Param("categoryId") Long categoryId,
                                              @Param("inputText") String inputText);

    //유저가 가입한 모임이 참여하는 대회 조회
    @Query("select t from Tournaments t, TournamentParticipant tp ,Groups g,GroupMember gm where gm.user= :user and g.groupId=gm.group.groupId and tp.group.groupId=g.groupId and t.id=tp.tournament.id GROUP BY tp.tournament.id")
    List<Tournaments> findTournamentsByUser(@Param("user") User user);

    //모임이 참가하는 대회 정보 조회
    @Query("SELECT DISTINCT tp.tournament FROM TournamentParticipant tp WHERE tp.group = :group")
    List<Tournaments> findDistinctTournamentsByGroup(@Param("group") Groups group);

    // 전체 회원 수 카운트
    @Query("SELECT COUNT(t) FROM Tournaments t")
    long countTotalTournaments();
}

