package com.MeetingWeb.Repository;

import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.TournamentParticipant;
import com.MeetingWeb.Entity.Tournaments;
import io.lettuce.core.ScanIterator;
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

    TournamentParticipant findByGroupAndTournament(Groups group, Tournaments tournament);

    TournamentParticipant findByMatchNumber(int matchNumber);

    //한 대회 참가자 명단에 같은 groupId가 있더라도 한 팀만 조회
    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.tournament = :tournament GROUP BY tp.group.groupId")
    List<TournamentParticipant> findDistinctParticipantsByTournament(@Param("tournament") Tournaments tournament);


    TournamentParticipant findByGroupAndTournamentAndMatchNumber(Groups group, Tournaments tournament, int matchNumber);

    //참가자 명단에서 한 대회 당 하나의 참가자 정보 조회
    @Query("SELECT tp FROM TournamentParticipant tp WHERE tp.group = :group GROUP BY tp.tournament.id")
    List<TournamentParticipant> findDistinctParticipantsByGroup(@Param("group") Groups group);


    TournamentParticipant findByTournamentAndMatchNumber(Tournaments tournament, int loserNum);
}
