package com.MeetingWeb.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class GroupCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupCategoryId;

    @Column(unique = true)
    private String category;

}
