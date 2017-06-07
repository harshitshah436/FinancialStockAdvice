/**
 * Performs actual operations. It makes http requests to the server and handle 
 * results asynchronously using $q service.
 * 
 * @file stockService.js
 * @member StockService
 * @memberOf App
 * @type {angular.Service}
 * @author Harshit
 */
'use strict';
angular.module('myApp').factory('stockService', ['$http', '$q', function ($http, $q) {

        var REST_SERVICE_URI = 'http://localhost:8080/FinancialStockAdvice/index/';

        /**
         * Send POST request to our spring mvc rest service with input data so 
         * server side, stocks details would be created.
         *
         * @function getAllStocks
         * @memberOf StockService
         * @param {object} input input object with propery location and industry
         */
        function sendInput(input) {
            var deferred = $q.defer();
            $http.post(REST_SERVICE_URI, input)
                .then(
                    function (response) {
                        deferred.resolve(response.data); // Empty response data
                    },
                    function (err) {
                        console.error('Error while sending Input (Location and Industry)');
                        deferred.reject(err);
                    }
                );
            return deferred.promise;
        }

        /**
         * Send GET request to our spring mvc rest service and get an arary of 
         * stock objects in the response.
         *
         * @function getAllStocks
         * @memberOf StockService
         */
        function getAllStocks() {
            var deferred = $q.defer();
            $http.get(REST_SERVICE_URI + 'stocks')
                .then(
                    function (response) {
                        deferred.resolve(response.data);
                    },
                    function (err) {
                        console.error('Error while fetching Stocks');
                        deferred.reject(err);
                    }
                );
            return deferred.promise;
        }

        var factory = {
            getAllStocks: getAllStocks,
            sendInput: sendInput
        };

        return factory;
    }]);
