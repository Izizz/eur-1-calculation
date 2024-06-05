package com.example.eurcertificatecalc.calculation.service.impl;

import com.example.eurcertificatecalc.calculation.data.entity.Attachment;
import com.example.eurcertificatecalc.calculation.model.PartComponent;
import com.example.eurcertificatecalc.calculation.service.AttachmentService;
import com.example.eurcertificatecalc.calculation.service.CalculationService;
import com.example.eurcertificatecalc.calculation.service.ExcelParserService;
import com.example.eurcertificatecalc.calculation.service.PdfParserService;
import com.example.eurcertificatecalc.calculation.util.CurrencyUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@Data
public class CalculationServiceImpl implements CalculationService {

    protected static String currencyRate = "";
    protected static double nonPreferentialCost = 0.00;
    protected static double preferentialCost = 0.00;
    protected static double preferentialCostEuro = 0.00;
    protected static double nonPreferentialCostEuro = 0.00;
    protected static double priceProduct = 0.00;
    protected static double priceProductEuro = 0.00;
    protected static double preferentialPercentage = 0.00;
    protected static double nonPreferentialPercentage = 0.00;
    private final ExcelParserService excelParserService;
    private final AttachmentService attachmentService;
    private final PdfParserService pdfParserService;
    private final CurrencyUtil currencyRateService;
    private final Set<String> missingFiles = new HashSet<>();

    @Override
    public Map<Long, PartComponent> getFullDataPartMap(Map<Long, PartComponent> data) {
        var inComeFile = attachmentService.getIncomeFile();
        var dataWithDeclarations = getDeclarationsNumber(data, inComeFile);
        var declarations = getDeclarationFiles(dataWithDeclarations);
        var dataWithNumber = getNumberFromDeclaration(declarations, dataWithDeclarations);

        return getInvoiceNumberAndData(dataWithNumber);
    }

    @Override
    public Map<Long, PartComponent> getDeclarationsNumber(Map<Long, PartComponent> data, Attachment incomeFile) {
        data.forEach((x, y) -> {
            try {
                if (y.getDocumentOfOrigin() != null) {
                    var declarationNumber = excelParserService
                            .getNumberOfDeclarationFromIncomeFile(incomeFile, y.getDocumentOfOrigin());
                    if (!declarationNumber.isEmpty()) {
                        y.setDeclarationNumber(declarationNumber);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (!excelParserService.getMissingDeclarationsForIncomeId().isEmpty()) {
            log.error("Missing declaration for income with id " + excelParserService.getMissingDeclarationsForIncomeId());
        }

        return data;
    }

    @Override
    public Map<String, Attachment> getDeclarationFiles(Map<Long, PartComponent> data) {
        Map<String, Attachment> files = new HashMap<>();
        data.forEach((x, y) -> {
            if (y.getDeclarationNumber() != null) {
                try {
                    Attachment file;
                    if (files.get(y.getDeclarationNumber()) == null) {
                        file = attachmentService.getAttachment(y.getDeclarationNumber() + ".xlsx");
                    } else {
                        file = files.get(y.getDeclarationNumber());
                    }
                    files.put(y.getDeclarationNumber(), file);

                } catch (Exception e) {
                    missingFiles.add(y.getDeclarationNumber() + ".xlsx");
                }
            }
        });
        if (!missingFiles.isEmpty()) {
            log.error("Files that was not found in database " + missingFiles);
        }

        return files;
    }

    @Override
    public Map<Long, PartComponent> getNumberFromDeclaration(Map<String, Attachment> declarations, Map<Long, PartComponent> data) {
        data.forEach((x, y) -> {
            if (y.getDeclarationNumber() != null) {
                try {
                    var number = excelParserService
                            .getNumberOfGroupInDeclaration(
                                    declarations.get(y.getDeclarationNumber()), y.getProductArticle()
                            );
                    y.setNumberOfGroupInCustomsDeclaration(number);
                } catch (Exception ignored) {}
            }
        });

        return data;
    }

    @Override
    public Map<Long, PartComponent> getInvoiceNumberAndData(Map<Long, PartComponent> dataWithNumber) {
        var declarations = attachmentService.getDeclarations(PageRequest.of(0, 1000));
        Map<String, Set<Map<String, String>>> declarationInvoicePair = new HashMap<>();
        declarations.forEach(declaration -> {
            try {
                var invoiceInfo = pdfParserService.getInvoiceInfoFromDeclaration(declaration);
                declarationInvoicePair.put(declaration.getFileName().replaceAll(".pdf", ""), invoiceInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        dataWithNumber.forEach((x, y) -> declarationInvoicePair.forEach((declaration, invoiceInfo) -> {
            if (y.getDeclarationNumber().equals(declaration)) {
                for (Map<String, String> invoiceValues : invoiceInfo) {
                    y.setInvoiceNumber(invoiceValues.get("number"));
                    try {
                        y.setInvoiceDate(new SimpleDateFormat("dd.MM.yyyy").parse(invoiceValues.get("date")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }));
        return dataWithNumber;
    }
}