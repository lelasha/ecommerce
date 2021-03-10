package com.bog.ecommerce.service;

import com.bog.ecommerce.model.Path;
import com.bog.ecommerce.model.dto.PurchasedProductDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class ExcelService {
    private static final String[] purchasedProductNameList = {"პროდუქტის სახელი", "ფასი", "რაოდენობა", "მაილი", "პირადი ნომერი"};

    @Autowired
    AnalyticService analyticService;

    public File createXLS(Long size, Long authSize) throws IOException, ParseException {


        // Create a Workbook
        Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

        // Create a Sheet
        Sheet sheet = workbook.createSheet("სტატისტიკა");
        Sheet sheet2 = workbook.createSheet("პროდუქციის სია");

        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        CellStyle hlink_style = workbook.createCellStyle();
        Font hlink_font = workbook.createFont();
        hlink_font.setUnderline(Font.U_SINGLE);
        hlink_font.setColor(IndexedColors.BLUE.getIndex());
        hlink_style.setFont(hlink_font);

        LinkedHashMap<String, Object> analyticMap = analyticService.buildHashMap(size,authSize);

        int rowNum = 0;
        for (Map.Entry<String, Object> entry : analyticMap.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue(entry.getKey());
            cell = row.createCell(1);
            Object value = entry.getValue();

            if (value instanceof Integer) cell.setCellValue((Integer) value);
            if (value instanceof Long) cell.setCellValue((Long) value);
            if (value instanceof BigDecimal) cell.setCellValue(String.valueOf(value));
            if (value instanceof List) {
                cell.setCellFormula("HYPERLINK(\"#'პროდუქციის სია'!A1\",\"პროდუქციის სია\")");
                cell.setCellStyle(hlink_style);

                Row newRow = sheet2.createRow(0);
                int newCellNum = 0;
                for (String s : purchasedProductNameList) {
                    Cell cell2 = newRow.createCell(newCellNum);
                    cell2.setCellValue(s);
                    cell2.setCellStyle(headerCellStyle);
                    newCellNum++;
                }


                    int productRowNum = 1;
                List<PurchasedProductDTO> purchasedProductDTOList = (List<PurchasedProductDTO>) value;
                for (PurchasedProductDTO purchasedProduct : purchasedProductDTOList) {
                    Row newRow2 = sheet2.createRow(productRowNum++);
                    newRow2.createCell(0).setCellValue(purchasedProduct.getName());
                    newRow2.createCell(1).setCellValue(String.valueOf(purchasedProduct.getPrice()));
                    newRow2.createCell(2).setCellValue(purchasedProduct.getQuantity());
                    newRow2.createCell(3).setCellValue(purchasedProduct.getEmail());
                    newRow2.createCell(4).setCellValue(purchasedProduct.getPersonalid());
                }
            }
        }
        int columNum = 5;
        for (int i = 0; i < columNum; i++) {
            sheet2.autoSizeColumn(i);
        }
        sheet.autoSizeColumn(0);
        sheet.setColumnWidth(1, 4250);


        String fileName = "analytic-" + analyticService.getCurrentDate() + ".xlsx";
        File file = new File(Path.excelPAth(), fileName);
        FileOutputStream fileOut = new FileOutputStream(file);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
        return file;
    }
}
