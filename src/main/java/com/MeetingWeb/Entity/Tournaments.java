package com.MeetingWeb.Entity;

import com.MeetingWeb.Constant.TournamentStatus;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Tournaments{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tournament_id")
    private Long id;

    private String tournamentImgUrl;
    private String title;
    @Lob
    private String description;

    @ManyToOne(fetch = FetchType.LAZY) // 카테고리와의 다대일 관계 설정
    @JoinColumn(name = "category_id") // 외래 키 설정
    private TournamentCategory category;

    private LocalDateTime receiptStart;
    private LocalDateTime receiptEnd;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private TournamentStatus status;
    private int format;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Groups group; // 어떤 모임이 대회를 만들었는지 저장

    private int capacity;
    private int currentTeamCount;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(); // 처음 저장할 때만 현재 시간을 설정
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now(); // 업데이트할 때마다 현재 시간을 설정
    }


    // User와의 ManyToOne 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "user_id", nullable = true)
    private User createdBy;

    // TournamentParticipant와의 OneToMany 관계 설정
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TournamentParticipant> participants = new ArrayList<>();

}
