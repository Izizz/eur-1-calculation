package com.example.eurcertificatecalc.calculation.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface PdfParserService {

    Set<Map<String, String>> getInvoiceInfoFromDeclaration(MultipartFile file) throws IOException;

    Map<Integer, Boolean> getPreferentialStatusFromPdfInvoice(MultipartFile file) throws Exception;
}
