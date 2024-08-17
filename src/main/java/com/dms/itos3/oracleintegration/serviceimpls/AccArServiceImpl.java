package com.dms.itos3.oracleintegration.serviceimpls;

import com.dms.itos3.oracleintegration.entity.*;
import com.dms.itos3.oracleintegration.repository.*;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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

    @Autowired
    private AccSalesPersonRepository accSalesPersonRepository;

    @Autowired
    private AccHeaderRepository accHeaderRepository;

    @Autowired
    private AccClusterRepository accClusterRepository;

    private final WebClient.Builder webClient;

    @Value("${finance.base.url}")
    private String financeBaseUrl;

    @Value("${started.batchid}")
    private Long batchId;

    @Value("${remote.host}")
    private String remoteHost;

    @Value("${host.port}")
    private int port;

    @Value("${host.username}")
    private String username;

    @Value("${host.pwd}")
    private String password;

    @Value("${remote.file.path}")
    private String remoteFilePath;


    public AccArServiceImpl(WebClient.Builder webClient) {
        this.webClient = webClient;
    }


    //set scheduler
    @Transactional
    public void createAr(){

        List<InvoiceVerification> invoiceVerificationList = invoiceVerificationRepository.findByArStatus(false);

        if (accArRepository.findTopByOrderByBatchId() != null)
            batchId = accArRepository.findTopByOrderByBatchId().getBatchId() + 1;

        /*List<InvoiceVerification> testInvoiceVerificationList = new ArrayList<>();
        testInvoiceVerificationList.add(invoiceVerificationList.get(0));
        testInvoiceVerificationList.add(invoiceVerificationList.get(1));*/


        List<AccAR> accARS = invoiceVerificationList.stream()
                .map(inv -> new AccAR(
                        null,
                        inv.getId(),
                        "ITOS",
                        batchId, //batch id should be the same value for bulk of records
                        inv.getType().equals("Invoice") || inv.getType().equals("Supplementary") ? "INV" : "CM",
                        getAccCategory(inv.getTourHeaderID(),inv.getType()),
                        generateActualCategoryCode(inv.getTourHeaderID(),inv.getMarketId()),// actual-categoty (combination of tble values)
                        "I",
                        stringDateConvertToLocalDate(inv.getDate()),
                        LocalDate.now(),
                        inv.getCurrency(), // LKR or itos-side invoice currency
                        getOperator(inv.getOperatorId()) == null ? null : getOperator(inv.getOperatorId()).getAccLink2(),  // customer code
                        getOperator(inv.getOperatorId()) == null ? null : getOperator(inv.getOperatorId()).getAccLink1(), // customer site
                        findUser(inv.getTourHeaderID()), // sales person
                        1,
                        inv.getInvoiceWithoutTax(),
                        "VAT",
                        inv.getInvoiceTax(),
                        18, //vat rate
                        "",
                        0,
                        0,
                        "",
                        0,
                        0,
                        inv.getType().equals("Invoice") || inv.getType().equals("Supplementary") ? "INV RF24INV00048" : "CRN RF24INV00048",
                        "", // invoice description
                        findTourDetails(inv.getTourHeaderID()) == null ? null : findTourDetails(inv.getTourHeaderID()).getTourNo(),
                        findTourDetails(inv.getTourHeaderID()) == null ? null : findTourDetails(inv.getTourHeaderID()).getTourName(),
                        findTourDetails(inv.getTourHeaderID()) == null ? null : findTourCategory(inv.getTourHeaderID()).getTourType(),
                        formatDateTime(LocalDateTime.now()),
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        false,
                        null,
                        ""

                )).collect(Collectors.toList());

        System.out.println(accARS.get(0).getBatchId());

        accArRepository.saveAll(accARS);

        invoiceVerificationRepository.updateInvoiceVerification(true, new Date(),accARS.stream().map(AccAR::getInvoiceId).collect(Collectors.toList()));

    }

    //@Scheduled(fixedRate = 20000)
    @Transactional
    public void generateExcelSheet(List<AccAR> accARList, String type) throws IOException {

        Workbook workbook = null;
        FileOutputStream fileOut = null;
        try {
            // Create a workbook and a sheet
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(type + " Sheet");

            // Create a header row
            Row headerRow = sheet.createRow(0);
            Cell cell = headerRow.createCell(0);
            cell.setCellValue("HEADER_ID");
            cell = headerRow.createCell(1);
            cell.setCellValue("SOURCE");
            cell = headerRow.createCell(2);
            cell.setCellValue("BATCH_ID");
            cell = headerRow.createCell(3);
            cell.setCellValue("INVOICE_CLASS");
            cell = headerRow.createCell(4);
            cell.setCellValue("TRN_TYPE");
            cell = headerRow.createCell(5);
            cell.setCellValue("ACTUAL_CATEGORY");
            cell = headerRow.createCell(6);
            cell.setCellValue("LEGAL_ENTITY");
            cell = headerRow.createCell(7);
            cell.setCellValue("INVOICE_DATE");
            cell = headerRow.createCell(8);
            cell.setCellValue("GL_DATE");
            cell = headerRow.createCell(9);
            cell.setCellValue("CURRENCY");
            cell = headerRow.createCell(10);
            cell.setCellValue("CUSTOMER_CODE");
            cell = headerRow.createCell(11);
            cell.setCellValue("CUSTOMER_SITE");
            cell = headerRow.createCell(12);
            cell.setCellValue("SALES_PERSON");
            cell = headerRow.createCell(13);
            cell.setCellValue("QUANTITY");
            cell = headerRow.createCell(14);
            cell.setCellValue("INVOICE_AMOUNT");
            cell = headerRow.createCell(15);
            cell.setCellValue("TAX_CODE_1");
            cell = headerRow.createCell(16);
            cell.setCellValue("TAX_AMOUNT_1");
            cell = headerRow.createCell(17);
            cell.setCellValue("TAX_RATE_1");
            cell = headerRow.createCell(18);
            cell.setCellValue("TAX_CODE_2");
            cell = headerRow.createCell(19);
            cell.setCellValue("TAX_AMOUNT_2");
            cell = headerRow.createCell(20);
            cell.setCellValue("TAX_RATE_2");
            cell = headerRow.createCell(21);
            cell.setCellValue("TAX_CODE_3");
            cell = headerRow.createCell(22);
            cell.setCellValue("TAX_AMOUNT_3");
            cell = headerRow.createCell(23);
            cell.setCellValue("TAX_RATE_3");
            cell = headerRow.createCell(24);
            cell.setCellValue("INVOICE_NO");
            cell = headerRow.createCell(25);
            cell.setCellValue("DESCRIPTION");
            cell = headerRow.createCell(26);
            cell.setCellValue("ATTRIBUTE1");
            cell = headerRow.createCell(27);
            cell.setCellValue("ATTRIBUTE2");
            cell = headerRow.createCell(28);
            cell.setCellValue("ATTRIBUTE3");
            cell = headerRow.createCell(29);
            cell.setCellValue("ATTRIBUTE4");
            cell = headerRow.createCell(30);
            cell.setCellValue("ATTRIBUTE5");
            cell = headerRow.createCell(31);
            cell.setCellValue("ATTRIBUTE6");
            cell = headerRow.createCell(32);
            cell.setCellValue("ATTRIBUTE7");
            cell = headerRow.createCell(33);
            cell.setCellValue("ATTRIBUTE8");
            cell = headerRow.createCell(34);
            cell.setCellValue("ATTRIBUTE9");
            cell = headerRow.createCell(35);
            cell.setCellValue("ATTRIBUTE10");
            cell = headerRow.createCell(36);
            cell.setCellValue("ATTRIBUTE11");
            cell = headerRow.createCell(37);
            cell.setCellValue("ATTRIBUTE12");


            int rowNumber = 1;
            for (AccAR ar : accARList) {
                Row dataRow = sheet.createRow(rowNumber);
                dataRow.createCell(0).setCellValue(ar.getHeaderId());
                dataRow.createCell(1).setCellValue(ar.getSource());
                dataRow.createCell(2).setCellValue(ar.getBatchId());
                dataRow.createCell(3).setCellValue(ar.getInvoiceClass());
                dataRow.createCell(4).setCellValue(ar.getTrnType());
                dataRow.createCell(5).setCellValue(ar.getActualCategory());
                dataRow.createCell(6).setCellValue(ar.getLegalEntity());
                dataRow.createCell(7).setCellValue(formatDateToString(ar.getInvoiceDate()));
                dataRow.createCell(8).setCellValue(formatDateToString(ar.getGlDate()));
                dataRow.createCell(9).setCellValue(ar.getCurrency());
                dataRow.createCell(10).setCellValue(ar.getCustomerCode());
                dataRow.createCell(11).setCellValue(ar.getCustomerSite());
                dataRow.createCell(12).setCellValue(ar.getSalesPerson());
                dataRow.createCell(13).setCellValue(ar.getQuantity());
                dataRow.createCell(14).setCellValue(ar.getInvoiceAmount());
                dataRow.createCell(15).setCellValue(ar.getTaxCode1());
                dataRow.createCell(16).setCellValue(ar.getTaxAmount1());
                dataRow.createCell(17).setCellValue(ar.getTaxRate1());
                dataRow.createCell(18).setCellValue(ar.getTaxCode2());
                dataRow.createCell(19).setCellValue(ar.getTaxAmount2());
                dataRow.createCell(20).setCellValue(ar.getTaxRate2());
                dataRow.createCell(21).setCellValue(ar.getTaxCode3());
                dataRow.createCell(22).setCellValue(ar.getTaxAmount3());
                dataRow.createCell(23).setCellValue(ar.getTaxRate3());
                dataRow.createCell(24).setCellValue(ar.getInvoiceNo());
                dataRow.createCell(25).setCellValue(ar.getDescription());
                dataRow.createCell(26).setCellValue(ar.getAttribute1());
                dataRow.createCell(27).setCellValue(ar.getAttribute2());
                dataRow.createCell(28).setCellValue(ar.getAttribute3());
                dataRow.createCell(29).setCellValue(ar.getAttribute4());
                dataRow.createCell(30).setCellValue(ar.getAttribute5());
                dataRow.createCell(31).setCellValue(ar.getAttribute6());
                dataRow.createCell(32).setCellValue(ar.getAttribute7());
                dataRow.createCell(33).setCellValue(ar.getAttribute8());
                dataRow.createCell(34).setCellValue(ar.getAttribute9());
                dataRow.createCell(35).setCellValue(ar.getAttribute10());
                dataRow.createCell(36).setCellValue(ar.getAttribute11());
                dataRow.createCell(37).setCellValue(ar.getAttribute12());

                //need to update raw is printed and printed date
                accArRepository.updateAccArDetails(true,LocalDateTime.now(),ar.getHeaderId());

                rowNumber++;

            }

            // Define the file path where the Excel file will be saved
            String fileName = type + "document.xlsx";
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

        if (type.equals("Accurate")){
            accHeaderRepository.save(new AccHeader(
                    null,
                    LocalDateTime.now(),
                    "AR",
                    accARList.get(0).getBatchId().toString(),
                    accARList.size(),
                    null, //no clear idea about sub category details
                    accARList.stream().mapToDouble(AccAR::getInvoiceAmount).sum(),
                    null,
                    0,
                    null,
                    0));
        }

    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void validateAndPrint() throws IOException {

        List<AccAR> accurateList = new ArrayList<>();
        List<AccAR> inaccurateList = new ArrayList<>();

        List<AccAR> accARList = accArRepository.findByPrinted(false);

        for (AccAR accAR : accARList) {

            StringBuilder errorMsg = new StringBuilder();

            if (accAR.getTrnType() == null || accAR.getTrnType().equals(""))
                errorMsg.append("TRN_TYPE, ");
            if (accAR.getActualCategory() == null || accAR.getActualCategory().equals("") || accAR.getActualCategory().contains("..")) // check contains if i added some keyword in save method
                errorMsg.append("ACTUAL_CATEGORY, ");
            if (accAR.getLegalEntity() == null || accAR.getLegalEntity().equals(""))
                errorMsg.append("LEGAL_ENTITY, ");
            if (accAR.getInvoiceDate() == null)
                errorMsg.append("INVOICE_DATE, ");
            if (accAR.getGlDate() == null)
                errorMsg.append("GL_DATE, ");
            if (accAR.getCurrency() == null || accAR.getCurrency().equals(""))
                errorMsg.append("CURRENCY, ");
            if (accAR.getCustomerCode() == 0)
                errorMsg.append("CUSTOMER_CODE, ");
            if (accAR.getCustomerSite() == 0)
                errorMsg.append("CUSTOMER_SITE, ");
            if (accAR.getSalesPerson() == null || accAR.getSalesPerson().equals(""))
                errorMsg.append("SALES_PERSON, ");
            if (accAR.getInvoiceNo() == null || accAR.getInvoiceNo().equals(""))
                errorMsg.append("SALES_PERSON, ");
            if (accAR.getAttribute1() == null || accAR.getAttribute1().equals(""))
                errorMsg.append("ATTRIBUTE1, ");
            if (accAR.getAttribute2() == null || accAR.getAttribute2().equals(""))
                errorMsg.append("ATTRIBUTE2, ");
            if (accAR.getAttribute3() == null || accAR.getAttribute3().equals(""))
                errorMsg.append("ATTRIBUTE3, ");
            if (accAR.getAttribute4() == null || accAR.getAttribute4().equals(""))
                errorMsg.append("ATTRIBUTE4, ");

            if (errorMsg.length() != 0){
                accAR.setRemarks(errorMsg.toString());
                accArRepository.updateAccArRemarks(accAR.getRemarks(),accAR.getHeaderId());
                inaccurateList.add(accAR);
            } else {
                accurateList.add(accAR);
            }

        }
        generateExcelSheet(accurateList, "Accurate");
        generateExcelSheet(inaccurateList,"Inaccurate");

        syncDocument();
    }

    private String generateActualCategoryCode(String tourId,String marketId){
        StringBuilder code = new StringBuilder("01.01.");

        if (findTourCategory(tourId) != null)
            code.append(findTourCategory(tourId).getSegmentValue());

        code.append(".610001.00.");

        if (getMarket(marketId) != null)
            code.append(getMarket(marketId).getSegmentValue());

        code.append(".");

        if (findCluster(marketId) != null)
            code.append(findCluster(marketId).getSegmentValue());

        code.append(".000");

        return code.toString();

    }

    private String getAccCategory(String tourId, String invType){
        PullTourHeaderDetails pullTourHeaderDetails = pullTourHeaderDetailsRepository.findByTourId(tourId);
        String category = "";
        if(pullTourHeaderDetails != null){
            if (invType.equals("Invoice")){
                category = accCategoryRepository.findByTourTypeId(pullTourHeaderDetails.getTourTypeId()).getAccLinkInv();
            }
            if (invType.equals("Credit_Note")){
                category = accCategoryRepository.findByTourTypeId(pullTourHeaderDetails.getTourTypeId()).getAccLinkCm();
            }
        }
        return category;
    }

    private AccMarket getMarket(String marketId){
        return marketRepository.findByMarketId(marketId);
    }

    private AccOperator getOperator(String operatorId){

        String agentId = webClient.build()
                .get()
                .uri(financeBaseUrl + "/agentByOperator/" + operatorId)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return operatorRepository.findByAgentId(agentId);
    }

    private PullTourHeaderDetails findTourDetails(String tourId){
        return pullTourHeaderDetailsRepository.findByTourId(tourId);
    }

    private String findUser(String tourId){
        PullTourHeaderDetails pullTourHeaderDetails = findTourDetails(tourId);
        if (pullTourHeaderDetails != null)
            return accSalesPersonRepository.findByUserName(findTourDetails(tourId).getCreateBy()).getAccountLink();

        return null;
    }

    private AccCategory findTourCategory(String tourId){
        PullTourHeaderDetails pullTourHeaderDetails = pullTourHeaderDetailsRepository.findByTourId(tourId);
        if (pullTourHeaderDetails != null)
            return accCategoryRepository.findByTourTypeId(pullTourHeaderDetails.getTourTypeId());

        return null;
    }

    private AccCluster findCluster(String marketId){


        String clusterId = webClient.build()
                .get()
                .uri(financeBaseUrl + "/market/clusterIdByMarket/" + marketId)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return accClusterRepository.findByClusterId(clusterId);
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

    public static String formatDateToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    public LocalDate stringDateConvertToLocalDate(String dateStr){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDate localDate = LocalDate.parse(dateStr, formatter);
        return localDate;
    }

    public static String formatDateTime(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy_HH:mm:ss");
        return date.format(formatter);
    }

    public void syncDocument(){

        String localFilePath = System.getProperty("user.home") + "/Documents/Accuratedocument.xlsx";

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, remoteHost, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            File localFile = new File(localFilePath);
            channelSftp.put(new FileInputStream(localFile), remoteFilePath + "file.xlsx");

            channelSftp.disconnect();
            session.disconnect();

            System.out.println("File uploaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
