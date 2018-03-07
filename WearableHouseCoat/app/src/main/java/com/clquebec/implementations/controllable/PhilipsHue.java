package com.clquebec.implementations.controllable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.clquebec.framework.controllable.ActionNotSupported;
import com.clquebec.framework.controllable.ControllableDeviceType;
import com.clquebec.framework.controllable.ControllableLightDevice;
import com.clquebec.framework.listenable.DeviceChangeListener;
import com.clquebec.framework.listenable.ListenableDevice;
import com.clquebec.framework.storage.ConfigurationStore;
import com.clquebec.wearablehousecoat.LightControlPanelActivity;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnection;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionType;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeResponseCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateCacheType;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateUpdatedCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateUpdatedEvent;
import com.philips.lighting.hue.sdk.wrapper.connection.ConnectionEvent;
import com.philips.lighting.hue.sdk.wrapper.connection.HeartbeatManager;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscovery;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryCallback;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryResult;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.domain.BridgeBuilder;
import com.philips.lighting.hue.sdk.wrapper.domain.BridgeState;
import com.philips.lighting.hue.sdk.wrapper.domain.HueError;
import com.philips.lighting.hue.sdk.wrapper.domain.ReturnCode;
import com.philips.lighting.hue.sdk.wrapper.domain.clip.ClipResponse;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightPoint;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightState;
import com.philips.lighting.hue.sdk.wrapper.utilities.HueColor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public class PhilipsHue implements ControllableLightDevice, ListenableDevice {
    private static final String TAG = "PhilipsHue";
    private static final int SETTING_BRIGHTNESS = 0;
    private static final int SETTING_COLOR = 1;
    private static final int SETTING_ON = 2;

    private static boolean findingBridge = false;
    private static Bridge mbridge = null;
    private static BridgeDiscovery mbridgeDiscovery = null;
    private static Map<DeviceChangeListener, PhilipsHue> listeners = new HashMap<>();
    private static BridgeConnection connection;
    private static boolean requiresAuthentication = false;

    private Context mContext;
    private UUID mUUID;
    private String mName;
    private Integer mLightNumber = 0;


    private BridgeConnectionCallback bridgeConnectionCallback = new BridgeConnectionCallback() {
        @Override
        public void onConnectionEvent(BridgeConnection bridgeConnection, ConnectionEvent connectionEvent) {
            Log.d(TAG, "Connection event: " + connectionEvent);

            if (connectionEvent == ConnectionEvent.DISCONNECTED
                    || connectionEvent == ConnectionEvent.CONNECTION_LOST
                    || connectionEvent == ConnectionEvent.CONNECTION_RESTORED){
                for (DeviceChangeListener l : listeners.keySet()){
                    l.updateState(listeners.get(l));
                }
            }else if (connectionEvent==ConnectionEvent.LINK_BUTTON_NOT_PRESSED){
                requiresAuthentication = true;
                for (DeviceChangeListener l : listeners.keySet()){
                    l.updateState(listeners.get(l));
                }
            }else if (connectionEvent==ConnectionEvent.AUTHENTICATED){
                requiresAuthentication = false;
                for (DeviceChangeListener l : listeners.keySet()){
                    l.updateState(listeners.get(l));
                }
            }
        }

        public void onConnectionError(BridgeConnection bridgeConnection, List<HueError> hueErrors){
            for (HueError error : hueErrors) {
                Log.e(TAG, "Connection error: " + error.toString());
            }
        }
    };

    private BridgeStateUpdatedCallback bridgeStateUpdatedCallback = new BridgeStateUpdatedCallback() {
        @Override
        public void onBridgeStateUpdated(Bridge bridge, BridgeStateUpdatedEvent bridgeStateUpdatedEvent) {
            Log.d(TAG, "Bridge state updated event: " + bridgeStateUpdatedEvent);
            if (bridgeStateUpdatedEvent == BridgeStateUpdatedEvent.INITIALIZED){
                for (DeviceChangeListener l : listeners.keySet()){
                    l.updateState(listeners.get(l));
                }

                HeartbeatManager hbm = connection.getHeartbeatManager();
                if (hbm != null) {
                    Log.d(TAG, "Starting heartbeats...");
                    hbm.startHeartbeat(BridgeStateCacheType.LIGHTS_AND_GROUPS, 5000);
                }
            } else if (bridgeStateUpdatedEvent == BridgeStateUpdatedEvent.LIGHTS_AND_GROUPS){
                //TODO: Only call listeners on PhilipsHue lights that have actually changed
                Log.d(TAG, "Attempting to run listeners");
                for (DeviceChangeListener l : listeners.keySet()){
                    l.updateState(listeners.get(l));
                }
            }
        }
    };

    public PhilipsHue(Context c, UUID id, JSONObject config) throws JSONException{
        this(c);
        mUUID = id;

        //Get light number from config
        mLightNumber = config.getInt("light");
    }

    public PhilipsHue(Context c) {
        mContext = c;
        //TODO: Allow addressing of specific Hue lights via JSON config
        if (mbridge == null && !findingBridge){

            //Load in parameters from configuration store
            ConfigurationStore.getInstance(c).onConfigAvailable(config -> {
                mbridgeDiscovery = new BridgeDiscovery();
                findingBridge = true;
                mbridgeDiscovery.search(BridgeDiscovery.BridgeDiscoveryOption.ALL, new BridgeDiscoveryCallback() {
                    @Override
                    public void onFinished(List<BridgeDiscoveryResult> list, ReturnCode returnCode) {
                        findingBridge = false;
                        if (list.size() == 0){
                            Log.e(TAG, "No bridge found");
                            //TODO: Decide what to do here.
                        }else if (list.size() > 1){
                            Log.e(TAG, "Multiple bridges found. WHC does not support multiple bridges");
                            //TODO: Pick one?
                        }else{
                            BridgeDiscoveryResult bdr = list.get(0);
                            String ip = bdr.getIP();
                            Log.d(TAG, "One bridge found. Connecting to bridge...");
                            mbridge = new BridgeBuilder("Wearable House Control", config.getMyUUID().toString())
                                    .setIpAddress(ip)
                                    .setConnectionType(BridgeConnectionType.LOCAL)
                                    .setBridgeConnectionCallback(bridgeConnectionCallback)
                                    .addBridgeStateUpdatedCallback(bridgeStateUpdatedCallback)
                                    .build();

                            connection = mbridge.getBridgeConnection(BridgeConnectionType.LOCAL);
                            connection.getConnectionOptions().enableFastConnectionMode(mbridge.getIdentifier());
                            connection.connect();
                        }

                    }
                });
            });
        }

    }

    @Override
    public void setLightColor(Integer color) throws ActionNotSupported {
        if (mbridge != null) {
            Log.d(TAG, "Setting the colour of PhilipsHue");
            BridgeState bs = mbridge.getBridgeState();

            List<LightPoint> lights = bs.getLights();

            if(lights.size() > mLightNumber){
                updateLight(lights.get(mLightNumber), SETTING_COLOR, color);
            }
        }
    }

    @Override
    public int getLightColor() throws ActionNotSupported{
        if (mbridge != null) {
            BridgeState bs = mbridge.getBridgeState();
            bs.refresh(BridgeStateCacheType.FULL_CONFIG, BridgeConnectionType.LOCAL);
            List<LightPoint> lights = bs.getLights();

            if (lights.size() > mLightNumber) {
                double[][] xys = new double[lights.size()][2];
                for (int i = 0; i < lights.size(); i++) {
                    xys[i][0] = lights.get(i).getLightState().getColor().getXY().x;
                    xys[i][1] = lights.get(i).getLightState().getColor().getXY().y;
                }
                int[] colors = HueColor.bulkConvertToRGBColors(xys, lights.get(mLightNumber));
                return colors[mLightNumber] | 0xFF000000;
            }
        }
        return 0;
    }


    public int getBrightness(){
        if (mbridge != null) {
            BridgeState bs = mbridge.getBridgeState();
            bs.refresh(BridgeStateCacheType.FULL_CONFIG, BridgeConnectionType.LOCAL);
            List<LightPoint> lights = bs.getLights();
            if (lights.size() > mLightNumber) {
                LightPoint light = lights.get(mLightNumber);
                return light.getLightState().getBrightness();
            }
        }
        return 0;
    }

    public boolean setBrightness(Integer val) {
        if (mbridge != null){
            BridgeState bs = mbridge.getBridgeState();
            List<LightPoint> lights = bs.getLights();

            if(lights.size() > mLightNumber){
                updateLight(lights.get(mLightNumber), SETTING_BRIGHTNESS, val);
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean enable() {

        if (mbridge != null){
            BridgeState bs = mbridge.getBridgeState();
            List<LightPoint> lights = bs.getLights();

            if(lights.size() > mLightNumber){
                updateLight(lights.get(mLightNumber), SETTING_ON, 1);
                return true;
            }
        }
        return false;
    }



    @Override
    public boolean disable() {
        if (mbridge != null){
            BridgeState bs = mbridge.getBridgeState();

            List<LightPoint> lights = bs.getLights();

            if(lights.size() > mLightNumber){
                updateLight(lights.get(mLightNumber), SETTING_ON, 0);
                return true;
            }
        }

        return false;
    }

    private void updateLight(LightPoint light, int option, int val){
        final LightState lightState = light.getLightState();
        switch (option){
            case SETTING_ON:
                Log.d(TAG, "Setting on");
                lightState.setOn(val!=0);
                break;

            case SETTING_BRIGHTNESS:
                Log.d(TAG, "Setting brightness");
                lightState.setBrightness(val);
                break;
            case SETTING_COLOR:
                Log.d(TAG, "Setting colour");
                int r = Color.red(val);
                int g = Color.green(val);
                int b = Color.blue(val);
                HueColor hc = new HueColor(new HueColor.RGB(r,g,b),
                        light.getLightConfiguration().getModelIdentifier(),
                        light.getLightConfiguration().getSwVersion());
                lightState.setXY(hc.getXY().x,hc.getXY().y);
                break;
        }

        light.updateState(lightState, BridgeConnectionType.LOCAL, new BridgeResponseCallback() {
            @Override
            public void handleCallback(Bridge bridge, ReturnCode returnCode, List<ClipResponse> list, List<HueError> errorList) {
                if (returnCode == ReturnCode.SUCCESS) {
                    Log.d(TAG, "Changed hue of light " + light.getIdentifier() + " to " + lightState.getHue());
                } else {
                    Log.e(TAG, "Error changing hue of light " + light.getIdentifier());
                    for (HueError error : errorList) {
                        Log.e(TAG, error.toString());
                    }
                }
            }
        });
    }

    @Override
    public boolean isEnabled() {
       if (mbridge != null){
           BridgeState bs = mbridge.getBridgeState();
           List<LightPoint> lights = bs.getLights();

           if(lights.size() > mLightNumber) {
               return lights.get(mLightNumber).getLightState().isOn();
           }else{
               return false;
           }
       }
        return false;
    }

    @Override
    public void setName(String name) {
        mName = name;
    }

    @Override
    public void addListener(DeviceChangeListener o){
        listeners.put(o, this);
    }

    @Override
    public void removeListener(DeviceChangeListener o){
        listeners.remove(o);
    }

    @Override
    public String getName() {
        if(mName != null){
            return mName;
        }else {
            return "Philips Hue";
        }
    }

    @Override
    public ControllableDeviceType getType() {
        return ControllableDeviceType.LIGHT;
    }

    @Override
    public UUID getID() {
        return mUUID;
    }

    @Override
    public boolean quickAction() {
        refreshConnection();
        return isEnabled() ? disable() : enable();
    }

    @Override
    public boolean extendedAction() {
        Intent lightControls = new Intent(mContext, LightControlPanelActivity.class);
        lightControls.putExtra("DeviceType", "HueLight"); //Deprecated - test without this
        lightControls.putExtra(LightControlPanelActivity.ID_EXTRA, this.getID());
        mContext.startActivity(lightControls);

        return true;
    }

    public boolean isConnected() {
        return (mbridge!=null && mbridge.isConnected() && mbridge.getBridgeState().getLightPoints().get(mLightNumber).getLightState().isReachable());
    }

    public boolean getRequiresAuthentication() {
        return requiresAuthentication;
    }

    public void refreshConnection() {
        if ((mbridge == null || !mbridge.isConnected()) && !findingBridge){
            //Load in parameters from configuration store
            ConfigurationStore.getInstance(mContext).onConfigAvailable(config -> {
                mbridgeDiscovery = new BridgeDiscovery();
                findingBridge = true;
                mbridgeDiscovery.search(new BridgeDiscoveryCallback() {
                    @Override
                    public void onFinished(List<BridgeDiscoveryResult> list, ReturnCode returnCode) {
                        findingBridge = false;
                        if (list.size() == 0){
                            Log.e(TAG, "No bridge found");
                            //TODO: Decide what to do here.
                        }else if (list.size() > 1){
                            Log.e(TAG, "Multiple bridges found. WHC does not support multiple bridges");
                            //TODO: Pick one?
                        }else{
                            BridgeDiscoveryResult bdr = list.get(0);
                            String ip = bdr.getIP();
                            Log.d(TAG, "One bridge found. Connecting to bridge...");
                            mbridge = new BridgeBuilder("Wearable House Control", config.getMyUUID().toString())
                                    .setIpAddress(ip)
                                    .setConnectionType(BridgeConnectionType.LOCAL)
                                    .setBridgeConnectionCallback(bridgeConnectionCallback)
                                    .addBridgeStateUpdatedCallback(bridgeStateUpdatedCallback)
                                    .build();

                            connection = mbridge.getBridgeConnection(BridgeConnectionType.LOCAL);
                            connection.getConnectionOptions().enableFastConnectionMode(mbridge.getIdentifier());
                            connection.connect();
                        }

                    }
                });
            });
        }
    }
}
