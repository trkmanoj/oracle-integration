package com.dms.itos3.oracleintegration.controller;

import com.dms.itos3.oracleintegration.serviceimpls.AccArServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class Controller {
    @Autowired
    private AccArServiceImpl accArService;

    @PostMapping("/save")
    public void saveInvoice(){
        accArService.createAr();
    }

    /*@PostMapping("/print")
    public void print() throws IOException {
        accArService.generateExcelSheet();
    }*/
}
