package com.MeetingWeb.Entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class GroupApplication {//모임신청

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId; //모임신청아이디

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Groups group;

    private LocalDateTime approvalDate; // 승인 시간 필드

    @PrePersist
    protected void onPrePersist() {
        this.approvalDate = LocalDateTime.now(); // 승인 시간 자동 설정
    }

//    private User id;

    private String reason; //가입이유
    private String say; //하고싶은말

}
