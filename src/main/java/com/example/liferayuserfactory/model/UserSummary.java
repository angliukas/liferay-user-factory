package com.example.liferayuserfactory.model;

import java.time.LocalDateTime;

public class UserSummary {

    private Long id;
    private String emailAddress;
    private LocalDateTime createDate;

    public UserSummary() {
    }

    public UserSummary(Long id, String emailAddress, LocalDateTime createDate) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.createDate = createDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
}
