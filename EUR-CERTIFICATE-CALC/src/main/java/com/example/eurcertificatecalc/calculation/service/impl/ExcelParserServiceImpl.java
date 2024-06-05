package com.example.eurcertificatecalc.calculation.service.impl;

import com.example.eurcertificatecalc.calculation.model.DocumentOfOrigin;
import com.example.eurcertificatecalc.calculation.model.InfoIndex;
import com.example.eurcertificatecalc.calculation.model.PartComponent;
import com.example.eurcertificatecalc.calculation.model.ValidationResult;
import com.example.eurcertificatecalc.calculation.service.ExcelParserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class ExcelParserServiceImpl implements ExcelParserService {

    private Set<String> missingDeclarationForIncomeID = new HashSet<>();

    @Override
    public Set<String> getMissingDeclarationsForIncomeId() {
        return missingDeclarationForIncomeID;
    }

    @Override
    public Map<String, Double> getExpensesForProduct(MultipartFile file) throws IOException {

        Map<String, Double> info = new TreeMap<>();
        var inputStream = new BufferedInputStream(file.getInputStream());
        var workbook = new XSSFWorkbook(inputStream);
        var sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        boolean isStartOfGroup = false;

        while (rowIterator.hasNext()) {

            var row = (XSSFRow) rowIterator.next();
            Iterator<Cell> cellIterator = row.iterator();
            var last = row.getLastCellNum();

            while (cellIterator.hasNext()) {

                var currentCell = cellIterator.next();
                if (!isStartOfGroup) {
                    // If this is the start of a new group of products, look for the "Product name" cell
                    if (currentCell.getStringCellValue().equals("Product name")) {
                        isStartOfGroup = true;
                    }
                    break;

                } else {
                    if (currentCell.getColumnIndex() == (last - 1)) {
                        info.put(row.getCell(0).getStringCellValue(), currentCell.getNumericCellValue());
                    }
                }
            }
        }

        return info;
    }

    @Override
    public ValidationResult validateExcelFileBillOfMaterials(MultipartFile file) throws IOException {

        var isValid = new ValidationResult();
        List<String> errorList = new ArrayList<>();
        Map<String, Long> headerList = new HashMap<>();
        var inputStream = new BufferedInputStream(file.getInputStream());
        var workbook = new HSSFWorkbook(inputStream);
        var sheet = workbook.getSheetAt(0);

        Map<String, InfoIndex> indexMap = new HashMap<>() {
            {
                put("№", InfoIndex.builder().id(0L).name("№").build());
                put("Найменування комплектуючих", InfoIndex.builder().id(1L).name("Найменування комплектуючих").build());
                put("Артикул", InfoIndex.builder().id(2L).name("Артикул").build());
                put("Постачальник", InfoIndex.builder().id(3L).name("Постачальник").build());
                put("Код товару УКТЗЕД", InfoIndex.builder().id(4L).name("Код товару УКТЗЕД").build());
                put("Документ Надходження", InfoIndex.builder().id(5L).name("Документ Надходження").build());
                put("Країна походження", InfoIndex.builder().id(6L).name("Країна походження").build());
                put("Кількість", InfoIndex.builder().id(7L).name("Кількість").build());
                put("Од. Виміру", InfoIndex.builder().id(8L).name("Од. Виміру").build());
                put("Ціна", InfoIndex.builder().id(9L).name("Ціна").build());
                put("Сума", InfoIndex.builder().id(10L).name("Сума").build());
            }
        };
        Iterator<Row> rowIterator = sheet.iterator();

        boolean isHeader = true;

        while (rowIterator.hasNext()) {
            var row = rowIterator.next();
            Iterator<Cell> cellIterator = row.iterator();
            if (isHeader) {
                while (cellIterator.hasNext()) {
                    var currentCell = cellIterator.next();
                    if (Objects.requireNonNull(currentCell.getCellType()) == CellType.STRING) {
                        headerList.put(currentCell.getStringCellValue(), (long) currentCell.getColumnIndex());
                        if (!indexMap.containsKey(currentCell.getStringCellValue())) {
                            isValid.setValid(false);
                            errorList.add("Bill of materials : \n Invalid header value: " + currentCell.getStringCellValue());
                        }
                        isHeader = false;
                    }
                }
            } else {
                while (cellIterator.hasNext()) {
                    var currentCell = cellIterator.next();
                    if (currentCell.getCellType() == CellType.BLANK
                            && headerList.containsValue((long) currentCell.getColumnIndex())
                            && ((long) currentCell.getColumnIndex() != headerList.get("№"))
                            && ((long) currentCell.getColumnIndex() != headerList.get("Артикул"))) {

                        Optional<String> key = headerList.entrySet()
                                .stream()
                                .filter(entry -> entry.getValue().equals((long) currentCell.getColumnIndex()))
                                .map(Map.Entry::getKey)
                                .findFirst();

                        isValid.setValid(false);
                        errorList.add("Bill of materials : \n Missing value for " + key.orElse("Wrong value") + " ; Row -> " + currentCell.getRowIndex() + " with Provider -> " + row.getCell(headerList.get("Постачальник").intValue()));
                    }
                }
            }
        }
        if (headerList.isEmpty()) {
            errorList.add("Bill of materials : \n No header was specified");
            isValid.setValid(false);
            isValid.setErrors(errorList);
            return isValid;
        }
        for (String value : indexMap.keySet()) {
            boolean obtained = false;
            for (String obtainedValue : headerList.keySet()) {
                if (value.equals(obtainedValue)) {
                    obtained = true;
                    break;
                }
            }
            if (!obtained)
                errorList.add("Bill of materials : \n Missing header element :  " + value);
        }
        isValid.setErrors(errorList);

        return isValid;
    }

    @Override
    public ValidationResult validateExcelFileExpenses(MultipartFile file) throws IOException {
        var isValid = new ValidationResult();
        Set<String> errorList = new HashSet<>();
        Set<String> headerList = new HashSet<>();
        List<String> headers = new ArrayList<>(List.of(
                "direct costs (materials),uah",
                "general production costs,uah including:",
                "rent of premises",
                "salary",
                "contributions to social events",
                "utilities",
                "improvement of technology and organization of production",
                "depreciation of fixed assets",
                "costs of maintenance of the production process",
                "labor protection costs, safety"
        ));
        var inputStream = new BufferedInputStream(file.getInputStream());
        var workbook = new XSSFWorkbook(inputStream);
        var sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        boolean isStartOfGroup = false;

        while (rowIterator.hasNext()) {

            var row = (XSSFRow) rowIterator.next();
            Iterator<Cell> cellIterator = row.iterator();
            row.getLastCellNum();
            while (cellIterator.hasNext()) {

                var currentCell = cellIterator.next();
                if (!isStartOfGroup) {
                    if (currentCell.getStringCellValue().equals("Product name")) {
                        isStartOfGroup = true;
                    }
                    break;
                } else {
                    if (row.getCell(0).getStringCellValue().isEmpty()) {
                        errorList.add("Expenses : \n Missing value for Header ; Row -> " + (currentCell.getRowIndex() + 1));
                        isValid.setValid(false);
                    } else {
                        headerList.add(String.valueOf(row.getCell(0)).toLowerCase().replaceAll("\\s+", " ").trim());
                    }
                }
            }
        }

        headers.forEach(header -> {
            if (!headerList.contains(header.toLowerCase()))
                errorList.add("Expenses : \n Missing header -> " + header);
        });
        isValid.setErrors(errorList.stream().toList());

        return isValid;
    }

    @Override
    public Map<Long, PartComponent> getDataFromExcelTable(MultipartFile file) throws IOException {
        Map<Long, Map<String, Object>> dataMap = new HashMap<>();
        Map<Long, PartComponent> parts = new HashMap<>();

        var inputStream = new BufferedInputStream(file.getInputStream());
        var workbook = new HSSFWorkbook(inputStream);
        var sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        var indexForMap = 0L;
        var idForPart = new AtomicInteger(0);
        boolean isHeader = true;
        Map<String, InfoIndex> indexMap = new HashMap<>();

        while (rowIterator.hasNext()) {

            Map<String, Object> rowMapData = new HashMap<>();
            var row = rowIterator.next();
            Iterator<Cell> cellIterator = row.iterator();
            if (isHeader) {
                while (cellIterator.hasNext()) {

                    var currentCell = cellIterator.next();
                    if (Objects.requireNonNull(currentCell.getCellType()) == CellType.STRING) {
                        switch (currentCell.getStringCellValue().toLowerCase()) {
                            case "№" -> indexMap.put("№", InfoIndex.builder()
                                    .id((long) currentCell.getColumnIndex())
                                    .name("№")
                                    .build());
                            case "найменування комплектуючих" ->
                                    indexMap.put("Найменування комплектуючих", InfoIndex.builder()
                                            .id((long) currentCell.getColumnIndex())
                                            .name("Найменування комплектуючих")
                                            .build());
                            case "артикул" -> indexMap.put("Артикул", InfoIndex.builder()
                                    .id((long) currentCell.getColumnIndex())
                                    .name("Артикул")
                                    .build());
                            case "постачальник" -> indexMap.put("Постачальник", InfoIndex.builder()
                                    .id((long) currentCell.getColumnIndex())
                                    .name("Постачальник")
                                    .build());
                            case "код товару уктзед" -> indexMap.put("Код товару УКТЗЕД", InfoIndex.builder()
                                    .id((long) currentCell.getColumnIndex())
                                    .name("Код товару УКТЗЕД")
                                    .build());
                            case "документ надходження" -> indexMap.put("Документ Надходження", InfoIndex.builder()
                                    .id((long) currentCell.getColumnIndex())
                                    .name("Документ Надходження")
                                    .build());
                            case "країна походження" -> indexMap.put("Країна походження", InfoIndex.builder()
                                    .id((long) currentCell.getColumnIndex())
                                    .name("Країна походження")
                                    .build());
                            case "кількість" -> indexMap.put("Кількість", InfoIndex.builder()
                                    .id((long) currentCell.getColumnIndex())
                                    .name("Кількість")
                                    .build());
                            case "од. виміру" -> indexMap.put("Од. Виміру", InfoIndex.builder()
                                    .id((long) currentCell.getColumnIndex())
                                    .name("Од. Виміру")
                                    .build());
                            case "ціна" -> indexMap.put("Ціна", InfoIndex.builder()
                                    .id((long) currentCell.getColumnIndex())
                                    .name("Ціна")
                                    .build());
                            case "сума" -> indexMap.put("Сума", InfoIndex.builder()
                                    .id((long) currentCell.getColumnIndex())
                                    .name("Сума")
                                    .build());
                        }
                    }
                    isHeader = false;
                }
            } else {
                while (cellIterator.hasNext()) {

                    var currentCell = cellIterator.next();
                    indexMap.forEach((x, y) -> {
                        if (currentCell.getColumnIndex() == y.getId()) {
                            switch (currentCell.getCellType()) {
                                case BLANK -> {
                                    if (y.getName().equals("№")) {
                                        rowMapData.put("№", idForPart.getAndIncrement());
                                    } else {
                                        rowMapData.put(y.getName(), "");
                                    }
                                }
                                case NUMERIC -> {
                                    if (currentCell.getColumnIndex() == indexMap.get("Артикул").getId()) {
                                        rowMapData.put(y.getName(), Double.valueOf(currentCell.getNumericCellValue()).longValue());
                                    } else {
                                        rowMapData.put(y.getName(), currentCell.getNumericCellValue());
                                    }
                                }
                                case STRING -> rowMapData.put(y.getName(), currentCell.getStringCellValue());
                            }
                        }
                    });
                }
                if (!rowMapData.isEmpty()) {
                    dataMap.put(indexForMap, rowMapData);
                    indexForMap++;
                }
            }
        }
        dataMap.forEach((id, part) -> {
            PartComponent partEntity = null;
            try {
                partEntity = PartComponent.instance(part);
                List<String> fieldsToCheck = Arrays.asList("id", "description", "productArticle", "provider", "customsIdForPart", "documentOfOrigin", "countryOrigin", "quantity", "typeOfCounting", "valueForPiece", "price");
                partEntity.getMissingFields(fieldsToCheck);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            parts.put(id, partEntity);
        });

        return parts;
    }

    @Override
    public String getNumberOfDeclarationFromIncomeFile(MultipartFile file, DocumentOfOrigin documentOfOrigin) throws IOException, ParseException {
        var numberOfDeclaration = "";
        var inputStream = new BufferedInputStream(file.getInputStream());
        var workbook = new HSSFWorkbook(inputStream);
        var sheet = workbook.getSheetAt(0);
        boolean isHeader = true;
        Iterator<Row> rowIterator = sheet.iterator();
        Map<String, InfoIndex> indexMap = new HashMap<>();
        while (rowIterator.hasNext()) {

            var row = rowIterator.next();
            Iterator<Cell> cellIterator = row.iterator();
            if (isHeader) {
                while (cellIterator.hasNext()) {

                    var currentCell = cellIterator.next();
                    if (Objects.requireNonNull(currentCell.getCellType()) == CellType.STRING) {
                        switch (currentCell.getStringCellValue()) {
                            case "Номер" -> indexMap.put("Номер", InfoIndex.builder()
                                    .id((long) currentCell.getColumnIndex())
                                    .name("Номер")
                                    .build());
                            case "Номер вхід. документа" -> indexMap.put("Номер вхід. документа", InfoIndex.builder()
                                    .id((long) currentCell.getColumnIndex())
                                    .name("Номер вхід. документа")
                                    .build());
                            case "Дата" -> indexMap.put("Дата", InfoIndex.builder()
                                    .id((long) currentCell.getColumnIndex())
                                    .name("Дата")
                                    .build());
                        }
                    } else {
                        isHeader = false;
                    }
                }
            } else {
                while (cellIterator.hasNext()) {//todo FIND OUT WHY THE HECK THIS DOES NOT LOOP
                    var cellWithNumber = row.getCell(Math.toIntExact(indexMap.get("Номер").getId()));
                    var numberOfIncome = Double.valueOf(cellWithNumber.getNumericCellValue()).longValue();
                    var cellWithDate = row.getCell(Math.toIntExact(indexMap.get("Дата").getId()));
                    var dateOfIncomeString = cellWithDate.getStringCellValue();
                    var dateOfIncome = new SimpleDateFormat("dd.MM.yyyy").parse(dateOfIncomeString.substring(0, 10));
                    if (numberOfIncome == documentOfOrigin.getNumberOfDocument() && dateOfIncome.toString().equals(documentOfOrigin.getDate().toString())) {
                        Cell cellWithDeclaration = row.getCell(Math.toIntExact(indexMap.get("Номер вхід. документа").getId()));
                        switch (cellWithDeclaration.getCellType()) {
                            case STRING -> numberOfDeclaration = cellWithDeclaration.getStringCellValue();
                            case NUMERIC ->
                                    numberOfDeclaration = String.valueOf(Double.valueOf(cellWithDeclaration.getNumericCellValue()).longValue());
                        }

                        return numberOfDeclaration.replaceAll("/", "");
                    } else {
                        missingDeclarationForIncomeID.add(documentOfOrigin.getNumberOfDocument().toString());
                    }
                    break;
                }
            }
        }
        return numberOfDeclaration;

    }

    @Override
    public Integer getNumberOfGroupInDeclaration(MultipartFile declaration, String article) throws IOException {
        var inputStream = new BufferedInputStream(declaration.getInputStream());
        var workbook = new XSSFWorkbook(inputStream);
        var sheet = workbook.getSheetAt(0);
        boolean isHeader = true;
        Iterator<Row> rowIterator = sheet.iterator();
        Map<String, InfoIndex> indexMap = new HashMap<>();
        int numberOfGroup = 1;
        boolean isNumberRow = false;

        while (rowIterator.hasNext()) {

            var row = rowIterator.next();
            Iterator<Cell> cellIterator = row.iterator();
            if (isHeader) {
                while (cellIterator.hasNext() && isHeader) {

                    var currentCell = cellIterator.next();
                    if (Objects.requireNonNull(currentCell.getCellType()) == CellType.STRING) {
                        switch (currentCell.getStringCellValue()) {
                            case "Назва товару" -> {
                                indexMap.put("Назва товару", InfoIndex.builder()
                                        .id((long) currentCell.getColumnIndex())
                                        .name("Назва товару")
                                        .build());
                                isHeader = false;
                            }
                            case "Артикул" -> indexMap.put("Артикул", InfoIndex.builder()
                                    .id((long) currentCell.getColumnIndex())
                                    .name("Артикул")
                                    .build());
                        }
                    }
                }
            } else {
                var cell = row.getCell(Math.toIntExact(indexMap.get("Артикул").getId()));
                while (cellIterator.hasNext()) {//todo FIND OUT WHY THE HECK THIS DOES NOT LOOP

                    if (cell.getCellType().equals(CellType.BLANK) && isNumberRow) {
                        var cellWithNumber = row.getCell(Math.toIntExact(indexMap.get("Назва товару").getId()));
                        numberOfGroup = Integer.parseInt(cellWithNumber.getStringCellValue().substring(21));

                        return numberOfGroup;
                    }
                    if (cell.getStringCellValue().equals(article)) isNumberRow = true;

                    break;
                }
            }
        }

        return numberOfGroup;
    }
}
