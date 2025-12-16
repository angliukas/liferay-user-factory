package com.example.liferayuserfactory.model;

public class FailedRow {
    private int rowIndex;
    private String reason;
    private UserRecord user;

    public FailedRow() {
    }

    public FailedRow(int rowIndex, String reason, UserRecord user) {
        this.rowIndex = rowIndex;
        this.reason = reason;
        this.user = user;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public UserRecord getUser() {
        return user;
    }

    public void setUser(UserRecord user) {
        this.user = user;
    }
}
