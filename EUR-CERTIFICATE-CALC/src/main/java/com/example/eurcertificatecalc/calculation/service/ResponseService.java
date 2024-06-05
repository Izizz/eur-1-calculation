package com.example.eurcertificatecalc.calculation.service;

import com.example.eurcertificatecalc.calculation.data.entity.Attachment;
import com.example.eurcertificatecalc.calculation.model.CalculationResponse;

import java.util.List;
import java.util.Set;

public interface ResponseService {

    List<CalculationResponse> response(Set<Attachment> files);
}
