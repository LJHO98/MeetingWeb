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


}
