package com.dms.itos3.oracleintegration.serviceimpls;

import com.dms.itos3.oracleintegration.entity.*;
import com.dms.itos3.oracleintegration.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
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
                        inv.getDate(),
                        new Date(),
                        inv.getCurrency(),



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

}
