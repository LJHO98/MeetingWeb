package com.MeetingWeb.Entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class UserSelectCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="category_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    @OnDelete(action = OnDeleteAction.CASCADE) // User 삭제 시 연관된 UserSelectCategory 자동 삭제
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_category_id")
    private GroupCategory groupCategory;
}
