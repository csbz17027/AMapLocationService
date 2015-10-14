/**
 * Created by lance on 15/10/13.
 */
var exec = require('cordova/exec');
var AMapLocationService = function(){};
AMapLocationService.prototype.execute = function(action,successCallback,errorCallback){
		exec(successCallback,errorCallback,"AMapLocationService",action,[]);
	};
	/**
	*启动定位服务并获取当前定位信息
	*/
AMapLocationService.prototype.getCurrentPosition = function(successCallback, errorCallback){
	this.execute("getCurrentPosition",successCallback,errorCallback);
	};

	/**
	*关闭定位服务
	*/
AMapLocationService.prototype.stop = function(successCallback, errorCallback){
	this.execute("stop",successCallback,errorCallback);
	};

if(!window.plugins){
	window.plugins = {};
	}

if(!window.plugins.AMapLocationService){
	window.plugins.AMapLocationService = new AMapLocationService();
	}

module.exports = new AMapLocationService();