package com.example.eurcertificatecalc.calculation.service.impl;

import com.example.eurcertificatecalc.calculation.data.entity.Attachment;
import com.example.eurcertificatecalc.calculation.model.CalculationResponse;
import com.example.eurcertificatecalc.calculation.service.ResponseService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ResponseServiceImpl implements ResponseService {

    @Override
    public List<CalculationResponse> response(Set<Attachment> files) {
        List<CalculationResponse> response = new ArrayList<>();
        files.forEach(file -> {
                    var downloadUrl = createDownloadUrl(file);
                    response.add(
                            CalculationResponse.builder()
                                    .fileName(file.getFileName())
                                    .size(file.getSize())
                                    .fileType(file.getContentType())
                                    .fileDownloadUri(downloadUrl)
                                    .build());
                }
        );

        return response;
    }

    private String createDownloadUrl(Attachment file) {

        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("api/v1/files/download/")
                .path(String.valueOf(file.getFileName())
                ).toUriString();
    }
}
