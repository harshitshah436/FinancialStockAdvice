package edu.rit.service;

import org.springframework.stereotype.Service;

import edu.rit.model.Stock;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.UriBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * StockServiceImpl implements StockService class and calls all apis, gets
 * stocks information.
 *
 * @author Harshit
 */
@Service("stockService")
public class StockServiceImpl implements StockService {

    private static final Logger LOGGER = Logger.getLogger(StockServiceImpl.class.getName());

    // KEY and PartnerID for marketOnDamand apis.
    private static final String PARTNER_ID = "91577";
    private static final String KEY = "hExJgaefQzk";

    private List<Stock> stocks;

    public StockServiceImpl() {
        stocks = new ArrayList<>();
    }

    /**
     * @return a list of Stock objects containing stock details.
     */
    @Override
    public List<Stock> findAllStocks() {
        return stocks;
    }

    /**
     * This method calls all APIs, makes Stock objects and sorts them by
     * descending order of SMA difference.
     *
     * @param location
     * @param industryType
     */
    @Override
    public void getStocksForFinancialAdvice(String location, String industryType) {
        try {
            stocks = new ArrayList<>();

            // Calling glassdoor api to get companies list.
            List<String> companies = getCompaniesInvokingGlassdoorAPI(location, industryType);
            if (companies.isEmpty()) {
                return;
            }

            // Calling yahoo finance api to get stock symbols by company name.
            List<Stock> allStocksWithSymbols = getStockSymbolsInvokingYahooFinanceAPI(companies);

            for (Stock stock : allStocksWithSymbols) {
//                TimeUnit.MILLISECONDS.sleep(2100);

                // Calling google finance api to get Stock price.
                double stockPrice = getStockPriceInvokingQuandlFinancialDataAPI(stock.getSymbol());
                if (stockPrice == -1) {
                    continue; // if no stock price found for the symbol, ignore current stock.
                }
                stock.setPrice(stockPrice);

                // Calling marketOnDemand api to get simple moving average of a stock for last 200 days.
                String response200 = getSimpleMovingAverageInvokingMarketOnDemandAPI(stock.getSymbol(), 200);
                String symbol_sma = "";
                if (!response200.isEmpty()) {
                    symbol_sma = parseJsonForSimpleMovingAverage(response200);
                }
                if (symbol_sma.isEmpty()) {
                    continue;
                }
                String[] temp = symbol_sma.split(":");

                // Calling marketOnDemand api to get simple moving average of a stock for last 50 days.
                String response50 = getSimpleMovingAverageInvokingMarketOnDemandAPI(stock.getSymbol(), 50);
                if (!response50.isEmpty()) {
                    symbol_sma = parseJsonForSimpleMovingAverage(response50);
                }
                if (symbol_sma.isEmpty()) {
                    continue;
                }
                String[] temp1 = symbol_sma.split(":");

                // Set SMA difference by subtracting 'sma of last 200 days' from
                // 'sma of last 50 days' for a stock price.
                stock.setSma200(Double.parseDouble(temp[1]));
                stock.setSma50(Double.parseDouble(temp1[1]));
                stock.setSmaDiff(stock.getSma50() - stock.getSma200());
                if (!stocks.contains(stock)) {
                    stocks.add(stock);
                }
            }

            // Sort in descending order of sma_diff
            Collections.sort(stocks);

        } catch (UnsupportedEncodingException | ParseException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method sends a request to Glassdoor API with location and
     * industryType and it will get list of employers from the response.
     *
     * @param location
     * @param industryType
     *
     * @return a list of companies/employers
     * @throws ParseException
     * @throws UnsupportedEncodingException
     */
    @Override
    public List<String> getCompaniesInvokingGlassdoorAPI(String location, String industryType) throws ParseException, UnsupportedEncodingException {
        LOGGER.log(Level.INFO, "Calling glassdoor api for getting a list of companies by location and industry.");

        // Creating a web resource
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource wr = client.resource(
                UriBuilder.fromUri("http://api.glassdoor.com/api/api.htm?v=1&format=json"
                        + "&t.p=" + PARTNER_ID + "&t.k=" + KEY
                        + "&action=employers"
                        + "&l=" + URLEncoder.encode(location, "UTF-8") + "&q=" + URLEncoder.encode(industryType, "UTF-8")
                        + "&userip=129.21.101.101"
                        + "&useragent=Mozilla/%2F4.0").build());

        // Sending get request and receiving JSON data
        ClientResponse response = wr.accept("application/json")
                .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String json_response = response.getEntity(String.class);

        // Get 'employers' array from json response.
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(json_response);
        JSONObject response_obj = (JSONObject) obj.get("response");
        JSONArray employers_array = (JSONArray) response_obj.get("employers");

        List<String> companies = new ArrayList<>();
        for (Object array1 : employers_array) {
            JSONObject employer_obj = (JSONObject) array1;
            companies.add(employer_obj.get("name").toString());
            LOGGER.log(Level.INFO, employer_obj.get("name").toString());
        }

        return companies;
    }

    /**
     * Get stock symbol for the given list of company names invoking Yahoo
     * Finance API.
     *
     * @param companies a list of companies
     *
     * @return a list of stock symbol for given companies
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    @Override
    public List<Stock> getStockSymbolsInvokingYahooFinanceAPI(List<String> companies) throws UnsupportedEncodingException, ParseException {
        LOGGER.log(Level.INFO, "Calling yahoo finance api for retrieving stock symbols by company names.");

        List<Stock> allStocksWithSymbols = new ArrayList<>();

        for (String company : companies) {

            Stock stock = new Stock();
            stock.setCompanyName(company);

            // Creating a web resource
            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            WebResource wr = client.resource(
                    UriBuilder.fromUri("http://d.yimg.com/autoc.finance.yahoo.com/autoc?query="
                            + URLEncoder.encode(stock.getCompanyName(), "UTF-8").replace("+", "%20") + "&region=1&lang=en").build());

            // Sending get request and receiving JSON data
            ClientResponse response = wr.accept("application/json")
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

            String json_response = response.getEntity(String.class);

            // Get a symbol property from the received json response.
            JSONParser parser = new JSONParser();
            JSONObject responseFromAPI = (JSONObject) parser.parse(json_response);
            JSONObject resultSet = (JSONObject) responseFromAPI.get("ResultSet");
            JSONArray result = (JSONArray) resultSet.get("Result");
            if (result.size() > 0) {
                JSONObject obj = (JSONObject) result.get(0);
                stock.setSymbol(obj.get("symbol").toString());
                allStocksWithSymbols.add(stock);
                LOGGER.log(Level.INFO, obj.get("symbol").toString());
            } else {
                LOGGER.log(Level.WARNING, "Not able to find stock symbol for the company: {0}", company);
            }
        }
        return allStocksWithSymbols;
    }

    /**
     * Get current stock price invoking Quandl financial data API for the given stock
     * symbol.
     *
     * @param symbol stock symbol
     *
     * @return current stock price for the given stock symbol
     * @throws ParseException
     */
    @Override
    public double getStockPriceInvokingQuandlFinancialDataAPI(String symbol) throws ParseException {
        LOGGER.log(Level.INFO, "Calling Quandl financial data API for getting stock price by stock symbol: {0}", symbol);

        // API Key to invoke API
        final String apiKEY = "4VQgQ5WYT1Fav_vgqc6o";
        // Creating a web resource
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource(
                UriBuilder.fromUri("https://www.quandl.com/api/v1/datasets/WIKI/"
                        + symbol + ".json?api_key" + apiKEY).build());

        // Sending get request and receiving JSON data
        ClientResponse response = service.accept("application/json")
                .get(ClientResponse.class);

        if (response.getStatus() == 404) {
            LOGGER.log(Level.WARNING, "Not able to find stock price invoking Quandl financial data API for a symbol: {0}", symbol);
            return -1;
        }

        String json_response = response.getEntity(String.class);

        // Get stock price from the received response.
        JSONParser parser = new JSONParser();
        JSONObject apiResponse = (JSONObject) parser.parse(json_response);
        JSONArray array = (JSONArray) apiResponse.get("data");
        double price = Double.parseDouble(((JSONArray) array.get(0)).get(1).toString());
        return price;
    }

    /**
     * Get simple moving average (SMA) by calling marketOnDemand API by given
     * number of days and stock symbol.
     *
     * @param symbol
     * @param period number of days
     * @return SMA
     * @throws UnsupportedEncodingException
     */
    @Override
    public String getSimpleMovingAverageInvokingMarketOnDemandAPI(String symbol, int period) throws UnsupportedEncodingException {
        LOGGER.log(Level.INFO, "Calling marketOnDemand api for getting simple moving average (SMA) difference from a given stock symbol: {0} and number of days: {1}", new Object[]{symbol, period});

        // Creating a web resource
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource(
                UriBuilder.fromUri("http://dev.markitondemand.com/MODApis/Api/v2/InteractiveChart/json?parameters="
                        + URLEncoder.encode("{\"Normalized\":false,\"NumberOfDays\":5,\"DataPeriod\":\"Day\",\"Elements\":[{\"Symbol\":\"", "UTF-8")
                        + symbol
                        + URLEncoder.encode("\",\"Type\":\"sma\",\"Params\":["
                                + period + "]}]}", "UTF-8")).build());

        // Sending get request and receiving JSON data
        ClientResponse response = service.accept("application/json")
                .get(ClientResponse.class);

        if (response.getStatus() != 200 && response.getStatus() != 500) {
            LOGGER.log(Level.SEVERE, response.getEntity(String.class));
        }

        String json_response = "";
        if (response.getStatus() != 500 && response.getStatus() != 501) {
            json_response = response.getEntity(String.class);
        }
        return json_response;
    }

    /**
     * Parse Json response and return simple moving average.
     *
     * @param response response from MarketOnDemand API
     * @return a string with SMA. format: "stock_symbol: sma_value"
     * @throws ParseException
     */
    public String parseJsonForSimpleMovingAverage(String response) throws ParseException {
        String result;

        // Get simple moving average from the json response.
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(response);
        JSONArray arrayElements = (JSONArray) obj.get("Elements");
        obj = (JSONObject) arrayElements.get(0);
        result = obj.get("Symbol").toString();
        obj = (JSONObject) obj.get("DataSeries");
        if (obj == null) {
            return "";
        }
        obj = (JSONObject) obj.get("sma");
        arrayElements = (JSONArray) obj.get("values");
        return result + ":" + Double.parseDouble(arrayElements.get(arrayElements.size() - 1).toString());
    }
}
