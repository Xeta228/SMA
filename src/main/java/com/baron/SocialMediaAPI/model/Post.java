package com.baron.SocialMediaAPI.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String label;
    private String text;

    private String image;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;


    @ManyToOne
    @JoinColumn(name = "author_id")
    private User user;


}
