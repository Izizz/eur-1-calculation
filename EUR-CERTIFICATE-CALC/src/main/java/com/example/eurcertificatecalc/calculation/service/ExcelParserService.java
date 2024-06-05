package com.example.eurcertificatecalc.calculation.service;


import com.example.eurcertificatecalc.calculation.model.DocumentOfOrigin;
import com.example.eurcertificatecalc.calculation.model.PartComponent;
import com.example.eurcertificatecalc.calculation.model.ValidationResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Set;

public interface ExcelParserService {

    Map<Long, PartComponent> getDataFromExcelTable(MultipartFile file) throws IOException;

    String getNumberOfDeclarationFromIncomeFile(MultipartFile file, DocumentOfOrigin documentOfOrigin) throws IOException, ParseException;

    Integer getNumberOfGroupInDeclaration(MultipartFile declaration, String article) throws IOException;

    Set<String> getMissingDeclarationsForIncomeId();

    Map<String, Double> getExpensesForProduct(MultipartFile file) throws IOException;

    ValidationResult validateExcelFileBillOfMaterials(MultipartFile file) throws IOException;

    ValidationResult validateExcelFileExpenses(MultipartFile file) throws IOException;
}
