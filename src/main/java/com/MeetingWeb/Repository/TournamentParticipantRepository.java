package com.MeetingWeb.Repository;

import com.MeetingWeb.Entity.TournamentParticipant;
import com.MeetingWeb.Entity.Tournaments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TournamentParticipantRepository extends JpaRepository<TournamentParticipant, Long> {
    List<TournamentParticipant> findByTournamentId(Long tournamentId);
    TournamentParticipant findByGroup_GroupId(Long participantId);

    @Query(value="select count(tournament.matchNumber) from TournamentParticipant tournament where tournament.tournament= :tournament")
    int getCount(@Param("tournament") Tournaments tournament);

    List<TournamentParticipant> findByTournamentIdOrderByMatchNumberAsc(Long tournamentId);
}
