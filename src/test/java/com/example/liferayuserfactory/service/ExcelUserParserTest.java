package com.example.liferayuserfactory.service;

import com.example.liferayuserfactory.model.UserRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExcelUserParserTest {

    @Test
    void parsesRowsSkippingHeader() throws IOException {
        byte[] workbookBytes = buildWorkbook();
        ExcelUserParser parser = new ExcelUserParser();

        List<UserRecord> results = parser.parse(new ByteArrayInputStream(workbookBytes));

        assertEquals(2, results.size());
        assertEquals("user1@example.com", results.get(0).getEmail());
        assertEquals("Jane", results.get(1).getFirstName());
    }

    private byte[] buildWorkbook() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("email");
            header.createCell(1).setCellValue("name");
            header.createCell(2).setCellValue("surname");

            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("user1@example.com");
            row1.createCell(1).setCellValue("John");
            row1.createCell(2).setCellValue("Doe");

            Row row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("user2@example.com");
            row2.createCell(1).setCellValue("Jane");
            row2.createCell(2).setCellValue("Roe");

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                workbook.write(baos);
                return baos.toByteArray();
            }
        }
    }
}
