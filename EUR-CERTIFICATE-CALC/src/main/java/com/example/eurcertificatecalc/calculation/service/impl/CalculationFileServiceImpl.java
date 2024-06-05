package com.example.eurcertificatecalc.calculation.service.impl;



import com.example.eurcertificatecalc.calculation.data.entity.Attachment;
import com.example.eurcertificatecalc.calculation.model.InfoIndex;
import com.example.eurcertificatecalc.calculation.model.PartComponent;
import com.example.eurcertificatecalc.calculation.service.CalculationFileService;
import com.example.eurcertificatecalc.calculation.util.CellStylesUtil;
import com.example.eurcertificatecalc.calculation.util.CurrencyUtil;
import com.example.eurcertificatecalc.calculation.util.PreferentialUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


import static com.example.eurcertificatecalc.calculation.service.impl.CalculationServiceImpl.*;
@Service
@Slf4j
@RequiredArgsConstructor
public class CalculationFileServiceImpl implements CalculationFileService {

    private final PreferentialUtil preferentialStatusService;
    private final CurrencyUtil currencyRateService;
    @Value("${files.demo-file}")
    private String pathForCalculationFile;

    @Override
    public Attachment createCalculationFileWithAllExpenses(Map<Long, PartComponent> data, Map<String, Double> expenses, String productTitle) throws IOException, URISyntaxException, InterruptedException {
        resetAllVariables();
        currencyRate = currencyRateService.getCurrencyRate();
        Map<Long, PartComponent> preferentialParts = new HashMap<>();
        Map<Long, PartComponent> nonPreferentialParts = new HashMap<>();
        var wb = new HSSFWorkbook();
        var sheet = wb.createSheet("Calculation");
        var rowTitle = sheet.createRow((short) 3);
        var cellWithTitle = rowTitle.createCell(0);
        cellWithTitle.setCellValue("Калькуляція виробу " + productTitle);
        cellWithTitle.setCellStyle(CellStylesUtil.styleForCenterBoldText(wb));
        var rowHead = sheet.createRow((short) 6);
        var cs = CellStylesUtil.styleForCenterAlignment(wb);
        var indexesForHeader = initializeIndexesForCalculationWithExpenses();

        indexesForHeader.forEach(
                (id, index) -> {
                    var cell = rowHead.createCell(index.getId().intValue());
                    var cellStyle = CellStylesUtil.styleForHeadersColumns(wb);
                    cell.setCellValue(index.getName());
                    cell.setCellStyle(cellStyle);
                }
        );

        var rowNumber = new AtomicInteger(7);
        var startOfPreferentialGroup = new AtomicInteger(rowNumber.get());


        data.forEach((id, part) -> {
            try {
                if (preferentialStatusService.checkIfPartIsPreferential(part))
                    preferentialParts.put(id, part);
                else
                    nonPreferentialParts.put(id, part);
                priceProduct += part.getPrice();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        priceProductEuro = priceProduct / Double.parseDouble(currencyRate);

        data.forEach((id, part) -> {
            try {
                var percentage = Double.parseDouble(String.format(Locale.US,"%,.4f", (part.getPrice() / Double.parseDouble(currencyRate) / priceProductEuro) * 100));
                if (preferentialStatusService.checkIfPartIsPreferential(part))
                    preferentialPercentage += percentage;
                else
                    nonPreferentialPercentage += percentage;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        preferentialParts.forEach((id, part) -> {
            createRowForEachPartEntityWithExpenses(part, sheet, rowNumber.getAndIncrement(), indexesForHeader, currencyRate, preferentialPercentage, cs, wb);
            preferentialCostEuro += (part.getPrice() / Double.parseDouble(currencyRate));
            preferentialCost += part.getPrice();
        });

        var finishOfPreferentialGroup = new AtomicInteger(rowNumber.get() - 1);

        var separate = sheet.createRow(rowNumber.getAndIncrement());
        var cellSum = separate.createCell(5);
        cellSum.setCellValue("Сума преф.");
        cellSum.setCellStyle(CellStylesUtil.styleForCenterBoldText(wb));
        var cellBoldSum = separate.createCell(9);
        cellBoldSum.setCellValue(preferentialCostEuro);
        cellBoldSum.setCellStyle(CellStylesUtil.boldText(wb));

        var cellBoldSumEur = separate.createCell(7);
        cellBoldSumEur.setCellValue(preferentialCost);
        cellBoldSumEur.setCellStyle(CellStylesUtil.boldText(wb));

        var startOfNonPreferentialGroup = new AtomicInteger(finishOfPreferentialGroup.get() + 2);

        nonPreferentialParts.forEach((id, part) -> {
            createRowForEachPartEntityWithExpenses(part, sheet, rowNumber.getAndIncrement(), indexesForHeader, currencyRate, nonPreferentialPercentage, cs, wb);
            nonPreferentialCostEuro += (part.getPrice() / Double.parseDouble(currencyRate));
            nonPreferentialCost += part.getPrice();
        });

        var finishOfNonPreferentialGroup = new AtomicInteger(rowNumber.get() - 1);

        if (preferentialParts.size() > 1) {
            sheet.addMergedRegion(
                    new CellRangeAddress(
                            startOfPreferentialGroup.get(),
                            finishOfPreferentialGroup.get(),
                            Math.toIntExact(indexesForHeader.get(12L).getId()),
                            Math.toIntExact(indexesForHeader.get(12L).getId())
                    )
            );
        }
        if (nonPreferentialParts.size() > 1) {
            sheet.addMergedRegion(
                    new CellRangeAddress(
                            startOfNonPreferentialGroup.get(),
                            finishOfNonPreferentialGroup.get(),
                            Math.toIntExact(indexesForHeader.get(12L).getId()),
                            Math.toIntExact(indexesForHeader.get(12L).getId())
                    )
            );
        }
        var separate1 = sheet.createRow(finishOfNonPreferentialGroup.get() + 1);
        var cellWithSumNonPref = separate1.createCell(5);
        cellWithSumNonPref.setCellValue("Сума непреф.");
        cellWithSumNonPref.setCellStyle(CellStylesUtil.styleForCenterBoldText(wb));

        var cellBoldNonPrefSum = separate1.createCell(7);
        cellBoldNonPrefSum.setCellValue(nonPreferentialCost);
        cellBoldNonPrefSum.setCellStyle(CellStylesUtil.boldText(wb));

        var cellBoldNonPrefSumEur = separate1.createCell(9);
        cellBoldNonPrefSumEur.setCellValue(nonPreferentialCostEuro);
        cellBoldNonPrefSumEur.setCellStyle(CellStylesUtil.boldText(wb));

        Row separate2 = sheet.createRow(finishOfNonPreferentialGroup.get() + 2);
        var cellSumProduct = separate2.createCell(5);
        cellSumProduct.setCellValue("Сума");
        cellSumProduct.setCellStyle(CellStylesUtil.styleForCenterBoldText(wb));

        var cellBoldSumProduct = separate2.createCell(7);
        cellBoldSumProduct.setCellValue(priceProduct);
        cellBoldSumProduct.setCellStyle(CellStylesUtil.boldText(wb));

        var cellBoldSumProductEur = separate2.createCell(9);
        cellBoldSumProductEur.setCellValue(priceProductEuro);
        cellBoldSumProductEur.setCellStyle(CellStylesUtil.boldText(wb));

        createExpensesSectionOnTable(expenses, finishOfNonPreferentialGroup.get() + 3, sheet);


        FileOutputStream fileOut = new FileOutputStream(pathForCalculationFile);
        wb.write(fileOut);
        fileOut.close();
        wb.close();

        var path = Paths.get(pathForCalculationFile);

        var productName = productTitle.replaceAll("/", " ");
        var name = "calculationWithExpenses " + productName + ".xls";

        return createFile(path, name, "application/vnd.ms-excel");
    }

    @Override
    public Attachment createCalculationFileWithoutExpenses(Map<Long, PartComponent> data, String productTitle) throws IOException {
        var wb = new HSSFWorkbook();
        var cs = CellStylesUtil.styleForCenterAlignment(wb);
        var cellStyleBoldCenter = CellStylesUtil.styleForCenterBoldText(wb);
        var cellStyleBlue = CellStylesUtil.styleForCalculationColor(wb);
        var sheet = wb.createSheet("Calculation");
        var rowTitle = sheet.createRow(1);
        var cellWithTitle = rowTitle.createCell(1);
        cellWithTitle.setCellStyle(cellStyleBoldCenter);
        cellWithTitle.setCellValue("Калькуляція виробу " + productTitle);
        var rowHead = sheet.createRow((short) 3);
        var indexForHeader = initializeIndexesForCalculation();

        indexForHeader.forEach(
                (id, index) -> {
                    var cellStyle = CellStylesUtil.styleForHeadersColumns(wb);
                    var cell = rowHead.createCell(index.getId().intValue());
                    cell.setCellValue(index.getName());
                    cell.setCellStyle(cellStyle);
                });

        var rowNumber = new AtomicInteger(4);

        data.forEach((id, part) -> {
            try {
                createRowForEachPartEntity(part, sheet, rowNumber.getAndIncrement(), indexForHeader, cs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        var rowWithSum = sheet.createRow(rowNumber.getAndIncrement());
        var cellWiThSum = rowWithSum.createCell(0);
        cellWiThSum.setCellValue("Всього");
        cellWiThSum.setCellStyle(cellStyleBoldCenter);

        var cellWithPreferentialSum = rowWithSum.createCell(14);
        cellWithPreferentialSum.setCellValue(preferentialCost);
        cellWithPreferentialSum.setCellStyle(CellStylesUtil.boldText(wb));

        var cellWithNonPreferentialSum = rowWithSum.createCell(15);
        cellWithNonPreferentialSum.setCellValue(nonPreferentialCost);
        cellWithNonPreferentialSum.setCellStyle(CellStylesUtil.boldText(wb));

        // Sum up for file

        var rowWithNonPreferentialPrice = sheet.createRow(rowNumber.getAndIncrement() + 1);
        var cellSumNonPref = rowWithNonPreferentialPrice.createCell(5);
        cellSumNonPref.setCellValue("Вартість сировини непреференційного походження");
        cellSumNonPref.setCellStyle(cellStyleBoldCenter);
        sheet.addMergedRegion(new CellRangeAddress(rowNumber.get(), rowNumber.get(), 5, 9));

        var nonPreferentialSum = rowWithNonPreferentialPrice.createCell(10);
        nonPreferentialSum.setCellValue(nonPreferentialCost);
        nonPreferentialSum.setCellStyle(cellStyleBlue);

        var rowWithPriceOfProduct = sheet.createRow(rowNumber.getAndIncrement() + 1);
        var cellProduct = rowWithPriceOfProduct.createCell(5);
        cellProduct.setCellValue("Загальна вартість продукції");
        cellProduct.setCellStyle(cellStyleBoldCenter);
        sheet.addMergedRegion(new CellRangeAddress(rowNumber.get(), rowNumber.get(), 5, 9));

        var priceProductSum = rowWithPriceOfProduct.createCell(10);
        priceProductSum.setCellValue(priceProduct);
        priceProductSum.setCellStyle(cellStyleBlue);

        var rowWithPercentage = sheet.createRow(rowNumber.getAndIncrement() + 1);
        var cellPercentage = rowWithPercentage.createCell(4);
        cellPercentage.setCellValue("Співвідношення сировини непреференційного походження до загальної вартості (%)");
        cellPercentage.setCellStyle(cellStyleBoldCenter);
        sheet.addMergedRegion(new CellRangeAddress(rowNumber.get(), rowNumber.get(), 4, 9));

        var percentageSum = rowWithPercentage.createCell(10);
        percentageSum.setCellValue((nonPreferentialCost / priceProduct) * 100);
        percentageSum.setCellStyle(cellStyleBlue);

        var fileOut = new FileOutputStream(pathForCalculationFile);
        wb.write(fileOut);
        fileOut.close();
        wb.close();

        var path = Paths.get(pathForCalculationFile);

        var productName = productTitle.replaceAll("/", " ");
        var name = "calculation " + productName + ".xls";
        return createFile(path, name, "application/vnd.ms-excel");

    }

    @Override
    public Attachment createCalculationDeclarationDocx(Map<Long, PartComponent> data, String productTitle) throws IOException {
        Map<Long, PartComponent> parts = new HashMap<>();

        data.forEach((id, part) -> {
            try {
                if (!preferentialStatusService.checkIfPartIsPreferential(part)) {
                    var partEntity = PartComponent.builder()
                            .description(part.getDescription())
                            .customsIdForPart(part.getCustomsIdForPart())
                            .price(part.getPrice())
                            .percentage(part.getPercentage())
                            .build();
                    parts.put(id, partEntity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        var document = new XWPFDocument();
        var paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        var run = paragraph.createRun();
        run.setFontFamily("Times New Roman");
        run.setBold(true);
        run.setFontSize(16);
        run.setText("Декларація");

        var paragraph1 = document.createParagraph();
        paragraph1.setAlignment(ParagraphAlignment.LEFT);
        var run1 = paragraph1.createRun();
        run1.setFontFamily("Times New Roman");
        run1.setFontSize(12);
        run1.setText("""
                Я, що нижче підписався, постачальник (виробник) товарів, зазначених у
                цій Декларації, декларую (засвідчую), що:
                """);

        var paragraph8 = document.createParagraph();
        var run8 = paragraph8.createRun();
        run8.setFontFamily("Times New Roman");
        run8.setFontSize(12);
        run8.setText("""
                1. Матеріали, що не мають преференційного походження, були
                використані в Україні для виробництва таких товарів:
                """);

        var table = document.createTable((short) parts.size() + 1, 5);
        var header = table.getRow(0);

        var cellProduct = header.getCell(0);
        cellProduct.setText("Опис товарів, що поставляються");

        var cellNonPrefPart = header.getCell(1);
        cellNonPrefPart.setText("""
                Опис використаних
                матеріалів, що не мають
                преференційного статусу
                походження
                """);

        var cellCustomsId = header.getCell(2);
        cellCustomsId.setText("""
                Товарна позиція згідно
                з УКТЗЕД використаних
                матеріалів, що не мають префенційного статусу походження
                """);


        var cellPriceNonPrefPart = header.getCell(3);
        cellPriceNonPrefPart.setText("Вартість використаних матеріалів, що не мають преференційного статусу походження");

        var cellPercentageNonPrefPart = header.getCell(4);
        cellPercentageNonPrefPart.setText("Відсоток використаних матеріалів, що не мають преференційного походження, у ціні кінцевого товару");

        var firstRow = table.getRow(0);
        var title = firstRow.getCell(0);
        title.setText(productTitle);
        var index = new AtomicInteger(1);

        var values = parts.values();
        Iterator<PartComponent> iterator = values.iterator();

        while (iterator.hasNext()) {
            var currentRow = table.getRow(index.getAndIncrement());
            var part = iterator.next();
            var cellWithDescr = currentRow.getCell(1);
            cellWithDescr.setText(part.getDescription());

            var cellWithCustomsId = currentRow.getCell(2);
            cellWithCustomsId.setText(part.getCustomsIdForPart().toString());

            var cellWithPrice = currentRow.getCell(3);
            cellWithPrice.setText(part.getPrice().toString());

            var cellWithPercentage = currentRow.getCell(4);
            cellWithPercentage.setText(part.getPercentage().toString());
            if (!iterator.hasNext()) {
                var sumRow = table.createRow();

                var cellSum = sumRow.getCell(2);
                cellSum.setText("Усього : ");

                var cellWithSumPrice = sumRow.getCell(3);
                cellWithSumPrice.setText(String.format("%.2f", nonPreferentialCost));

                var cellWithSumPercentage = sumRow.getCell(4);
                cellWithSumPercentage.setText(String.format("%.2f", nonPreferentialPercentage));

            }
        }

        document.createParagraph();

        var paragraph2 = document.createParagraph();
        paragraph2.setAlignment(ParagraphAlignment.LEFT);
        var run2 = paragraph2.createRun();
        run2.setFontFamily("Times New Roman");
        run2.setFontSize(12);
        run2.setText("2. Усі інші матеріали, що були використані в Україні для виробництва цих товарів," +
                " мають преференційний статус походження з ЄС відповідно до правил походження" +
                " в рамках вільної торгівлі з ЄС.\n");

        var paragraph3 = document.createParagraph();
        var run3 = paragraph3.createRun();
        run3.setFontFamily("Times New Roman");
        run3.setFontSize(12);
        run3.setText(
                "Я декларую (засвідчую):\n");

        var paragraph4 = document.createParagraph();
        var run4 = paragraph4.createRun();
        run4.setFontFamily("Times New Roman");
        run4.setFontSize(12);
        run4.setText("       кумуляція застосовується з ____________________________________________\n");

        var paragraph5 = document.createParagraph();
        var run5 = paragraph5.createRun();
        run5.setFontFamily("Times New Roman");
        run5.setFontSize(12);
        run5.setText("""
                \s
                 X  кумуляція не застосовується.
                """);


        var paragraph6 = document.createParagraph();
        var run6 = paragraph6.createRun();
        run6.setFontFamily("Times New Roman");
        run6.setFontSize(12);
        run6.setText("Я зобов’язуюсь надати митниці на її вимогу будь-які інші підтвердні документи.\n" +
                "                                                                                      ");

        var fileOut = new FileOutputStream(pathForCalculationFile);
        document.write(fileOut);
        fileOut.close();

        var path = Paths.get(pathForCalculationFile);
        var productName = productTitle.replaceAll("/", " ");
        var name = "Declaration " + productName + ".docx";
        var contentType = "aapplication/vnd.openxmlformats-officedocument.wordprocessingml.document";

        return createFile(path, name, contentType);
    }

    private void createRowForEachPartEntityWithExpenses(PartComponent part,
                                                        HSSFSheet sheet,
                                                        int rowNumber,
                                                        Map<Long, InfoIndex> indexMap,
                                                        String rate,
                                                        Double percentage,
                                                        CellStyle cs,
                                                        HSSFWorkbook wb) {
        var row = sheet.createRow(rowNumber);
        CellStylesUtil.setColumnWidthForTableWithExpenses(sheet, indexMap);

        var cellWithDescription = row.createCell(Math.toIntExact(indexMap.get(0L).getId()));
        cellWithDescription.setCellValue(part.getDescription());

        var cellWithProvider = row.createCell(Math.toIntExact(indexMap.get(1L).getId()));
        cellWithProvider.setCellValue(part.getProvider());
        cellWithProvider.setCellStyle(cs);

        var cellWithCustomsId = row.createCell(Math.toIntExact(indexMap.get(2L).getId()));
        cellWithCustomsId.setCellValue(part.getCustomsIdForPart());
        cellWithCustomsId.setCellStyle(cs);

        var cellWithCountry = row.createCell(Math.toIntExact(indexMap.get(3L).getId()));
        cellWithCountry.setCellValue(part.getCountryOrigin());
        cellWithCountry.setCellStyle(cs);

        var cellWithQuantity = row.createCell(Math.toIntExact(indexMap.get(4L).getId()));
        cellWithQuantity.setCellValue(part.getQuantity());
        cellWithQuantity.setCellStyle(cs);

        var cellWithTypeOfCounting = row.createCell(Math.toIntExact(indexMap.get(5L).getId()));
        cellWithTypeOfCounting.setCellValue(part.getTypeOfCounting());
        cellWithTypeOfCounting.setCellStyle(cs);

        var cellWithValue = row.createCell(Math.toIntExact(indexMap.get(6L).getId()));
        cellWithValue.setCellValue(part.getValueForPiece());
        cellWithValue.setCellStyle(cs);

        var cellWithPrice = row.createCell(Math.toIntExact(indexMap.get(7L).getId()));
        cellWithPrice.setCellValue(part.getPrice());
        cellWithPrice.setCellStyle(cs);

        var cellWithRate = row.createCell(Math.toIntExact(indexMap.get(8L).getId()));
        cellWithRate.setCellValue(Double.parseDouble(rate));
        cellWithRate.setCellStyle(cs);

        var cellWithPriceEur = row.createCell(Math.toIntExact(indexMap.get(9L).getId()));
        cellWithPriceEur.setCellValue(part.getPrice() / Double.parseDouble(rate));
        cellWithPriceEur.setCellStyle(cs);

        var cellWithPriceProduct = row.createCell(Math.toIntExact(indexMap.get(10L).getId()));
        cellWithPriceProduct.setCellValue(priceProductEuro);
        cellWithPriceProduct.setCellStyle(cs);

        part.setPercentage(Double.parseDouble(
                String.format(Locale.US,"%,.7f", (part.getPrice() / Double.parseDouble(rate) / priceProductEuro) * 100))
        );
        var cellWithPercentage = row.createCell(Math.toIntExact(indexMap.get(11L).getId()));
        cellWithPercentage.setCellValue(part.getPercentage());
        cellWithPercentage.setCellStyle(cs);

        var cellWithSumPercentage = row.createCell(Math.toIntExact(indexMap.get(12L).getId()));
        cellWithSumPercentage.setCellValue(percentage);
        cellWithSumPercentage.setCellStyle(CellStylesUtil.styleForCenterBoldText(wb));
    }

    private void createRowForEachPartEntity(PartComponent part,
                                            HSSFSheet sheet,
                                            int rowNumber,
                                            Map<Long, InfoIndex> indexMap,
                                            CellStyle style) throws Exception {

        var row = sheet.createRow(rowNumber);
        String dateForTable;
        String dateForInvoice;

        if (part.getInvoiceDate() != null) {
            var invoiceDate = part.getInvoiceDate();
            dateForInvoice = getDateForTable(invoiceDate);
        } else {
            dateForInvoice = "";
        }
        if (part.getDocumentOfOrigin() != null) {
            var date = part.getDocumentOfOrigin().getDate();
            dateForTable = getDateForTable(date);
        } else {
            dateForTable = "";
        }

        priceProduct += part.getPrice();
        CellStylesUtil.setColumnWidthForTable(sheet, indexMap);

        var cellWithArticle = row.createCell(Math.toIntExact(indexMap.get(0L).getId()));
        cellWithArticle.setCellValue(part.getProductArticle());
        cellWithArticle.setCellStyle(style);

        row.createCell(Math.toIntExact(indexMap.get(1L).getId())).setCellValue(part.getDescription());

        var cellWithProvider = row.createCell(Math.toIntExact(indexMap.get(2L).getId()));
        cellWithProvider.setCellValue(part.getProvider());
        cellWithProvider.setCellStyle(style);


        var cellWithTypeOfCounting = row.createCell(Math.toIntExact(indexMap.get(3L).getId()));
        cellWithTypeOfCounting.setCellValue(part.getTypeOfCounting());
        cellWithTypeOfCounting.setCellStyle(style);

        var cellWithCountry = row.createCell(Math.toIntExact(indexMap.get(4L).getId()));
        cellWithCountry.setCellValue(part.getCountryOrigin());
        cellWithCountry.setCellStyle(style);

        var cellWithCountryCode = row.createCell(Math.toIntExact(indexMap.get(5L).getId()));
        cellWithCountryCode.setCellValue(part.getCountryCode());
        cellWithCountryCode.setCellStyle(style);

        var cellWithCustomsId = row.createCell(Math.toIntExact(indexMap.get(6L).getId()));
        cellWithCustomsId.setCellValue(part.getCustomsIdForPart());
        cellWithCustomsId.setCellStyle(style);

        var cellWithInvoiceNumber = row.createCell(Math.toIntExact(indexMap.get(7L).getId()));
        cellWithInvoiceNumber.setCellValue(part.getInvoiceNumber());
        cellWithInvoiceNumber.setCellStyle(style);

        var cellWithInvoiceDate = row.createCell(Math.toIntExact(indexMap.get(8L).getId()));
        cellWithInvoiceDate.setCellValue(dateForInvoice);
        cellWithInvoiceDate.setCellStyle(style);


        var cellWithDeclarationNumber = row.createCell(Math.toIntExact(indexMap.get(9L).getId()));
        cellWithDeclarationNumber.setCellValue(part.getDeclarationNumber());
        cellWithDeclarationNumber.setCellStyle(style);

        var cellWithDate = row.createCell(Math.toIntExact(indexMap.get(10L).getId()));
        cellWithDate.setCellValue(dateForTable);
        cellWithDate.setCellStyle(style);


        if (part.getNumberOfGroupInCustomsDeclaration() != null) {
            var cellWithGroupNum = row.createCell(Math.toIntExact(indexMap.get(11L).getId()));
            cellWithGroupNum.setCellValue(part.getNumberOfGroupInCustomsDeclaration());
            cellWithGroupNum.setCellStyle(style);
        }

        var cellWithQuantity = row.createCell(Math.toIntExact(indexMap.get(12L).getId()));
        cellWithQuantity.setCellValue(part.getQuantity());
        cellWithQuantity.setCellStyle(style);

        var cellWithValue = row.createCell(Math.toIntExact(indexMap.get(13L).getId()));
        cellWithValue.setCellValue(part.getValueForPiece());
        cellWithValue.setCellStyle(style);

        if (preferentialStatusService.checkIfPartIsPreferential(part)) {

            preferentialCost += part.getPrice();
            var cellWithNonPreferentialPrice = row.createCell(Math.toIntExact(indexMap.get(14L).getId()));
            cellWithNonPreferentialPrice.setCellValue(part.getPrice());
            cellWithNonPreferentialPrice.setCellStyle(style);

        } else {

            nonPreferentialCost += part.getPrice();
            var cellWithPreferentialPrice = row.createCell(Math.toIntExact(indexMap.get(15L).getId()));
            cellWithPreferentialPrice.setCellValue(part.getPrice());
            cellWithPreferentialPrice.setCellStyle(style);

        }
    }

    private void resetAllVariables() {
        priceProductEuro = 0.00;
        priceProduct = 0.00;
        preferentialCost = 0.00;
        nonPreferentialCost = 0.00;
        preferentialPercentage = 0.00;
        nonPreferentialPercentage = 0.00;
    }

    private String getDateForTable(Date date) {

        String dateFormatted = "";

        var calendar = Calendar.getInstance();
        calendar.setTime(date);

        dateFormatted += calendar.get(Calendar.DAY_OF_MONTH) + ".";
        dateFormatted += (calendar.get(Calendar.MONTH) + 1) + ".";
        dateFormatted += calendar.get(Calendar.YEAR);

        return dateFormatted;
    }

    private Attachment createFile(Path path, String name, String fileType) {
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
            var f = new File(pathForCalculationFile);
            if (f.delete()) {
                log.info("Demo file was successfully deleted");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Attachment.builder()
                .fileName(name)
                .fileType(fileType)
                .data(content)
                .build();
    }

    private Map<Long, InfoIndex> initializeIndexesForCalculationWithExpenses() {

        Map<Long, InfoIndex> indexMap = new HashMap<>();

        indexMap.put(0L, InfoIndex.builder().name("Назва статей калькуляції").id(0L).build());
        indexMap.put(1L, InfoIndex.builder().name("Постачальник").id(1L).build());
        indexMap.put(2L, InfoIndex.builder().name("Код товару").id(2L).build());
        indexMap.put(3L, InfoIndex.builder().name("Країна походження").id(3L).build());
        indexMap.put(4L, InfoIndex.builder().name("Кількість").id(4L).build());
        indexMap.put(5L, InfoIndex.builder().name("Од. Виміру").id(5L).build());
        indexMap.put(6L, InfoIndex.builder().name("Ціна, грн.").id(6L).build());
        indexMap.put(7L, InfoIndex.builder().name("Сума, грн.").id(7L).build());
        indexMap.put(8L, InfoIndex.builder().name("Курс євро (НБУ)").id(8L).build());
        indexMap.put(9L, InfoIndex.builder().name("Сума, Євро").id(9L).build());
        indexMap.put(10L, InfoIndex.builder().name("Вартість кінцевого \n  продукту, Євро").id(10L).build());
        indexMap.put(11L, InfoIndex.builder().name("Відсотковий вміст \n у вартості \n кінцевого продукту, %").id(11L).build());
        indexMap.put(12L, InfoIndex.builder().name("Відсотковий вміст \n у вартості \n  кінцевого продукту \n по групам, %").id(12L).build());

        return indexMap;
    }

    private Map<Long, InfoIndex> initializeIndexesForCalculation() {

        Map<Long, InfoIndex> indexMap = new HashMap<>();

        indexMap.put(0L, InfoIndex.builder().name("Artikel \r\n Артикул").id(0L).build());
        indexMap.put(1L, InfoIndex.builder().name("Commodity \n Назва номенклатури").id(1L).build());
        indexMap.put(2L, InfoIndex.builder().name("Постачальник").id(2L).build());
        indexMap.put(3L, InfoIndex.builder().name("Unit of measurement \n Од.вим. ").id(3L).build());
        indexMap.put(4L, InfoIndex.builder().name("Країна походження").id(4L).build());
        indexMap.put(5L, InfoIndex.builder().name("Країна походження (двох симв.код)").id(5L).build());
        indexMap.put(6L, InfoIndex.builder().name("УКТЗЕД").id(6L).build());
        indexMap.put(7L, InfoIndex.builder().name("Інвойс-імпорт (номер)").id(7L).build());
        indexMap.put(8L, InfoIndex.builder().name("Інвойс-імпорт (дата)").id(8L).build());
        indexMap.put(9L, InfoIndex.builder().name("МД-імпорт (номер)").id(9L).build());
        indexMap.put(10L, InfoIndex.builder().name("МД-імпорт (дата)").id(10L).build());
        indexMap.put(11L, InfoIndex.builder().name("МД-імпорт (номер товару)").id(11L).build());
        indexMap.put(12L, InfoIndex.builder().name("Кількість").id(12L).build());
        indexMap.put(13L, InfoIndex.builder().name("Ціна").id(13L).build());
        indexMap.put(14L, InfoIndex.builder().name("Вартість сировини").id(14L).build());
        indexMap.put(15L, InfoIndex.builder().name("Вартість \r сировини \r  непрефер. \r походження").id(15L).build());

        return indexMap;

    }

    private void createExpensesSectionOnTable(Map<String, Double> expenses, int rowNumber, HSSFSheet sheet) {
        var rowNum = new AtomicInteger(rowNumber);

        expenses.forEach((x, y) -> {
            var row = sheet.createRow(rowNum.getAndIncrement());
            switch (x) {
                case "direct costs (materials),UAH" ->
                        row.createCell(0).setCellValue("Прямі витрати (Складові + матеріали)");
                case "general production costs,UAH including:" ->
                        row.createCell(0).setCellValue("Загальновиробничі витрати");
                case "rent of premises" -> row.createCell(0).setCellValue("Оренда приміщень");
                case "Salary" -> row.createCell(0).setCellValue("Заробітна плата");
                case " contributions to social events" -> row.createCell(0).setCellValue("Внесок в публічний простір");
                case "utilities" -> row.createCell(0).setCellValue("Комунальні послуги");
                case "improvement of technology and organization of production" ->
                        row.createCell(0).setCellValue("Вдосконалення технології та організації виробництва");
                case "depreciation of fixed assets " -> row.createCell(0).setCellValue("Амортизація основних засобів");
                case "costs of maintenance of the production process" ->
                        row.createCell(0).setCellValue("Витрати на обслуговування процесу");
                case "labor protection costs, safety" -> row.createCell(0).setCellValue("Витрати на охорону праці");
                default -> row.createCell(0).setCellValue("Повна собівартість " + x);
            }

            row.createCell(1).setCellValue("ОН САЙТ КОМПАНІ");
            row.createCell(3).setCellValue("УКРАЇНА");
            row.createCell(4).setCellValue(1);
            row.createCell(5).setCellValue("шт");
            row.createCell(6).setCellValue(y);
            row.createCell(7).setCellValue(y);
            row.createCell(8).setCellValue(Double.parseDouble(currencyRate));
            row.createCell(9).setCellValue(y / Double.parseDouble(currencyRate));
        });
    }


}
