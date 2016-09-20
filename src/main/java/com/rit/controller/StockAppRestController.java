package com.rit.controller;

import com.rit.model.Input;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.rit.model.Stock;
import com.rit.service.StockService;

@RestController
public class StockAppRestController {

    @Autowired
    StockService stockService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public ResponseEntity<List<Stock>> index() {
        return new ResponseEntity("", HttpStatus.OK);
    }

    @RequestMapping(value = "/index/stocks", method = RequestMethod.GET)
    public ResponseEntity<List<Stock>> listOfAllStocks() {
        List<Stock> stocks = stockService.findAllStocks();
        if (stocks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(stocks, HttpStatus.OK);
    }

    @RequestMapping(value = "/index/", method = RequestMethod.POST)
    public ResponseEntity<Void> findStocks(@RequestBody Input input, UriComponentsBuilder ucBuilder) {

        stockService.getStocksForFinancialAdvice(input.getLocation(), input.getIndustry());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/index").build().toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
}
