/**
 * Created by lance on 15/10/13.
 */
package lance.cordova.plugins;


import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AMapLocationService extends CordovaPlugin implements AMapLocationListener {

    private static final String STOP_ACTION = "stop";
    private static final String GET_ACTION = "getCurrentPosition";
    private static final String WATCH_ACTION = "watch";
    private static final String CHANGE_OPTION_ACTION = "changeOption";
    CallbackContext cbCtx;
    JSONObject jsonObj = new JSONObject();
    AMapLocationClientOption mLocationOption = null;
    AMapLocationClient mLocationClient = null;

    @Override
    public boolean execute(final String action, final JSONArray args,
                           final CallbackContext callbackContext) {
        setCallbackContext(callbackContext);
        if (GET_ACTION.equals(action)) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            cbCtx.sendPluginResult(pluginResult);
            if (mLocationClient == null) {
                cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        mLocationOption = new AMapLocationClientOption();
                        mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
                        mLocationOption.setNeedAddress(true);
                        mLocationOption.setOnceLocation(false);
                        mLocationOption.setWifiActiveScan(true);
                        mLocationOption.setMockEnable(false);
                        mLocationOption.setInterval(10 * 1000);
                        mLocationClient = new AMapLocationClient(cordova.getActivity());
                        mLocationClient.setLocationOption(mLocationOption);
                        mLocationClient.setLocationListener(AMapLocationService.this);
                        mLocationClient.startLocation();
                    }
                });
            }
            return true;
        }
        if (WATCH_ACTION.equals(action)) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            cbCtx.sendPluginResult(pluginResult);
            if (mLocationClient == null) {
                cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        setLocationOption(args);
                        mLocationClient = new AMapLocationClient(cordova.getActivity());
                        mLocationClient.setLocationOption(mLocationOption);
                        mLocationClient.setLocationListener(AMapLocationService.this);
                        mLocationClient.startLocation();
                    }
                });
            }
            return true;
        }
        if (CHANGE_OPTION_ACTION.equals(action)) {
            setLocationOption(args);
            if (mLocationClient == null) {
                mLocationClient = new AMapLocationClient(cordova.getActivity());
                mLocationClient.setLocationListener(this);
            }
            mLocationClient.setLocationOption(mLocationOption);
            return true;
        }

        if (STOP_ACTION.equals(action)) {
            stopLocation();
            cbCtx.success(200);
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, 200);
            pluginResult.setKeepCallback(false);
            cbCtx.sendPluginResult(pluginResult);
            return true;
        }
        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, PluginResult.Status.INVALID_ACTION.toString());
        pluginResult.setKeepCallback(false);
        cbCtx.sendPluginResult(pluginResult);
        return false;
    }

    private void setLocationOption(JSONArray args) {
        try {
            if (mLocationOption == null) {
                mLocationOption = new AMapLocationClientOption();
            }
            mLocationOption.setInterval(args.getInt(0) * 1000);
            mLocationOption.setNeedAddress(args.getBoolean(1));
            mLocationOption.setOnceLocation(args.getBoolean(2));
            mLocationOption.setGpsFirst(args.getBoolean(3));
            mLocationOption.setHttpTimeOut(args.getInt(4) * 1000);
            mLocationOption.setWifiActiveScan(args.getBoolean(5));
            int locationType = args.getInt(6);
            switch (locationType) {
                case 0:
                    mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
                    break;
                case 1:
                    mLocationOption.setLocationMode(AMapLocationMode.Device_Sensors);
                    break;
                case 2:
                    mLocationOption.setLocationMode(AMapLocationMode.Battery_Saving);
                    break;
                default:
                    mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
                    break;
            }
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            mLocationOption.setMockEnable(false);
        } catch (JSONException e) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "json解析错误，请确认参数是否有误");
            pluginResult.setKeepCallback(false);
            cbCtx.sendPluginResult(pluginResult);
        }
    }

    private void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
            mLocationClient = null;
            mLocationOption = null;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocation();
    }

    public CallbackContext getCallbackContext() {
        return cbCtx;
    }

    public void setCallbackContext(CallbackContext callbackContext) {
        cbCtx = callbackContext;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        try {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                Log.d("aMapLocation", aMapLocation.toString());
                //获取位置信息
                Double geoLat = aMapLocation.getLatitude();
                Double geoLng = aMapLocation.getLongitude();

                JSONObject position = new JSONObject();
                position.put("lat", aMapLocation.getLatitude());
                position.put("lng", aMapLocation.getLongitude());
                jsonObj.put("errCode", aMapLocation.getErrorCode());
                jsonObj.put("position", position);
                jsonObj.put("address", aMapLocation.getAddress());
                jsonObj.put("adCode", aMapLocation.getAdCode());
                jsonObj.put("province", aMapLocation.getProvince());
                jsonObj.put("city", aMapLocation.getCity());
                jsonObj.put("cityCode", aMapLocation.getCityCode());
                jsonObj.put("locationType", aMapLocation.getLocationType());
                jsonObj.put("locationDetail", aMapLocation.getLocationDetail());
                Log.d("location jsonObj", jsonObj.toString());
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObj);
                if (!mLocationOption.isOnceLocation()) {
                    pluginResult.setKeepCallback(true);
                } else {
                    pluginResult.setKeepCallback(false);
                }
                cbCtx.sendPluginResult(pluginResult);
            } else {
                jsonObj.put("errCode", aMapLocation.getErrorCode());
                jsonObj.put("errMsg", aMapLocation.getErrorInfo());
                PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, jsonObj);
                if (!mLocationOption.isOnceLocation()) {
                    pluginResult.setKeepCallback(true);
                } else {
                    pluginResult.setKeepCallback(false);
                }
                cbCtx.sendPluginResult(pluginResult);
            }
        } catch (JSONException e) {
            cbCtx.error(e.getMessage());
            String errMsg = e.getMessage();
            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, errMsg);
            pluginResult.setKeepCallback(false);
            cbCtx.sendPluginResult(pluginResult);
            stopLocation();
        }
    }
}