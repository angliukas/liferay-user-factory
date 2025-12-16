package com.example.liferayuserfactory.service;

import com.example.liferayuserfactory.model.UserRecord;
import org.apache.poi.ss.usermodel.Cell;
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
            Iterator<Row> iterator = sheet.iterator();

            boolean headerProcessed = false;
            while (iterator.hasNext()) {
                Row row = iterator.next();
                if (!headerProcessed && looksLikeHeader(row)) {
                    headerProcessed = true;
                    continue;
                }
                String email = getCellValue(row.getCell(0));
                String firstName = getCellValue(row.getCell(1));
                String lastName = getCellValue(row.getCell(2));

                if (email.isBlank() && firstName.isBlank() && lastName.isBlank()) {
                    continue;
                }
                users.add(new UserRecord(email.trim(), firstName.trim(), lastName.trim()));
            }
        }
        return users;
    }

    private boolean looksLikeHeader(Row row) {
        String email = getCellValue(row.getCell(0)).toLowerCase();
        String firstName = getCellValue(row.getCell(1)).toLowerCase();
        String lastName = getCellValue(row.getCell(2)).toLowerCase();
        return email.contains("email") && firstName.contains("name") && lastName.contains("surname");
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
}
