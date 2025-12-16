package com.example.liferayuserfactory.service;

import com.example.liferayuserfactory.model.UserRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class ExcelUserParser {

    public List<UserRecord> parse(InputStream inputStream) throws IOException {
        List<UserRecord> users = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            boolean headerProcessed = false;
            for (int rowIndex = sheet.getFirstRowNum(); rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                if (!headerProcessed && looksLikeHeader(row, formatter)) {
                    headerProcessed = true;
                    continue;
                }
                String email = getCellValue(row.getCell(0), formatter);
                String firstName = getCellValue(row.getCell(1), formatter);
                String lastName = getCellValue(row.getCell(2), formatter);

                if (email.isBlank() && firstName.isBlank() && lastName.isBlank()) {
                    continue;
                }
                users.add(new UserRecord(email.trim(), firstName.trim(), lastName.trim()));
            }
        }
        return users;
    }

    private boolean looksLikeHeader(Row row, DataFormatter formatter) {
        String email = getCellValue(row.getCell(0), formatter).toLowerCase();
        String firstName = getCellValue(row.getCell(1), formatter).toLowerCase();
        String lastName = getCellValue(row.getCell(2), formatter).toLowerCase();
        return email.contains("email") && firstName.contains("name") && lastName.contains("surname");
    }

    private String getCellValue(Cell cell, DataFormatter formatter) {
        if (cell == null) {
            return "";
        }
        return formatter.formatCellValue(cell);
    }
}
