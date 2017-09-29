package edu.rit.controller;

import edu.rit.model.Input;
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

import edu.rit.model.Stock;
import edu.rit.service.StockService;

/**
 * StockAppRestController handles all application requests, calls the service,
 * performs respective operations, and returns respective views.
 *
 * @author Harshit
 */
@RestController
public class StockAppRestController {

    /**
     * Tells the application context to inject an instance of StockService here.
     */
    @Autowired
    StockService stockService;

    /**
     * Receive GET request '/index'.
     *
     * @return empty body in the response
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public ResponseEntity<List<Stock>> index() {
        return new ResponseEntity("", HttpStatus.OK);
    }

    /**
     * Receive GET request '/index/stocks'.
     *
     * @return list of stocks to display
     */
    @RequestMapping(value = "/index/stocks", method = RequestMethod.GET)
    public ResponseEntity<List<Stock>> listOfAllStocks() {
        List<Stock> stocks = stockService.findAllStocks();
        if (stocks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(stocks, HttpStatus.OK);
    }

    /**
     * Receive POST request '/index'.
     *
     * @param input requestBody containing Input object
     * @param ucBuilder
     * @return New location after receiving POST request
     */
    @RequestMapping(value = "/index/", method = RequestMethod.POST)
    public ResponseEntity<Void> findStocks(@RequestBody Input input, UriComponentsBuilder ucBuilder) {

        stockService.getStocksForFinancialAdvice(input.getLocation(), input.getIndustry());

        // Create HttpHeaders and setLocation to "index" route
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/index").build().toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
}
