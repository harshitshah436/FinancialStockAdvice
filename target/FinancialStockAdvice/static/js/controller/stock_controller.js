'use strict';

angular.module('myApp').controller('StockController', ['$scope', 'StockService', function ($scope, StockService) {
        var self = this;
        self.input = {location: '', industry: ''};

        self.stocks = [];

        self.submit = submit;
        self.reset = reset;

        function getAllStocks() {
            StockService.getAllStocks()
                    .then(
                            function (d) {
                                self.stocks = d;
                            },
                            function (errResponse) {
                                console.error('Error while fetching Stocks');
                            }
                    );
        }

        function sendInput(input) {
            StockService.sendInput(input)
                    .then(
                            getAllStocks,
                            function (errResponse) {
                                console.error('Error while sending Input');
                            }
                    );
        }

        function submit() {
            console.log('Passing Location and Industry to Controller', self.input);
            sendInput(self.input);
            reset();
        }

        function reset() {
            self.input = {location: '', industry: ''};
            $scope.myForm.$setPristine(); //reset Form
        }

    }]);
