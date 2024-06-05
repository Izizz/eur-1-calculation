package com.example.eurcertificatecalc.calculation.service.impl;


import com.example.eurcertificatecalc.calculation.service.PdfParserService;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Slf4j
public class PdfParserServiceImpl implements PdfParserService {

    @Override
    public Set<Map<String, String>> getInvoiceInfoFromDeclaration(MultipartFile file) throws IOException {


        var parser = new PDFParser(new RandomAccessBufferedFileInputStream(file.getInputStream()));
        parser.parse();
        var cosDoc = parser.getDocument();


        var stripper = new PDFTextStripper();
        var text = stripper.getText(new PDDocument(cosDoc));

        // Define a regular expression pattern to search for invoice information
        var pattern = Pattern.compile("0380[* ]\\s*(\\d{1,6})\\s*(\\d{2}\\.\\d{2}\\.\\d{2,4})");

        // Create a matcher object to search for the pattern in the extracted text
        var matcher = pattern.matcher(text);

        // Create a set to store the invoice information found in the file
        Set<Map<String, String>> matches = new HashSet<>();

        // Iterate over all matches found by the matcher and add them to the set
        while (matcher.find()) {

            var number = matcher.group(1);
            var date = matcher.group(2);

            // Create a new map containing the invoice number and date and add it to the set
            Map<String, String> match = new HashMap<>();
            match.put("number", number);
            match.put("date", date);

            matches.add(match);
        }

        // Close the parsed document and return the set of matches
        cosDoc.close();

        return matches;
    }

    @Override
    public Map<Integer, Boolean> getPreferentialStatusFromPdfInvoice(MultipartFile file) {


        Map<Integer, Boolean> preferentialOfGroupMap = new LinkedHashMap<>();

        try {

            var inputStream = file.getInputStream();
            var reader = new PdfReader(inputStream);
            int pages = reader.getNumberOfPages();

            // Extract text from each page of the PDF
            var result = new StringBuilder();
            for (int i = 1; i <= pages; i++) {
                result.append(PdfTextExtractor.getTextFromPage(reader, i));
            }

            reader.close();
            var res = result.toString();

            // Split the extracted text into sections based on a regular expression
            var regex = "1\\.\\s?[а-щА-ЩЬьЮюЯяЇїІіЄєҐґ]";
            var arr = res.split(regex);

            // Extract the number of goods and check for preferential status in each section
            for (String a : arr) {

                var pattern = Pattern.compile("товарів\\s+(\\d{1,3})");
                var matcher = pattern.matcher(a);

                if (matcher.find()) {

                    var numberString = matcher.group(1);
                    int number = Integer.parseInt(numberString);

                    if (a.contains("410000000")) {
                        preferentialOfGroupMap.put(number, true);
                    } else {
                        preferentialOfGroupMap.put(number, false);
                    }
                }
            }
        } catch (Exception ignored) {}

        return preferentialOfGroupMap;
    }
}