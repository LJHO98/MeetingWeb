package com.MeetingWeb.Entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Events {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Groups group;

    private String imageUrl;

    private String title;

    private String description;

    private LocalDateTime date;

    private String location;

    private Integer capacity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;





}
