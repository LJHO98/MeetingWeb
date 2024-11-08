package com.MeetingWeb.Repository;

import com.MeetingWeb.Entity.TournamentParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TournamentParticipantRepository extends JpaRepository<TournamentParticipant, Long> {
    List<TournamentParticipant> findByTournamentId(Long tournamentId);
    TournamentParticipant findByParticipantId(Long participantId);
}
