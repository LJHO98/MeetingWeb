package com.MeetingWeb.Entity;

import com.MeetingWeb.Constant.Gender;
import com.MeetingWeb.Constant.RegistType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Groups {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Column(unique = true)
    private String name;
    private String introduce;
    @Lob
    private String description;

    private String category;
    private String location;
    @Enumerated(EnumType.STRING)
    private Gender genderPreference;
//    private Integer minAge;
//    private Integer maxAge;
    @Enumerated(EnumType.STRING)
    private RegistType registrationType;
    private Integer currentHeadCount = 0;
    private Integer capacity;
    private String profileImgUrl;
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

//   @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
//   private List<GroupDescriptionImg> descriptionImages;

    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<GroupMember> groupMembers = new ArrayList<>();
}
