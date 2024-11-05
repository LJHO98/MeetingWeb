package com.MeetingWeb.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class TournamentCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tournamentCategoryId;

    @Column(unique = true)
    private String category;
}
