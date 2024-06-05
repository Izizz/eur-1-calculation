package com.example.eurcertificatecalc.calculation.model;

import com.example.eurcertificatecalc.calculation.data.entity.Type;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class CalculationResponse {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
    private String declarationNumber;
    private String invoiceDate;
    private String invoiceNumber;
    private Set<Type> types;
}
