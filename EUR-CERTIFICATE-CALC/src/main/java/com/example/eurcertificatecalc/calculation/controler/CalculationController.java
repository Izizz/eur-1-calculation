package com.example.eurcertificatecalc.calculation.controler;

import com.example.eurcertificatecalc.calculation.model.CalculationResponse;
import com.example.eurcertificatecalc.calculation.service.*;
import com.example.eurcertificatecalc.calculation.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/calc")
@RequiredArgsConstructor
@Slf4j
public class CalculationController {

    private final AttachmentService attachmentService;
    private final ExcelParserService excelParserService;
    private final CalculationService calculationService;
    private final CalculationFileService calculationFileService;
    private final ResponseService responseService;

    @PostMapping("/calculate")
    public List<CalculationResponse> calculate(@RequestParam("file") MultipartFile file,
                                               @RequestParam("expenses file") MultipartFile expensesFile,
                                               @RequestParam("product title") String productTitle) throws Exception {
        TimeUtil.startTimer();

        var expenses = excelParserService.getExpensesForProduct(expensesFile);
        var data = excelParserService.getDataFromExcelTable(file);
        var fullData = calculationService.getFullDataPartMap(data);

        var calculationFileWithExpenses = calculationFileService.createCalculationFileWithAllExpenses(data, expenses, productTitle);
        var calculationFileWithoutExpenses = calculationFileService.createCalculationFileWithoutExpenses(fullData, productTitle);
        var calculationFileDeclaration = calculationFileService.createCalculationDeclarationDocx(data, productTitle);

        TimeUtil.stopTimer();

        log.info(TimeUtil.getTotalTimeForProgram());

        attachmentService.storeFile(calculationFileWithExpenses, Set.of("CALCULATION_WITH_EXPENSES"));
        attachmentService.storeFile(calculationFileWithoutExpenses, Set.of("CALCULATION_WITHOUT_EXPENSES"));
        attachmentService.storeFile(calculationFileDeclaration, Set.of("CALCULATION_DECLARATION"));

        return responseService.response(Set.of(calculationFileWithExpenses, calculationFileWithoutExpenses, calculationFileDeclaration));

    }
}
