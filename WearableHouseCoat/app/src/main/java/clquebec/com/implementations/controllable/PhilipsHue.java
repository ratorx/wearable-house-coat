package clquebec.com.implementations.controllable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;

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
import com.philips.lighting.hue.sdk.wrapper.domain.ClipAttribute;
import com.philips.lighting.hue.sdk.wrapper.domain.HueError;
import com.philips.lighting.hue.sdk.wrapper.domain.ReturnCode;
import com.philips.lighting.hue.sdk.wrapper.domain.clip.ClipResponse;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightPoint;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightState;
import com.philips.lighting.hue.sdk.wrapper.utilities.HueColor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import clquebec.com.framework.controllable.ActionNotSupported;
import clquebec.com.framework.controllable.ControllableDeviceType;
import clquebec.com.framework.controllable.ControllableLightDevice;
import clquebec.com.framework.listenable.DeviceChangeListener;
import clquebec.com.framework.listenable.ListenableDevice;
import clquebec.com.framework.storage.ConfigurationStore;
import clquebec.com.wearablehousecoat.LightControlPanelActivity;


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

    private static Bridge mbridge = null;
    private static Map<DeviceChangeListener, PhilipsHue> listeners = new HashMap<>();
    private static BridgeConnection connection;

    private Context mContext;
    private UUID mUUID;
    private String mName;


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
                    hbm.startHeartbeat(BridgeStateCacheType.LIGHTS_AND_GROUPS, 1000);
                }
            } else if (bridgeStateUpdatedEvent == BridgeStateUpdatedEvent.LIGHTS_AND_GROUPS){
               // mbridge.getBridgeState().refresh(BridgeStateCacheType.LIGHTS_AND_GROUPS, BridgeConnectionType.REMOTE_LOCAL);

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
    }

    public PhilipsHue(Context c) {
        //Scan for Hues on local network

        //Pick the right one(s)

        //Initialise internal state.
        mContext = c;
        //TODO: Allow addressing of specific Hue lights
        if (mbridge == null){
            //Load in parameters from configuration store
            ConfigurationStore.getInstance(c).onConfigAvailable(config -> {
                BridgeDiscovery bridgeDiscovery = new BridgeDiscovery();
                bridgeDiscovery.search(new BridgeDiscoveryCallback() {
                    @Override
                    public void onFinished(List<BridgeDiscoveryResult> list, ReturnCode returnCode) {
                        if (list.size() == 0){
                            Log.e("Hue", "No bridge found");
                            //TODO: Decide what to do here.
                        }else if (list.size() > 1){
                            Log.e("Hue", "Multiple bridges found. WHC does not support" +
                                    "multiple bridges");
                        }else{
                            BridgeDiscoveryResult bdr = list.get(0);
                            String ip = bdr.getIP();
                            Log.d("Hue", "One bridge found. Connecting to bridge...");
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
    public void setLightColor(int color) throws ActionNotSupported {
        if (mbridge != null) {
            Log.d(TAG, "Setting the colour of PhilipsHue");
            BridgeState bs = mbridge.getBridgeState();

            List<LightPoint> lights = bs.getLights();

            for (LightPoint light : lights) {
                updateLight(light, SETTING_COLOR, color);
            }
        }
    }

    @Override
    public int getLightColor() throws ActionNotSupported{
        if (mbridge != null) {
            BridgeState bs = mbridge.getBridgeState();
            bs.refresh(BridgeStateCacheType.FULL_CONFIG, BridgeConnectionType.LOCAL);
            List<LightPoint> lights = bs.getLights();

            if (lights.size() > 0) {
                double[][] xys = new double[lights.size()][2];
                for (int i = 0; i < lights.size(); i++) {
                    xys[i][0] = lights.get(i).getLightState().getColor().getXY().x;
                    xys[i][1] = lights.get(i).getLightState().getColor().getXY().y;
                }
                int[] colors = HueColor.bulkConvertToRGBColors(xys, lights.get(0));
                return colors[0] | 0xFF000000;
            }
        }
        return 0;
    }


    public int getBrightness(){
        if (mbridge != null) {
            BridgeState bs = mbridge.getBridgeState();
            bs.refresh(BridgeStateCacheType.FULL_CONFIG, BridgeConnectionType.LOCAL);
            List<LightPoint> lights = bs.getLights();
            if (lights.size() > 0) {
                LightPoint testLight = lights.get(0);
                return testLight.getLightState().getBrightness();
            }
        }
        return 0;
    }

    public boolean setBrightness(int val) {
        if (mbridge != null){
            BridgeState bs = mbridge.getBridgeState();
            List<LightPoint> lights = bs.getLights();

            for (LightPoint light : lights){
                updateLight(light, SETTING_BRIGHTNESS, val);
            }
            return true;
        }
        return false;
    }


    @Override
    public boolean enable() {

        if (mbridge != null){
            BridgeState bs = mbridge.getBridgeState();
            List<LightPoint> lights = bs.getLights();

            for (LightPoint light : lights){
                updateLight(light, SETTING_ON, 1);
            }
            return true;
        }
        return false;
    }



    @Override
    public boolean disable() {
        if (mbridge != null){
            BridgeState bs = mbridge.getBridgeState();

            List<LightPoint> lights = bs.getLights();

            for (LightPoint light : lights) {

                updateLight(light, SETTING_ON, 0);


            }
            return true;
        }

        return false;
    }

    private void updateLight(LightPoint light, int option, int val){
        final LightState lightState = light.getLightState();
        switch (option){
            case SETTING_ON:
                lightState.setOn((val!=0));
                break;

            case SETTING_BRIGHTNESS:
                lightState.setBrightness(val);
                break;
            case SETTING_COLOR:
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

           if(lights.size() > 0) {
               return lights.get(0).getLightState().isOn();
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
        if (mbridge==null){
            return false;
        }else{
            return mbridge.isConnected();
        }
    }
}
