/**
 * Controls application inputs and binds data between model and view.
 * 
 * @file stockController.js
 * @member StockController
 * @memberOf App
 * @type {angular.Controller}
 * @author Harshit
 */
'use strict';
angular.module('myApp').controller('stockController', ['$scope', 'stockService', function ($scope, stockService) {
        var self = this;

        // Declare variables
        self.input = {
            location: '',
            industry: ''
        };
        self.stocks = [];
        self.submit = submit;
        self.reset = reset;

        /**
         * Submits input form details and send it to our spring mvc application 
         * as a request and gets stock information.
         *
         * @function submit
         * @memberOf StockController
         */
        function submit() {
            console.log('Passing Location and Industry to Controller', self.input);
            sendInput(self.input);
            reset();
        };

        /**
         * Send input data to our spring mvc application through service as a 
         * request and gets stock information.
         *
         * @function sendInput
         * @memberOf StockController
         * @param {object} input input object with propery location and industry
         */
        function sendInput(input) {
            stockService.sendInput(input)
                .then(
                    getAllStocks,
                    function (err) {
                        console.error('Error while sending Input: ' + err);
                    }
                );
        }

        /**
         * Make a request through service and get an arary of stock objects.
         *
         * @function getAllStocks
         * @memberOf StockController
         */
        function getAllStocks() {
            stockService.getAllStocks()
                .then(
                    function (data) {
                        self.stocks = data;
                    },
                    function (err) {
                        console.error('Error while fetching Stocks: ' + err);
                    }
                );
        }

        /**
         * Reset input object and form in the view.
         *
         * @function reset
         * @memberOf StockController
         */
        function reset() {
            self.input = {location: '', industry: ''};
            $scope.myForm.$setPristine(); //reset Form
        }

    }]);
 