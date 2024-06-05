package com.example.eurcertificatecalc.calculation.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class DocumentOfOrigin {
    private UUID id;
    private Long numberOfDocument;
    private Date date;
}
