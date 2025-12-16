package com.example.liferayuserfactory.model;

import java.util.ArrayList;
import java.util.List;

import com.example.liferayuserfactory.model.UserSummary;

public class ImportResult {
    private int totalRows;
    private int created;
    private List<FailedRow> failures = new ArrayList<>();
    private List<FailedRow> validationErrors = new ArrayList<>();
    private List<UserSummary> importedUsers = new ArrayList<>();
    private List<UserSummary> existingUsers = new ArrayList<>();

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public List<FailedRow> getFailures() {
        return failures;
    }

    public void setFailures(List<FailedRow> failures) {
        this.failures = failures;
    }

    public List<FailedRow> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<FailedRow> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public List<UserSummary> getImportedUsers() {
        return importedUsers;
    }

    public void setImportedUsers(List<UserSummary> importedUsers) {
        this.importedUsers = importedUsers;
    }

    public List<UserSummary> getExistingUsers() {
        return existingUsers;
    }

    public void setExistingUsers(List<UserSummary> existingUsers) {
        this.existingUsers = existingUsers;
    }
}
