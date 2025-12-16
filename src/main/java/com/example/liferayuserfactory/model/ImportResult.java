package com.example.liferayuserfactory.model;

import java.util.ArrayList;
import java.util.List;

public class ImportResult {
    private int totalRows;
    private int created;
    private List<FailedRow> failures = new ArrayList<>();

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
}
