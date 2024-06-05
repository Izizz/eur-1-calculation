package com.example.eurcertificatecalc.calculation.service;

import com.example.eurcertificatecalc.calculation.data.entity.Attachment;
import com.example.eurcertificatecalc.calculation.model.PartComponent;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public interface CalculationFileService {

    Attachment createCalculationFileWithAllExpenses(Map<Long, PartComponent> data, Map<String, Double> expenses, String productTitle) throws IOException, URISyntaxException, InterruptedException;

    Attachment createCalculationFileWithoutExpenses(Map<Long, PartComponent> data, String productTitle) throws IOException;

    Attachment createCalculationDeclarationDocx(Map<Long, PartComponent> data, String productTitle) throws IOException;

}
