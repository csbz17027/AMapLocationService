/**
 * Created by lance on 15/10/13.
 */
package lance.cordova.plugins;


import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AMapLocationService extends CordovaPlugin implements AMapLocationListener {

    private static final String STOP_ACTION = "stop";
    private static final String GET_ACTION = "getCurrentPosition";
    CallbackContext callbackContext;
    LocationManagerProxy mLocationManagerProxy;
    JSONObject jsonObj = new JSONObject();

    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callbackContext) {
        setCallbackContext(callbackContext);
        if (GET_ACTION.equals(action)) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            this.callbackContext.sendPluginResult(pluginResult);
            if (mLocationManagerProxy == null) {
                cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        mLocationManagerProxy = LocationManagerProxy.getInstance(cordova.getActivity());
                        mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 10 * 1000, 10, AMapLocationService.this);
                    }
                });
            }
//            else {
//                try {
//                    if (jsonObj.getInt("errCode") == 0) {
//                        callbackContext.success(jsonObj);
//                    } else {
//                        callbackContext.error(jsonObj);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
            return true;
        }
        if (STOP_ACTION.equals(action)) {
            stopLocation();
            callbackContext.success(200);
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, 200);
            callbackContext.sendPluginResult(pluginResult);
            pluginResult.setKeepCallback(false);
            return true;
        }
        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR,PluginResult.Status.INVALID_ACTION.toString());
        callbackContext.sendPluginResult(pluginResult);
        pluginResult.setKeepCallback(false);
        return false;
    }

    private void stopLocation() {
        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.removeUpdates(this);
            mLocationManagerProxy.destory();
        }
        mLocationManagerProxy = null;
    }

    @Override
    public void onDestroy() {
        stopLocation();
        super.onDestroy();
    }

    public CallbackContext getCallbackContext() {
        return callbackContext;
    }

    public void setCallbackContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        try {
            if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
                //获取位置信息
                Double geoLat = aMapLocation.getLatitude();
                Double geoLng = aMapLocation.getLongitude();

                JSONObject position = new JSONObject();
                position.put("lat", aMapLocation.getLatitude());
                position.put("lng", aMapLocation.getLongitude());
                jsonObj.put("errCode", aMapLocation.getAMapException().getErrorCode());
                jsonObj.put("position", position);
                jsonObj.put("accuracy", aMapLocation.getAccuracy());
                jsonObj.put("provider", aMapLocation.getProvider());
                jsonObj.put("address", aMapLocation.getAddress());
                jsonObj.put("time", aMapLocation.getTime());
                Log.d("lanceLocation", jsonObj.toString());
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObj);
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            } else {
                jsonObj.put("errCode", aMapLocation.getAMapException().getErrorCode());
                jsonObj.put("errMsg", aMapLocation.getAMapException().getMessage());
                PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR,jsonObj);
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            }
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            String errMsg = e.getMessage();
            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, errMsg);
            callbackContext.sendPluginResult(pluginResult);
            pluginResult.setKeepCallback(false);
            stopLocation();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}