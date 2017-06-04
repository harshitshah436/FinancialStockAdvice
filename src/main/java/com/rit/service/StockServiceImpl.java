package com.rit.service;

import org.springframework.stereotype.Service;

import com.rit.model.Stock;
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
import javax.ws.rs.core.UriBuilder;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Service("stockService")
public class StockServiceImpl implements StockService {

    private static final Logger LOGGER = Logger.getLogger(StockServiceImpl.class);
    private static final String PARTNER_ID = "91577";
    private static final String KEY = "hExJgaefQzk";

    private List<Stock> stocks;

    public StockServiceImpl() {
        stocks = new ArrayList<>();
    }

    @Override
    public List<Stock> findAllStocks() {
        return stocks;
    }

    @Override
    public void getStocksForFinancialAdvice(String location, String industryType) {
        try {

            stocks = new ArrayList<>();

            List<String> companies = getCompaniesInvokingGlassdoorAPI(location, industryType);

            List<Stock> allStocksWithSymbols = getStockSymbolsInvokingYahooFinanceAPI(companies);

            for (Stock stock : allStocksWithSymbols) {

//                TimeUnit.MILLISECONDS.sleep(2100);
                double stockPrice = getStockPriceInvokingGoogleFinanceAPI(stock.getSymbol());
                if (stockPrice == -1) {
                    continue;
                }
                stock.setPrice(stockPrice);

                String response200 = getSimpleMovingAverageInvokingMarketOnDemandAPI(stock.getSymbol(), 200);
                String symbol_sma = "";
                if (!response200.isEmpty()) {
                    symbol_sma = parseJsonForSimpleMovingAverage(response200);
                }
                if (symbol_sma.isEmpty()) {
                    continue;
                }
                String[] temp = symbol_sma.split(":");

                String response50 = getSimpleMovingAverageInvokingMarketOnDemandAPI(stock.getSymbol(), 50);
                if (!response50.isEmpty()) {
                    symbol_sma = parseJsonForSimpleMovingAverage(response50);
                }
                if (symbol_sma.isEmpty()) {
                    continue;
                }
                String[] temp1 = symbol_sma.split(":");

                stock.setSma200(Double.parseDouble(temp[1]));
                stock.setSma50(Double.parseDouble(temp1[1]));
                stock.setSmaDiff(stock.getSma50() - stock.getSma200());
                if (!stocks.contains(stock)) {
                    stocks.add(stock);
                }

            }

            Collections.sort(stocks);

        } catch (UnsupportedEncodingException | ParseException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    @Override
    public List<String> getCompaniesInvokingGlassdoorAPI(String location, String industryType) throws ParseException, UnsupportedEncodingException {
        ClientConfig config = new DefaultClientConfig();

        Client client = Client.create(config);
        WebResource wr = client.resource(
                UriBuilder.fromUri("http://api.glassdoor.com/api/api.htm?v=1&format=json"
                        + "&t.p=" + PARTNER_ID + "&t.k=" + KEY
                        + "&action=employers"
                        + "&l=" + URLEncoder.encode(location, "UTF-8") + "&q=" + URLEncoder.encode(industryType, "UTF-8")
                        + "&userip=129.21.101.101"
                        + "&useragent=Mozilla/%2F4.0").build());

        // getting JSON data
        ClientResponse response = wr.accept("application/json")
                .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String json_response = response.getEntity(String.class);

        //System.out.println(json_response);
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(json_response);
        JSONObject response_obj = (JSONObject) obj.get("response");
        JSONArray employers_array = (JSONArray) response_obj.get("employers");

        List<String> companies = new ArrayList<>();
        for (Object array1 : employers_array) {
            JSONObject employer_obj = (JSONObject) array1;
            companies.add(employer_obj.get("name").toString());
        }

        return companies;
    }

    @Override
    public List<Stock> getStockSymbolsInvokingYahooFinanceAPI(List<String> companies) throws UnsupportedEncodingException, ParseException {

        List<Stock> allStocksWithSymbols = new ArrayList<>();

        for (String company : companies) {

            Stock stock = new Stock();
            stock.setCompanyName(company);

            ClientConfig config = new DefaultClientConfig();

            Client client = Client.create(config);
            WebResource wr = client.resource(
                    UriBuilder.fromUri("http://d.yimg.com/autoc.finance.yahoo.com/autoc?query="
                            + URLEncoder.encode(stock.getCompanyName(), "UTF-8").replace("+", "%20") + "&region=1&lang=en").build());

            ClientResponse response = wr.accept("application/json")
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

            String json_response = response.getEntity(String.class);

            JSONParser parser = new JSONParser();
            JSONObject responseFromAPI = (JSONObject) parser.parse(json_response);
            JSONObject resultSet = (JSONObject) responseFromAPI.get("ResultSet");
            JSONArray result = (JSONArray) resultSet.get("Result");
            if (result.size() > 0) {
                JSONObject obj = (JSONObject) result.get(0);
                stock.setSymbol(obj.get("symbol").toString());
                allStocksWithSymbols.add(stock);
                System.out.println(obj.get("symbol"));
//                Logger.getLogger(KEY, json_response);
            }

        }
        return allStocksWithSymbols;
    }

    @Override
    public double getStockPriceInvokingGoogleFinanceAPI(String symbol) throws ParseException {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource(
                UriBuilder.fromUri("http://finance.google.com/finance/info?q="
                        + symbol).build());

        ClientResponse response = service.accept("application/json")
                .get(ClientResponse.class);

        if (response.getStatus() == 400) {
            LOGGER.warn("Not able to find stock price invoking google finance API for a symbol: " + symbol);
            return -1;
        }

        String json_response = response.getEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONArray array = (JSONArray) parser.parse(json_response.replace("//", ""));

        double price = 0;
        if (array.size() > 0) {
            JSONObject obj = (JSONObject) array.get(0);
            price = Double.parseDouble(obj.get("l").toString().replaceAll(",", ""));
        }

        return price;
    }

    @Override
    public String getSimpleMovingAverageInvokingMarketOnDemandAPI(String symbol, int period) throws UnsupportedEncodingException {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource(
                UriBuilder.fromUri("http://dev.markitondemand.com/MODApis/Api/v2/InteractiveChart/json?parameters="
                        + URLEncoder.encode("{\"Normalized\":false,\"NumberOfDays\":5,\"DataPeriod\":\"Day\",\"Elements\":[{\"Symbol\":\"", "UTF-8")
                        + symbol
                        + URLEncoder.encode("\",\"Type\":\"sma\",\"Params\":["
                                + period + "]}]}", "UTF-8")).build());

        ClientResponse response = service.accept("application/json")
                .get(ClientResponse.class
                );

//                json_response = response.getEntity(String.class);
//                System.out.println(json_response);
        if (response.getStatus() != 200 && response.getStatus() != 500) {
            System.err.println(response.getEntity(String.class));
        }

        String json_response = "";

        if (response.getStatus() != 500 && response.getStatus() != 501) {
            json_response = response.getEntity(String.class);
            System.out.println(json_response);
        }

        return json_response;
    }

    public String parseJsonForSimpleMovingAverage(String response200) throws ParseException {
        String result;
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(response200);
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
