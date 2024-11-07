package com.MeetingWeb.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class TournamentParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournaments tournament;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Groups group;

    @Column(updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    public void prePersist() {
        this.joinedAt = LocalDateTime.now(); // 처음 저장할 때만 현재 시간을 설정
    }
}
