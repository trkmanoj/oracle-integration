package com.dms.itos3.oracleintegration.serviceimpls;

import com.dms.itos3.oracleintegration.entity.*;
import com.dms.itos3.oracleintegration.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccArServiceImpl {

    @Autowired
    private AccArRepository accArRepository;

    @Autowired
    private InvoiceVerificationRepository invoiceVerificationRepository;

    @Autowired
    private PullTourHeaderDetailsRepository pullTourHeaderDetailsRepository;

    @Autowired
    private AccCategoryRepository accCategoryRepository;

    @Autowired
    private AccMarketRepository marketRepository;

    @Autowired
    private AccOperatorRepository operatorRepository;

    private void createAr(){

        List<InvoiceVerification> invoiceVerificationList = invoiceVerificationRepository.findByArStatus(false);

        /*List<AccAR> accARS = invoiceVerificationList.stream()
                .map(inv -> new AccAR(
                        null,
                        "ITOS",
                        null,
                        inv.getType().equals("Invoice") || inv.getType().equals("Supplementary") ? "INV" : "CM",
                        getAccCategory(inv.getTourHeaderID(),inv.getType()),
                        null,  // actual-categoty (combination of tble values)
                        "I",
                        convertStringToDate(inv.getDate()),
                        formatDate(LocalDate.now()),
                        inv.getCurrency(),
                        getOperator(inv.getOperatorId()).getAccLink2(),  // customer code
                        getOperator(inv.getOperatorId()).getAccLink1(), // customer site
                        null, // sales person


                )).collect(Collectors.toList());*/

    }

    private String getAccCategory(String tourId, String invType){
        PullTourHeaderDetails pullTourHeaderDetails = pullTourHeaderDetailsRepository.findByTourId(tourId);
        String category = "";
        if (invType.equals("Invoice")){
            category = accCategoryRepository.findByTourTypeId(pullTourHeaderDetails.getTourTypeId()).getAccLinkInv();
        }
        if (invType.equals("Credit_Note")){
            category = accCategoryRepository.findByTourTypeId(pullTourHeaderDetails.getTourTypeId()).getAccLinkCm();
        }
        return category;
    }

    private AccMarket getMarket(String marketId){
        return marketRepository.findByMarketId(marketId);
    }

    private AccOperator getOperator(String operatorId){
        return operatorRepository.findByAgentId(operatorId);
    }

    public static LocalDate convertStringToDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate date = LocalDate.parse(dateStr, formatter);
            return date;
        } catch (DateTimeParseException e) {
            log.error("Invalid date format: " + e.getMessage());
            return null;
        }
    }

    public static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    @Scheduled(fixedRate = 20000)
    private void generateExcelSheet() throws IOException {

        Workbook workbook = null;
        FileOutputStream fileOut = null;
        try {
            // Create a workbook and a sheet
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Sample Sheet");

            // Create a header row
            Row headerRow = sheet.createRow(0);
            Cell cell = headerRow.createCell(0);
            cell.setCellValue("ID");
            cell = headerRow.createCell(1);
            cell.setCellValue("Name");

            // Create a data row
            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue(1);
            dataRow.createCell(1).setCellValue("John Doe");

            // Define the file path where the Excel file will be saved
            String fileName = "sample.xlsx";
            Path filePath = Paths.get(System.getProperty("user.home"), "Documents", fileName);

            // Write the output to a file
            fileOut = new FileOutputStream(filePath.toFile());
            workbook.write(fileOut);
            log.info("Excel file created successfully at " + filePath.toString());;

            // Return the path of the created file
            //return filePath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            //return null;
        } finally {
            try {
                // Ensure that the workbook and file output stream are closed
                if (workbook != null) {
                    workbook.close();
                }
                if (fileOut != null) {
                    fileOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
