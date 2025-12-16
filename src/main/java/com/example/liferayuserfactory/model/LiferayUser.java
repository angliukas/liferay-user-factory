package com.example.liferayuserfactory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "User_")
public class LiferayUser {

    @Id
    @Column(name = "userId")
    private Long id;

    @Column(name = "emailAddress")
    private String emailAddress;

    public Long getId() {
        return id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
