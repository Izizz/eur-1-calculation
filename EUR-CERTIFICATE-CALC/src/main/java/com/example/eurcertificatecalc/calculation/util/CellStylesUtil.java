package com.example.eurcertificatecalc.calculation.util;


import com.example.eurcertificatecalc.calculation.model.InfoIndex;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CellStylesUtil {

    public static CellStyle styleForCalculationColor(HSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());

        return cellStyle;
    }

    public static CellStyle styleForHeadersColumns(HSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setWrapText(true);
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        cellStyle.setFont(font);

        return cellStyle;
    }

    public static CellStyle boldText(HSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        cellStyle.setFont(font);

        return cellStyle;
    }

    public static CellStyle styleForCenterAlignment(HSSFWorkbook workbook) {
        CellStyle cs = workbook.createCellStyle();
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);

        return cs;
    }

    public static CellStyle styleForCenterBoldText(HSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        return cellStyle;
    }

    public static void setColumnWidthForTable(HSSFSheet sheet, Map<Long, InfoIndex> indexMap) {
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(0L).getId()), 3000);  // article
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(1L).getId()), 28000); // description
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(2L).getId()), 5000);  // provider
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(3L).getId()), 3400);  // type counting
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(4L).getId()), 7000);  // country
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(5L).getId()), 3400);  // country code
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(6L).getId()), 4000);  // customs code
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(7L).getId()), 4000);  // invoice
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(8L).getId()), 3000);  // invoice date
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(9L).getId()), 5300);  // declaration number
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(10L).getId()), 4000); // declaration date
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(11L).getId()), 2000); // declaration group
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(12L).getId()), 3000); // quantity
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(13L).getId()), 4000); // value
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(14L).getId()), 2500); // price pref
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(15L).getId()), 3400); // price non pref
    }

    public static void setColumnWidthForTableWithExpenses(HSSFSheet sheet, Map<Long, InfoIndex> indexMap) {
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(0L).getId()), 30000); // description
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(1L).getId()), 8000);  // provider
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(2L).getId()), 3000);  // customsId
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(3L).getId()), 5400);  // country
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(4L).getId()), 2400);  // quantity
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(5L).getId()), 4000);  // type counting
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(6L).getId()), 4000);  // value UAH
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(7L).getId()), 4000);  // price UAH
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(8L).getId()), 3000);  // Rate EUR-UAH
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(9L).getId()), 3000);  // Price EUR
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(10L).getId()), 5000); // Price product EUR
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(11L).getId()), 5000); // Percentage
        sheet.setColumnWidth(Math.toIntExact(indexMap.get(12L).getId()), 5000); // Percentage for group
    }
}