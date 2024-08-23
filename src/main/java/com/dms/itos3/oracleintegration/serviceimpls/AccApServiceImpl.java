package com.dms.itos3.oracleintegration.serviceimpls;


import com.dms.itos3.oracleintegration.dto.TaxGroupAndIndividualTaxResponseDto;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccApServiceImpl {

    @Autowired
    AccCategoryRepository accCategoryRepository;

    @Autowired
    private AccMarketRepository marketRepository;

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    private AccClusterRepository accClusterRepository;

    @Autowired
    BillVerificationRepository billVerificationRepository;

    @Autowired
    PullTourHeaderDetailsRepository pullTourHeaderDetailsRepository;

    @Autowired
    AccApRepository accApRepository;

    @Autowired
    private final WebClient.Builder webClient;

    @Autowired
    private AccHeaderRepository accHeaderRepository;

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

    public AccApServiceImpl(WebClient.Builder webClient) {
        this.webClient = webClient;
    }

    //@Scheduled(cron = "0 0 18 * * ?")
    @Transactional
    public void createAP() {
        log.info("Start saveAPData method: ");


        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        DateTimeFormatter timeFormatterDateOnly = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDateTime currentTime = LocalDateTime.now();

        List<BillVerification> billVerificationList = billVerificationRepository.findApData();
        System.out.println("print=>"+ billVerificationList);

        List<AccAP> accAPS = billVerificationList.stream().map(billVerification -> {

            if (accApRepository.findTopByOrderByBatchId() != null)
                batchId = accApRepository.findTopByOrderByBatchId().getBatchId() + 1;

            System.out.println("batchID:=>"+ batchId);


            System.out.println("tourId=>"+billVerification.getTourId());
            //getting tourId
            PullTourHeaderDetails pullTourHeaderDetails = getPullTourHeaderDetails(billVerification.getTourId());
            String tourNumber = pullTourHeaderDetails == null ? "" : pullTourHeaderDetails.getTourNo() ;

            //getting supplier details
            System.out.println("supplierid=>"+billVerification.getSupplier().toString());
            Supplier supplier= getSupplier(UUID.fromString(billVerification.getSupplier()));
            String supplierName = supplier != null ? supplier.getSupplierName() : "";
            String supplierCode = supplier != null ? supplier.getAccountCode() : "";
            String bilDate = supplier != null ? supplier.getAccountCode() : "";
            String siteCode = supplier != null ? supplier.getSiteCode() : "";

            //getting category details
            AccCategory accCategory = getCategory(billVerification.getTourId());
            System.out.println("category=>"+accCategory);
            String distributionId = accCategory != null ? accCategory.getDistributionSetId(): "";
            String distributionSetName = accCategory != null ? accCategory.getTourTypeName() : "";
            String tourTypeCode = accCategory != null ? accCategory.getTourType(): "";

            //getting vat/amount details
            Double supplierBillAmount = billVerification.getTotalAmountWithTax() - billVerification.getTaxes().stream().filter(billTaxes -> billTaxes.getTaxCode().equals("VAT18")).mapToDouble(BillTaxes::getTaxAmount).sum();
                    //billVerificationRepository.findSupplierBillAmount(billVerification.getTourId());
            System.out.println("billAmount=>"+ supplierBillAmount);
            Double vatAmount = billVerification.getTaxes().stream().filter(billTaxes -> billTaxes.getTaxCode().equals("VAT18")).mapToDouble(BillTaxes::getTaxAmount).sum();
            System.out.println("vatAmount=>"+ vatAmount);
            String taxCodeId = billVerificationRepository.findTaxCode(billVerification.getBillId().toString());
           // String taxCodeId = billVerification.getTaxes().stream().filter(billTaxes -> billTaxes.getTaxCode().equals("VAT18")).collect(Collectors.toList()).get(0).getTaxCode();
           //System.out.println("taxCode=>"+ taxCodeId);
            TaxGroupAndIndividualTaxResponseDto tax =null;
           if(taxCodeId != null && !taxCodeId.isEmpty()){
               tax = this.getTax(taxCodeId);
           }

           double vatRate;
           if(tax != null){
                vatRate = tax.getTax();
           }else {
                vatRate=0.00;
           }
            //TaxGroupAndIndividualTaxResponseDto tax = this.getTax("a81f22ba-3e6b-4973-b49b-60d697b55118");

            //batch id no idea
            //actual category how to find market id no idea

            return new AccAP(
                    null,
                    "ITOS",
                    batchId,
                    billVerification.getBillId(),
                    supplierName,
                    supplierCode,
                    siteCode, //siteCode===postitos need to add
                    billVerification.getBillDate().toString(), // bill entry date(bill date )
                    currentTime.format(timeFormatterDateOnly), //fill write date
                    generateActualCategoryCode(pullTourHeaderDetails.getTourId(),pullTourHeaderDetails.getMarketId()), //actual category
                    "STANDARD",
                    billVerification.getSupplierBillNo(),  //supplier bill no
                    "LKR",  //supplier bill currency code
                    supplierBillAmount,  //Supplier bill amount (without VAT)
                    "VAT", //
                    vatAmount,  //VAT amount for the supplier bill value
                    vatRate, //VAT Rate
                    "",  //empty
                    0.00,  //empty,
                    0.00,// empty
                    "",  //empty
                    0.00,  //empty
                    0.00,  //empty
                    tourNumber,  //tour number
                    "1",
                    distributionId, //DISTRIBUTION_SET_ID
                    distributionSetName,  //DISTRIBUTION_SET_NAME
                    73,
                    tourNumber, //tour number
                    billVerification.getType(), //Type(ex: Accommodation, Transport, Activity - Group, Activity - Individual.
                    "N/A", //batch number
                    billVerification.getType() == "Transport" ? "N" : "", //The default value is "N" for Transports and keep empty for other types
                    billVerification.getType() == "Transport" ? "External" : "", //The default value is "External" for Transports and keep empty for other types
                    "", //The value is "Vehicle Number" for Transports and keep empty for other types
                    tourTypeCode, //Transfering "tour category code" in previous iTOS, In GEN3.0, we can transfer "tour type code"
                    currentTime.format(timeFormatter), //File write time
                    "",
                    "",
                    "",
                    "",
                    false,
                    null,
                    ""
                    );
        }).collect(Collectors.toList());

        accApRepository.saveAll(accAPS);

        billVerificationRepository.updateBillVerification(true, new Date(),accAPS.stream().map(AccAP::getBillId).collect(Collectors.toList()));

    }

    private TaxGroupAndIndividualTaxResponseDto getTax(String taxCodeId) {
        TaxGroupAndIndividualTaxResponseDto tax = webClient.build()
                .get()
                .uri(financeBaseUrl + "/taxTaxGroupDetails/" + taxCodeId)
                .retrieve()
                .bodyToMono(TaxGroupAndIndividualTaxResponseDto.class)
                .block();

        if (tax == null) {
            log.info("Error occurred while call taxGroup get by id endpoint.");
        }
        return tax;
    }


    //@Scheduled(fixedRate = 20000)
    @Transactional
    public void generateExcelSheet(List<AccAP> accAPList, String type) throws IOException {

        String fileName = "";
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
            cell.setCellValue("SUPPLIER_NAME");
            cell = headerRow.createCell(4);
            cell.setCellValue("SUPP_CODE");
            cell = headerRow.createCell(5);
            cell.setCellValue("SITE_CODE");
            cell = headerRow.createCell(6);
            cell.setCellValue("INV_DATE");
            cell = headerRow.createCell(7);
            cell.setCellValue("GL_DATE");
            cell = headerRow.createCell(8);
            cell.setCellValue("ACTUAL_CATEGORY");
            cell = headerRow.createCell(9);
            cell.setCellValue("INV_TYPE");
            cell = headerRow.createCell(10);
            cell.setCellValue("INV_NO");
            cell = headerRow.createCell(11);
            cell.setCellValue("CURRENCY_CODE");
            cell = headerRow.createCell(12);
            cell.setCellValue("AMOUNT");
            cell = headerRow.createCell(13);
            cell.setCellValue("TAX_CODE_1");
            cell = headerRow.createCell(14);
            cell.setCellValue("TAX_RATE_1");
            cell = headerRow.createCell(15);
            cell.setCellValue("TAX_AMOUNT_1");
            cell = headerRow.createCell(16);
            cell.setCellValue("TAX_CODE_2");
            cell = headerRow.createCell(17);
            cell.setCellValue("TAX_RATE_2");
            cell = headerRow.createCell(18);
            cell.setCellValue("TAX_AMOUNT_2");
            cell = headerRow.createCell(19);
            cell.setCellValue("TAX_CODE_3");
            cell = headerRow.createCell(20);
            cell.setCellValue("TAX_RATE_3");
            cell = headerRow.createCell(21);
            cell.setCellValue("TAX_AMOUNT_3");
            cell = headerRow.createCell(22);
            cell.setCellValue("DESCRIPTION");
            cell = headerRow.createCell(23);
            cell.setCellValue("LEGAL_ENTITY");
            cell = headerRow.createCell(24);
            cell.setCellValue("DISTRIBUTION_SET_ID");
            cell = headerRow.createCell(25);
            cell.setCellValue("DISTRIBUTION_SET_NAME");
            cell = headerRow.createCell(26);
            cell.setCellValue("REQUESTER_ID");
            cell = headerRow.createCell(27);
            cell.setCellValue("ATTRIBUTE1");
            cell = headerRow.createCell(28);
            cell.setCellValue("ATTRIBUTE2");
            cell = headerRow.createCell(29);
            cell.setCellValue("ATTRIBUTE3");
            cell = headerRow.createCell(30);
            cell.setCellValue("ATTRIBUTE4");
            cell = headerRow.createCell(31);
            cell.setCellValue("ATTRIBUTE5");
            cell = headerRow.createCell(32);
            cell.setCellValue("ATTRIBUTE6");
            cell = headerRow.createCell(33);
            cell.setCellValue("ATTRIBUTE7");
            cell = headerRow.createCell(34);
            cell.setCellValue("ATTRIBUTE8");
            cell = headerRow.createCell(35);
            cell.setCellValue("ATTRIBUTE9");
            cell = headerRow.createCell(36);
            cell.setCellValue("ATTRIBUTE10");
            cell = headerRow.createCell(37);
            cell.setCellValue("ATTRIBUTE11");
            cell = headerRow.createCell(38);
            cell.setCellValue("ATTRIBUTE12");

            if (type.equals("Inaccurate")){
                cell = headerRow.createCell(39);
                cell.setCellValue("REMARKS");
            }


            int rowNumber = 1;
            for (AccAP ap : accAPList) {
                Row dataRow = sheet.createRow(rowNumber);
                dataRow.createCell(0).setCellValue(ap.getHeaderId());
                dataRow.createCell(1).setCellValue(ap.getSource());
                dataRow.createCell(2).setCellValue(ap.getBatchId());
                dataRow.createCell(3).setCellValue(ap.getSupplierName());
                dataRow.createCell(4).setCellValue(ap.getSupplierCode());
                dataRow.createCell(5).setCellValue(ap.getSiteCode());
                dataRow.createCell(6).setCellValue(ap.getInvoiceDate());
                dataRow.createCell(7).setCellValue(ap.getGlDate());
                dataRow.createCell(8).setCellValue(ap.getActualCategory());
                dataRow.createCell(9).setCellValue(ap.getInvoiceType());
                dataRow.createCell(10).setCellValue(ap.getInvoiceNo());
                dataRow.createCell(11).setCellValue(ap.getCurrencyCode());
                dataRow.createCell(12).setCellValue(ap.getAmount());
                dataRow.createCell(13).setCellValue(ap.getTaxCode1());
                dataRow.createCell(14).setCellValue(ap.getTaxRate1());
                dataRow.createCell(15).setCellValue(ap.getTaxAmount1());
                dataRow.createCell(16).setCellValue(ap.getTaxCode2());
                dataRow.createCell(17).setCellValue(ap.getTaxRate2());
                dataRow.createCell(18).setCellValue(ap.getTaxAmount2());
                dataRow.createCell(19).setCellValue(ap.getTaxCode3());
                dataRow.createCell(20).setCellValue(ap.getTaxRate3());
                dataRow.createCell(21).setCellValue(ap.getTaxAmount3());
                dataRow.createCell(22).setCellValue(ap.getDescription());
                dataRow.createCell(23).setCellValue(ap.getLegalEntity());
                dataRow.createCell(24).setCellValue(ap.getDestributionSetId());
                dataRow.createCell(25).setCellValue(ap.getDestributionSetName());
                dataRow.createCell(26).setCellValue(ap.getRequestedId());
                dataRow.createCell(27).setCellValue(ap.getAttribute1());
                dataRow.createCell(28).setCellValue(ap.getAttribute2());
                dataRow.createCell(29).setCellValue(ap.getAttribute3());
                dataRow.createCell(30).setCellValue(ap.getAttribute4());
                dataRow.createCell(31).setCellValue(ap.getAttribute5());
                dataRow.createCell(32).setCellValue(ap.getAttribute6());
                dataRow.createCell(33).setCellValue(ap.getAttribute7());
                dataRow.createCell(34).setCellValue(ap.getAttribute8());
                dataRow.createCell(35).setCellValue(ap.getAttribute9());
                dataRow.createCell(36).setCellValue(ap.getAttribute10());
                dataRow.createCell(37).setCellValue(ap.getAttribute11());
                dataRow.createCell(38).setCellValue(ap.getAttribute12());

                if (type.equals("Inaccurate"))
                    dataRow.createCell(39).setCellValue(ap.getRemarks());

                //need to update raw is printed and printed date
                accApRepository.updateAccApDetails(true,LocalDateTime.now(),ap.getHeaderId());

                rowNumber++;

            }

            // Define the file path where the Excel file will be saved
            if (type.equals("Accurate")){
                fileName = formatDateTimeForFileName(LocalDateTime.now()) + "_AP.xlsx";
            }else {
                fileName = formatDateTimeForFileName(LocalDateTime.now()) + "_INACCURATE_AP.xlsx";
            }

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

        syncDocument(fileName);

        if (type.equals("Accurate")){
            accHeaderRepository.save(new AccHeader(
                    null,
                    LocalDateTime.now(),
                    "AP",
                    accAPList.get(0).getBatchId().toString(),
                    accAPList.size(),
                    "ALL", //no clear idea about sub category details
                    accAPList.stream().mapToDouble(AccAP::getAmount).sum(),
                    "",
                    0,
                    "",
                    0,
                    false,
                    null));
        }

   }


    private String generateActualCategoryCode(String tourId,String marketId){
        StringBuilder code = new StringBuilder("01.01.");

        if (findTourCategory(tourId) != null)
            code.append(findTourCategory(tourId).getSegmentValue());

        code.append(".710001.00.");

        if (getMarket(marketId) != null)
            code.append(getMarket(marketId).getSegmentValue());

        code.append(".");

        if (findCluster(marketId) != null)
            code.append(findCluster(marketId).getSegmentValue());

        code.append(".000");

        return code.toString();

    }
    private AccCategory findTourCategory(String tourId){
        PullTourHeaderDetails pullTourHeaderDetails = pullTourHeaderDetailsRepository.findByTourId(tourId);
        if (pullTourHeaderDetails != null)
            return accCategoryRepository.findByTourTypeId(pullTourHeaderDetails.getTourTypeId());

        return null;
    }

    private AccMarket getMarket(String marketId){
        return marketRepository.findByMarketId(marketId);
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

    @Transactional
//@Scheduled(cron = "0 0 19 * * ?")
    public void validateAndPrint() throws IOException {

        List<AccAP> accurateList = new ArrayList<>();
        List<AccAP> inaccurateList = new ArrayList<>();

        List<AccAP> accARList = accApRepository.findByPrinted(false);

        for (AccAP accAP : accARList) {

            StringBuilder errorMsg = new StringBuilder();

            if (accAP.getSupplierName() == null || accAP.getSupplierName().equals(""))
                errorMsg.append("SUPPLIER_NAME IS REQUIRED, ");
            if (accAP.getSupplierCode() == null || accAP.getSupplierCode().equals(""))
                errorMsg.append("SUPP_CODE IS REQUIRED, ");
            if (accAP.getSiteCode() == null || accAP.getSiteCode().equals(""))
                errorMsg.append("SITE_CODE IS REQUIRED, ");
            if (accAP.getInvoiceType() == null || accAP.getInvoiceType().equals(""))
                errorMsg.append("INV_TYPE IS REQUIRED, ");
            if(accAP.getInvoiceNo() == null || accAP.getInvoiceNo().equals(""))
                errorMsg.append("INV_NO IS REQUIRED, ");
            if (accAP.getActualCategory() == null || accAP.getActualCategory().equals("") || accAP.getActualCategory().contains("..")) // check contains if i added some keyword in save method
                errorMsg.append("ACTUAL_CATEGORY IS REQUIRED, ");
            if (accAP.getLegalEntity() == null || accAP.getLegalEntity().equals(""))
                errorMsg.append("LEGAL_ENTITY IS REQUIRED, ");
            if (accAP.getInvoiceDate() == null)
                errorMsg.append("INVOICE_DATE IS REQUIRED, ");
            if (accAP.getGlDate() == null)
                errorMsg.append("GL_DATE IS REQUIRED, ");
            if (accAP.getCurrencyCode() == null || accAP.getCurrencyCode().equals(""))
                errorMsg.append("CURRENCY_CODE IS REQUIRED, ");
            if (accAP.getDestributionSetName() == null || accAP.getDestributionSetName().equals(""))
                errorMsg.append("DISTRIBUTION_SET_NAME IS REQUIRED, ");
            if (accAP.getAttribute1() == null || accAP.getAttribute1().equals(""))
                errorMsg.append("ATTRIBUTE1 IS REQUIRED, ");
            if (accAP.getAttribute2() == null || accAP.getAttribute2().equals(""))
                errorMsg.append("ATTRIBUTE2 IS REQUIRED, ");
            if (accAP.getAttribute3() == null || accAP.getAttribute3().equals(""))
                errorMsg.append("ATTRIBUTE3 IS REQUIRED, ");
            if (accAP.getAttribute4() == null || accAP.getAttribute4().equals(""))
                errorMsg.append("ATTRIBUTE4 IS REQUIRED, ");

            if (errorMsg.length() != 0){
                accAP.setRemarks(errorMsg.toString());
                accApRepository.updateAccApRemarks(accAP.getRemarks(), accAP.getHeaderId());
                inaccurateList.add(accAP);
            } else {
                accurateList.add(accAP);
            }

        }

        if (!accurateList.isEmpty())
            generateExcelSheet(accurateList, "Accurate");
        if (!inaccurateList.isEmpty())
            generateExcelSheet(inaccurateList,"Inaccurate");

       // syncDocument(String fileName);
    }

    private AccCategory getCategory(String categoryId){

        return accCategoryRepository.findByTourTypeId(categoryId);
    }

    private PullTourHeaderDetails getPullTourHeaderDetails(String tourId){
        return pullTourHeaderDetailsRepository.findByTourId(tourId);
    }
    private Supplier getSupplier(UUID supplierId){

        return supplierRepository.findBySupplierId(supplierId);
    }

    public void syncDocument(String fileName){

        String localFilePath = System.getProperty("user.home") + "/Documents/"+fileName;

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, remoteHost, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            File localFile = new File(localFilePath);
            channelSftp.put(new FileInputStream(localFile), remoteFilePath + fileName);

            channelSftp.disconnect();
            session.disconnect();

            log.info("File uploaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printHeader(){

        List<AccHeader> accHeaders = accHeaderRepository.findAll();

        String fileName = "";
        Workbook workbook = null;
        FileOutputStream fileOut = null;
        try {
            // Create a workbook and a sheet
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Sheet");

            // Create a header row
            Row headerRow = sheet.createRow(0);
            Cell cell = headerRow.createCell(0);
            cell.setCellValue("TRANS_DATE");
            cell = headerRow.createCell(1);
            cell.setCellValue("TYPE");
            cell = headerRow.createCell(2);
            cell.setCellValue("BATCH_ID");
            cell = headerRow.createCell(3);
            cell.setCellValue("RECORD_COUNT");
            cell = headerRow.createCell(4);
            cell.setCellValue("SUB_CATEGORY_1");
            cell = headerRow.createCell(5);
            cell.setCellValue("SUB_CATEGORY_1_TOTAL");
            cell = headerRow.createCell(6);
            cell.setCellValue("SUB_CATEGORY_2");
            cell = headerRow.createCell(7);
            cell.setCellValue("SUB_CATEGORY_2_TOTAL");
            cell = headerRow.createCell(8);
            cell.setCellValue("SUB_CATEGORY_3");
            cell = headerRow.createCell(9);
            cell.setCellValue("SUB_CATEGORY_3_TOTAL");


            int rowNumber = 1;
            for (AccHeader ar : accHeaders) {
                Row dataRow = sheet.createRow(rowNumber);
                dataRow.createCell(0).setCellValue(ar.getTransDate());
                dataRow.createCell(1).setCellValue(ar.getType());
                dataRow.createCell(2).setCellValue(ar.getBatchId());
                dataRow.createCell(3).setCellValue(ar.getRecordCount());
                dataRow.createCell(4).setCellValue(ar.getSubCategory1());
                dataRow.createCell(5).setCellValue(ar.getSubCategory1Total());
                dataRow.createCell(6).setCellValue("");
                dataRow.createCell(7).setCellValue(0);
                dataRow.createCell(8).setCellValue("");
                dataRow.createCell(9).setCellValue(0);

                accHeaderRepository.updateAccHeaderDetails(true,LocalDateTime.now(),ar.getHeaderId());

                rowNumber++;

            }

            // Define the file path where the Excel file will be saved
            fileName = formatDateTimeForFileName(LocalDateTime.now()) + "_HEADER.xlsx";

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
        syncDocument(fileName);
    }

    public static String formatDateTimeForFileName(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy HHmm");
        return date.format(formatter);
    }
}
