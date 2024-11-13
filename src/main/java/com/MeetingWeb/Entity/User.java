package com.MeetingWeb.Entity;

import com.MeetingWeb.Constant.Gender;
import com.MeetingWeb.Constant.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="user_id")
    private Long id;

    @Column(unique = true)
    private String userName;
    private String password;
    private String name;

    @Column(unique = true)
    private String email;
    @Column(columnDefinition = "DATE")
    private LocalDate birthdate;
    private String activityArea;
    private String profileImgUrl;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSelectCategory> selectedCategories;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(); // 처음 저장할 때만 현재 시간을 설정
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now(); // 업데이트할 때마다 현재 시간을 설정
    }

    @OneToMany(mappedBy = "createdBy")
    private List<Groups> groups;

    @OneToMany(mappedBy = "createdBy")
    private List<Tournaments> tournaments;

    // User가 삭제될 때 관련 GroupMember 삭제
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<GroupMember> groupMembers;

    @PreRemove
    public void preRemove() {
        // Groups의 createdBy 필드를 null로 설정
        for (Groups group : groups) {
            group.setCreatedBy(null);
        }

        // Tournaments의 createdBy 필드를 null로 설정
        for (Tournaments tournament : tournaments) {
            tournament.setCreatedBy(null);
        }
    }

}
