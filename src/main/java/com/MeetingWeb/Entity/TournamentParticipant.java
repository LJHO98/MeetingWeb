package com.MeetingWeb.Entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Tournaments tournament;

    @ManyToOne
    @JoinColumn(name = "group_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Groups group;

    private int matchNumber;

    private int score=0;
    private String matchResult;
    private int bracketNumber; // 브라켓 번호
    private boolean isCompleted = false; // 경기 완료 여부 추가

    @Column(updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    public void prePersist() {
        this.joinedAt = LocalDateTime.now(); // 처음 저장할 때만 현재 시간을 설정
    }
}
