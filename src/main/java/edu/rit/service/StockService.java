package edu.rit.service;

import java.util.List;
import edu.rit.model.Stock;
import java.io.UnsupportedEncodingException;
import org.json.simple.parser.ParseException;

/**
 * StockService interface defines methods those must be implemented by
 * implementing classes.
 *
 * @author Harshit
 */
public interface StockService {

    List<Stock> findAllStocks();

    void getStocksForFinancialAdvice(String location, String industry);

    List<String> getCompaniesInvokingGlassdoorAPI(String location, String industryType) throws ParseException, UnsupportedEncodingException;

    List<Stock> getStockSymbolsInvokingYahooFinanceAPI(List<String> companies) throws UnsupportedEncodingException, ParseException;

    double getStockPriceInvokingQuandlFinancialDataAPI(String symbol) throws ParseException;

    String getSimpleMovingAverageInvokingMarketOnDemandAPI(String symbol, int period) throws UnsupportedEncodingException;

}
