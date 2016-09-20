'use strict';

angular.module('myApp').factory('StockService', ['$http', '$q', function($http, $q){

    var REST_SERVICE_URI = 'http://localhost:8080/FinancialStockAdvice/index/';

    var factory = {
        getAllStocks: getAllStocks,
        sendInput: sendInput
    };

    return factory;

    function getAllStocks() {
        var deferred = $q.defer();
        $http.get(REST_SERVICE_URI + 'stocks')
            .then(
            function (response) {
                deferred.resolve(response.data);
            },
            function(errResponse){
                console.error('Error while fetching Stocks');
                deferred.reject(errResponse);
            }
        );
        return deferred.promise;
    }

    function sendInput(input) {
        var deferred = $q.defer();
        $http.post(REST_SERVICE_URI, input)
            .then(
            function (response) {
                deferred.resolve(response.data);
            },
            function(errResponse){
                console.error('Error while sending Input (Location and Industry)');
                deferred.reject(errResponse);
            }
        );
        return deferred.promise;
    }
}]);
