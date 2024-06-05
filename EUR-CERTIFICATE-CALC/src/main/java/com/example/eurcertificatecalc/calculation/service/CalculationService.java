package com.example.eurcertificatecalc.calculation.service;

import com.example.eurcertificatecalc.calculation.data.entity.Attachment;
import com.example.eurcertificatecalc.calculation.model.PartComponent;

import java.util.Map;

public interface CalculationService {

    Map<Long, PartComponent> getFullDataPartMap(Map<Long, PartComponent> data) throws Exception;

    Map<Long, PartComponent> getDeclarationsNumber(Map<Long, PartComponent> data, Attachment incomeFile);

    Map<String, Attachment> getDeclarationFiles(Map<Long, PartComponent> data);

    Map<Long, PartComponent> getNumberFromDeclaration(Map<String, Attachment> declarations, Map<Long, PartComponent> data);

    Map<Long, PartComponent> getInvoiceNumberAndData(Map<Long, PartComponent> dataWithNumber);
}
