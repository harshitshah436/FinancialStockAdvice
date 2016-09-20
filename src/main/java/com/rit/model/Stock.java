package com.rit.model;

public class Stock implements Comparable<Stock> {

    private String symbol;

    private String companyName;

    private double price;

    private double sma50;

    private double sma200;

    private double smaDiff;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSma50() {
        return sma50;
    }

    public void setSma50(double sma50) {
        this.sma50 = sma50;
    }

    public double getSma200() {
        return sma200;
    }

    public void setSma200(double sma200) {
        this.sma200 = sma200;
    }

    public double getSmaDiff() {
        return smaDiff;
    }

    public void setSmaDiff(double smaDiff) {
        this.smaDiff = smaDiff;
    }

    @Override
    public int compareTo(Stock stock) {
        return (stock.smaDiff - this.smaDiff) >= 0 ? 1 : -1;
    }

}
