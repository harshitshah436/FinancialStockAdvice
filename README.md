# FinancialStockAdvice
Restful Web service using Spring framework and invoking the service from AngularJS front-end app.

A service for financial advise for buying stocks based on location and industry type (Software, Pharmaceutical, etc.).

URL: https://github.com/harshitshah436/FinancialStockAdvice

Author: Harshit Shah

### Installation & Run
    - Clone the project from the above URL and rename the root directory of the project to 'FinancialStockAdvice'.
    - Install latest version of Netbeans IDE.
    - Open this project selecting ,'File->Open Project' option.
    - Now right click on the project and select option 'Clean and Build'.
    - After successful build, 'Run' the project on appropriate server.

### Used APIs
  - Preselected API
    - [Glassdoor API](https://www.glassdoor.com/developer/index.htm)
  
  - Other API
    - ~~[CHStockSearch API](http://chstocksearch.herokuapp.com/welcome)~~ (obsoleted)
    - [Yahoo Finance API](http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=xerox&region=1&lang=en)
    - ~~[Google Finance API](http://finance.google.com/finance/info?q=INTU)~~ (depreciated)
    - [Market On Demand Market Data API](http://dev.markitondemand.com/MODApis/)
    - [Quandl financial data API](https://www.quandl.com/tools/api)

### Algorithm Implemented
- Buy 50 shares of a stock when its 50-day simple moving average goes above the 200-day moving average.
- Sell shares of the stock when its 50-day simple moving average goes below the 200-day moving average.

### Process Flow
    - Base on user input (location and industry), retrieving companies with respect to input using Glassdoor API.
    - Then, figuring out stock symbol for each company using CHStockSearch API.
    - Using this symbol getting past 200 and 50 days stock information including Simple Moving Average using Market On Demand Interactive Chart API.
    - We will also use this symbol to get current stock prices from Google Finanace API.
    - At last, apply our algorithm to all retrieved data and find out which stocks to buy and sell for financial advise.

### Enviornment
- Netbeans 8.1
- Spring 4.3 MVC
- AngularJS 1.4
- Maven 3
- JDK 1.7
- Windows 10
- Google Chrome (expected user experience)

### Application Working Screens
![1](https://github.com/harshitshah436/FinancialStockAdvice/blob/master/working_screens/1.png)
![3](https://github.com/harshitshah436/FinancialStockAdvice/blob/master/working_screens/3.png)
![2](https://github.com/harshitshah436/FinancialStockAdvice/blob/master/working_screens/2.png)
![4](https://github.com/harshitshah436/FinancialStockAdvice/blob/master/working_screens/4.png)
