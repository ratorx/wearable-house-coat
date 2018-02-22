package clquebec.com.implementations.controllable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.List;

import java.util.UUID;

import clquebec.com.framework.controllable.ActionNotSupported;
import clquebec.com.framework.controllable.ControllableDeviceType;
import clquebec.com.framework.controllable.ControllableLightDevice;
import clquebec.com.wearablehousecoat.LightControlPanelActivity;


/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public class PhilipsHue implements ControllableLightDevice {
    //TODO: Implement this.
    private static Bridge mbridge = null;
    private static List<PhilipsHueListener> listeners = new ArrayList<>();
    private static BridgeConnection connection;
    private Context mContext;
    private UUID mUUID;



    private BridgeConnectionCallback bridgeConnectionCallback = new BridgeConnectionCallback() {
        @Override
        public void onConnectionEvent(BridgeConnection bridgeConnection, ConnectionEvent connectionEvent) {
            Log.d("Hue", "Connection event: " + connectionEvent);
        }

        public void onConnectionError(BridgeConnection bridgeConnection, List<HueError> hueErrors){
            for (HueError error : hueErrors) {
                Log.e("Hue", "Connection error: " + error.toString());
            }
        }
    };

    private BridgeStateUpdatedCallback bridgeStateUpdatedCallback = new BridgeStateUpdatedCallback() {
        @Override
        public void onBridgeStateUpdated(Bridge bridge, BridgeStateUpdatedEvent bridgeStateUpdatedEvent) {
            Log.i("Hue", "Bridge state updated event: " + bridgeStateUpdatedEvent);
            if (bridgeStateUpdatedEvent == BridgeStateUpdatedEvent.INITIALIZED){
                HeartbeatManager hbm = connection.getHeartbeatManager();
                if (hbm != null) {
                    Log.d("Hue", "Starting heartbeats...");
                    hbm.startHeartbeat(BridgeStateCacheType.LIGHTS_AND_GROUPS, 1000);
                }
            } else {
                mbridge.getBridgeState().refresh(BridgeStateCacheType.FULL_CONFIG, BridgeConnectionType.REMOTE_LOCAL);
                for (PhilipsHueListener l : listeners){
                    Log.d("Hue", "Attempting to run listeners");
                    l.updateState(mbridge.getBridgeState());
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
        //connect to mbridge. This is currently hardcoded. Need to implement discovery
        if (mbridge == null){
            mbridge = new BridgeBuilder("Wearable House Control", "CLWatch-other")
                    .setIpAddress("192.168.14.220")
                    .setConnectionType(BridgeConnectionType.LOCAL)
                    .setBridgeConnectionCallback(bridgeConnectionCallback)
                    .addBridgeStateUpdatedCallback(bridgeStateUpdatedCallback)
                    .build();

            Log.d("Hue", "Connected to mbridge");
            connection = mbridge.getBridgeConnection(BridgeConnectionType.LOCAL);
            connection.getConnectionOptions().enableFastConnectionMode(mbridge.getIdentifier());
            connection.connect();




        }

    }

    @Override
    public void setLightColor(int color) throws ActionNotSupported {
        Log.d("Hue", "Running this");
        BridgeState bs = mbridge.getBridgeState();

        List<LightPoint> lights = bs.getLights();

        for (LightPoint light : lights) {

            final LightState lightState = light.getLightState();


            //lightState.setOn(true);
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            HueColor hc = new HueColor(new HueColor.RGB(r,g,b),
                    light.getLightConfiguration().getModelIdentifier(),
                    light.getLightConfiguration().getSwVersion());
            lightState.setXY(hc.getXY().x,hc.getXY().y);

            light.updateState(lightState, BridgeConnectionType.LOCAL, new BridgeResponseCallback() {
                @Override
                public void handleCallback(Bridge bridge, ReturnCode returnCode, List<ClipResponse> list, List<HueError> errorList) {
                    if (returnCode == ReturnCode.SUCCESS) {
                        Log.i("Hue", "Changed hue of light " + light.getIdentifier() + " to " + lightState.getHue());
                    } else {
                        Log.e("Hue", "Error changing hue of light " + light.getIdentifier());
                        for (HueError error : errorList) {
                            Log.e("Hue", error.toString());
                        }
                    }
                }
            });
        }
    }

    public int getColor() {
            BridgeState bs = mbridge.getBridgeState();
            bs.refresh(BridgeStateCacheType.FULL_CONFIG, BridgeConnectionType.LOCAL);
            List<LightPoint> lights = bs.getLights();

            if (lights.size() > 0){
               LightPoint testLight = lights.get(0);
                HueColor.RGB rgb = testLight.getLightState().getColor().getRGB();

                return getIntFromColor(rgb.r, rgb.g, rgb.b);
            }

        return 20;
    }

    private int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

    public int getBrightness(){
        BridgeState bs = mbridge.getBridgeState();
        bs.refresh(BridgeStateCacheType.FULL_CONFIG, BridgeConnectionType.LOCAL);
        List<LightPoint> lights = bs.getLights();
        if (lights.size() > 0) {
            LightPoint testLight = lights.get(0);
            return testLight.getLightState().getBrightness();
        }

        return 0;
    }

    public boolean setBrightness(int val) {
        BridgeState bs = mbridge.getBridgeState();
        List<LightPoint> lights = bs.getLights();

        for (LightPoint light : lights){
            final LightState lightState = light.getLightState();
            lightState.setBrightness(val);
            light.updateState(lightState, BridgeConnectionType.LOCAL, new BridgeResponseCallback() {
                @Override
                public void handleCallback(Bridge bridge, ReturnCode returnCode, List<ClipResponse> list, List<HueError> errorList) {
                    if (returnCode == ReturnCode.SUCCESS) {
                        Log.i("Hue", "Changed hue of light " + light.getIdentifier() + " to " + lightState.getHue());
                    } else {
                        Log.e("Hue", "Error changing hue of light " + light.getIdentifier());
                        for (HueError error : errorList) {
                            Log.e("Hue", error.toString());
                        }
                    }
                }
            });
        }
        return true;
    }


    @Override
    public boolean enable() {
        BridgeState bs = mbridge.getBridgeState();
        List<LightPoint> lights = bs.getLights();

        for (LightPoint light : lights){
            final LightState lightState = light.getLightState();
            lightState.setBrightness(255);
            lightState.setOn(true);
            light.updateState(lightState, BridgeConnectionType.LOCAL, new BridgeResponseCallback() {
                @Override
                public void handleCallback(Bridge bridge, ReturnCode returnCode, List<ClipResponse> list, List<HueError> errorList) {
                    if (returnCode == ReturnCode.SUCCESS) {
                        Log.i("Hue", "Changed hue of light " + light.getIdentifier() + " to " + lightState.getHue());
                    } else {
                        Log.e("Hue", "Error changing hue of light " + light.getIdentifier());
                        for (HueError error : errorList) {
                            Log.e("Hue", error.toString());
                        }
                    }
                }
            });
        }
        return true;
    }



    @Override
    public boolean disable() {
        BridgeState bs = mbridge.getBridgeState();

        List<LightPoint> lights = bs.getLights();

        for (LightPoint light : lights) {

            final LightState lightState = light.getLightState();
            lightState.setOn(false);
            light.updateState(lightState, BridgeConnectionType.LOCAL, new BridgeResponseCallback() {
                @Override
                public void handleCallback(Bridge bridge, ReturnCode returnCode, List<ClipResponse> list, List<HueError> errorList) {
                    if (returnCode == ReturnCode.SUCCESS) {
                        Log.i("Hue", "Changed hue of light " + light.getIdentifier() + " to " + lightState.getHue());
                    } else {
                        Log.e("Hue", "Error changing hue of light " + light.getIdentifier());
                        for (HueError error : errorList) {
                            Log.e("Hue", error.toString());
                        }
                    }
                }
            });

        }
        return true;
    }

    @Override
    public boolean isEnabled() {
       BridgeState bs = mbridge.getBridgeState();
       List<LightPoint> lights = bs.getLights();

       return lights.get(0).getLightState().isOn();
    }

    @Override
    public void setName(String name) {

    }

    public static void addListener(PhilipsHueListener o){
        listeners.add(o);
    }

    public void removeListener(PhilipsHueListener o){
        listeners.remove(o);
    }

    @Override
    public String getName() {
        return "Philips Hue";
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
        lightControls.putExtra("DeviceType", "HueLight");
        mContext.startActivity(lightControls);

        return true;
    }
}
