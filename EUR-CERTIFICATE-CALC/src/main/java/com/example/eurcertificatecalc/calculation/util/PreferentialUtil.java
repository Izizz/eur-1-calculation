package com.example.eurcertificatecalc.calculation.util;


import com.example.eurcertificatecalc.calculation.data.entity.Attachment;
import com.example.eurcertificatecalc.calculation.model.PartComponent;
import com.example.eurcertificatecalc.calculation.service.AttachmentService;
import com.example.eurcertificatecalc.calculation.service.PdfParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreferentialUtil {
    private final PdfParserService pdfParserService;
    private final AttachmentService attachmentService;

    public Boolean checkIfPartIsPreferential(PartComponent part) throws Exception {

        // If the country of origin is Ukraine or China, the part is not preferential
        if (part.getCountryOrigin().equalsIgnoreCase("україна") || part.getCountryOrigin().equalsIgnoreCase("китай"))  return false;

        var declarationNumber = part.getDeclarationNumber();
        Attachment declarationFile = null;

        try {
            declarationFile = attachmentService.getAttachment(declarationNumber + ".pdf");
            if (declarationFile == null) {
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        // Get the preferential status from the PDF file using the PdfParserService
        var preferentialMap = pdfParserService.getPreferentialStatusFromPdfInvoice(declarationFile);
        var numberOfGroup = part.getNumberOfGroupInCustomsDeclaration();
        var preferentialStatus = preferentialMap.get(numberOfGroup);

        // If the preferential status is not null, return it, otherwise return false
        return preferentialStatus != null ? preferentialStatus : false;

    }
}