package com.MeetingWeb.Entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Tournaments{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tournamentId")
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

    private String status;
    private int format;
    private Long groupId;

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
    @JoinColumn(name = "created_by", referencedColumnName = "user_id", nullable = false)
    private User createdBy;



}
