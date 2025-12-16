package com.example.liferayuserfactory.model;

import java.util.ArrayList;
import java.util.List;

import com.example.liferayuserfactory.model.LiferayUser;

public class ImportResult {
    private int totalRows;
    private int created;
    private List<FailedRow> failures = new ArrayList<>();
    private List<FailedRow> validationErrors = new ArrayList<>();
    private List<LiferayUser> importedUsers = new ArrayList<>();
    private List<LiferayUser> existingUsers = new ArrayList<>();

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

    public List<LiferayUser> getImportedUsers() {
        return importedUsers;
    }

    public void setImportedUsers(List<LiferayUser> importedUsers) {
        this.importedUsers = importedUsers;
    }

    public List<LiferayUser> getExistingUsers() {
        return existingUsers;
    }

    public void setExistingUsers(List<LiferayUser> existingUsers) {
        this.existingUsers = existingUsers;
    }
}
