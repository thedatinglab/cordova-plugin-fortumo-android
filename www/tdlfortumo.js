//
//  tdlFortumo
//  The Dating Lab
//
//  Created by TheDatingLab on 06/07/15.
//  Copyright (c) 2015 TheDatingLab. All rights reserved.

'use strict';

var argscheck = require('cordova/argscheck'),
	utils = require('cordova/utils'),
	exec = require('cordova/exec'),
	platform = require('cordova/platform');

function tdlFortumo() {}

tdlFortumo.prototype = {
	buy: function (serviceID, inAppSecret, memberID, success, error) {
		argscheck.checkArgs('sssFF', 'tdlFortumo.buy', arguments);
		exec(success, error, 'TDLFortumo', 'buy', [serviceID, inAppSecret, memberID]);
	},
	getPricePoint: function (serviceID, inAppSecret, success, error) {
		argscheck.checkArgs('ssFF', 'tdlFortumo.getPricePoint', arguments);
		exec(success, error, 'TDLFortumo', 'getPricePoint', [serviceID, inAppSecret]);
	},
	preLoadPricePoint: function (serviceID, inAppSecret, success, error) {
		argscheck.checkArgs('ssFF', 'tdlFortumo.preLoadPricePoint', arguments);
		exec(success, error, 'TDLFortumo', 'preLoadPricePoint', [serviceID, inAppSecret]);
	}
};

module.exports = new tdlFortumo();