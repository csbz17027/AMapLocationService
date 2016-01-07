/**
 * Created by lance on 15/10/13.
 */
 var argscheck = require('cordova/argscheck'),
	 exec = require('cordova/exec'),
	AMapLocationService = function(){};
	AMapLocationService.prototype.locationType = {
		Hight_Accuracy:0,//高精度
		Device_Sensors:1,//仅设备(Gps)模式,不支持室内环境的定位
		Battery_Saving:2//低功耗模式
	};
AMapLocationService.prototype.execute = function(action,successCallback,errorCallback,args){
		exec(successCallback,errorCallback,"AMapLocationService",action,args);
	};
	/**
	*启动定位服务并获取当前定位信息
	*/
AMapLocationService.prototype.getCurrentPosition = function(successCallback, errorCallback){
	this.execute("getCurrentPosition",successCallback,errorCallback);
	};

AMapLocationService.prototype.locationWatch = function(successCallback,errorCallback,option){
	var option = option||{};
	var getValue = argscheck.getValue;
	var interval = getValue(option.interval,2);//定位时间间隔 秒
	var isNeedAddress = getValue(option.isNeedAddress,true);//是否需要地址
	var isOnceLocation = getValue(option.isOnceLocation,false);//是否单次定位
	var gpsFirst = getValue(option.gpsFirst,false);//是否优先返回GPS定位信息
	var httpTimeOut = getValue(option.httpTimeOut,30);//联网超时时间 秒
	var wifiActiveScan = getValue(option.wifiActiveScan,true);//是否主动刷新wifi
	var locationType = getValue(option.locationType,0);//定位模式
	var args = [interval,isNeedAddress,isOnceLocation,gpsFirst,httpTimeOut,wifiActiveScan,locationType];
	this.execute("watch",successCallback,errorCallback,args);
};

AMapLocationService.prototype.changeOption = function(successCallback,errorCallback,option){
	var option = option||{};
    var getValue = argscheck.getValue;
    var interval = getValue(option.interval,2);//定位时间间隔 秒
    var isNeedAddress = getValue(option.isNeedAddress,true);//是否需要地址
    var isOnceLocation = getValue(option.isOnceLocation,false);//是否单次定位
    var gpsFirst = getValue(option.gpsFirst,false);//是否优先返回GPS定位信息
    var httpTimeOut = getValue(option.httpTimeOut,30);//联网超时时间 秒
    var wifiActiveScan = getValue(option.wifiActiveScan,true);//是否主动刷新wifi
    var locationType = getValue(option.locationType,0);//定位模式
    var args = [interval,isNeedAddress,isOnceLocation,gpsFirst,httpTimeOut,wifiActiveScan,locationType];
    this.execute("changeOption",successCallback,errorCallback,args);
}

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