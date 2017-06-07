<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>  
        <title>Financial Stock Advice</title>  
        <style>
            .loc.ng-valid {
                background-color: lightgreen;
            }
            .loc.ng-dirty.ng-invalid-required {
                background-color: red;
            }
        </style>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
        <link href="<c:url value='/static/css/app.css' />" rel="stylesheet"></link>
    </head>
    <body ng-app="myApp" class="ng-cloak">
        <div class="generic-container" ng-controller="stockController as ctrl">
            <div class="panel panel-default">
                <div class="panel-heading"><span class="lead">Financial Advice within specific Location and Industry </span></div>
                <div class="formcontainer">
                    <form ng-submit="ctrl.submit()" name="myForm" class="form-horizontal">
                        <input type="hidden" ng-model="ctrl.input.location" />
                        <div class="row">
                            <div class="form-group col-md-12">
                                <label class="col-md-2 control-lable" for="file">Location</label>
                                <div class="col-md-7">
                                    <input type="text" ng-model="ctrl.input.location" name="loc" class="location form-control input-sm" placeholder="Enter Location" required/>
                                    <div class="has-error" ng-show="myForm.$dirty">
                                        <span ng-show="myForm.loc.$error.required">This is a required field</span>
                                    </div>
                                </div>
                            </div>
                        </div>


                        <div class="row">
                            <div class="form-group col-md-12">
                                <label class="col-md-2 control-lable" for="file">Industry Type</label>
                                <div class="col-md-7">
                                    <input type="text" ng-model="ctrl.input.industry" class="form-control input-sm" placeholder="Enter Industry type (e.g. Software)"/>
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="form-actions floatRight">
                                <input type="submit"  class="btn btn-primary btn-sm" ng-disabled="myForm.$invalid" value="Submit">
                                <button type="button" ng-click="ctrl.reset()" class="btn btn-warning btn-sm" ng-disabled="myForm.$pristine">Reset Form</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="panel panel-default" ng-show="ctrl.stocks.length > 0">
                <!-- Default panel contents -->
                <div class="panel-heading"><span class="lead">List of Stocks </span></div>
                <div class="tablecontainer">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>Symbol</th>
                                <th>Name</th>
                                <th>Price</th>
                                <th>SMA (of 50 days)</th>
                                <th>SMA (of 200 days)</th>
                                <th>SMA Difference</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr ng-repeat="stock in ctrl.stocks">
                                <td><span ng-bind="stock.symbol"></span></td>
                                <td><span ng-bind="stock.companyName"></span></td>
                                <td><span ng-bind="stock.price"></span></td>
                                <td><span ng-bind="stock.sma50"></span></td>
                                <td><span ng-bind="stock.sma200"></span></td>
                                <td><span ng-bind="stock.smaDiff"></span></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="panel-heading" ng-show="ctrl.stocks.length > 0">
                <span class="lead">
                    Buy 50 stocks of: &nbsp;<span ng-repeat="stock in ctrl.stocks" ng-if="stock.smaDiff >= 0">
                        {{stock.companyName}} &nbsp; </span>
                </span>
                <br/>
                <span class="lead">
                    Sell 50 stocks of: &nbsp;<span ng-repeat="stock in ctrl.stocks" ng-if="stock.smaDiff < 0">
                        {{stock.companyName}} &nbsp; </span>
                </span>
            </div>
            <span us-spinner="{radius:30, width:8, length: 16}"></span>
        </div>

        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.4/angular.js"></script>
        <script src="<c:url value='/static/js/app.js' />"></script>
        <script src="<c:url value='/static/js/service/stockService.js' />"></script>
        <script src="<c:url value='/static/js/controller/stockController.js' />"></script>
        <script src="<c:url value='/static/js/angular-spinner.min.js' />"></script>
        <script src="<c:url value='/static/js/angular-loading-spinner.js' />"></script>
        <script type="text/javascript" src="http://fgnass.github.io/spin.js/spin.min.js"></script>
    </body>
</html>