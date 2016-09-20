package com.rit.service;

import java.util.List;

import com.rit.model.Stock;
import java.io.UnsupportedEncodingException;
import org.json.simple.parser.ParseException;

public interface StockService {

    List<Stock> findAllStocks();

    void getStocksForFinancialAdvice(String location, String industry);
    
    List<String> getCompaniesInvokingGlassdoorAPI(String location, String industryType) throws ParseException, UnsupportedEncodingException;
    
    List<Stock> getStockSymbolsInvokingStockSearchAPI(List<String> companies) throws UnsupportedEncodingException, ParseException;
    
    double getStockPriceInvokingGoogleFinanceAPI(String symbol) throws ParseException;
    
    String getSimpleMovingAverageInvokingMarketOnDemandAPI(String symbol, int period) throws UnsupportedEncodingException;

}
