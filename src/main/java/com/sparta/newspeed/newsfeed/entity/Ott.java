package com.sparta.newspeed.newsfeed.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ott")
public class Ott {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "newsfeed_seq")
    private Long newsFeedSeq;

    @Column(name = "ott_name")
    private String ottName;

    @Column(name = "price")
    private int price;

    @Column(name = "max_member")
    private int maxMember;

    public Ott(String ottName, int price, int maxMember) {
        this.ottName = ottName;
        this.price = price;
        this.maxMember = maxMember;
    }
}
