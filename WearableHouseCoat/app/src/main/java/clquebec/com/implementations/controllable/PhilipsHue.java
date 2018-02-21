package clquebec.com.implementations.controllable;

import android.content.Context;
import android.util.Log;

import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnection;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionType;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeResponseCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateUpdatedCallback;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateUpdatedEvent;
import com.philips.lighting.hue.sdk.wrapper.connection.ConnectionEvent;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.domain.BridgeBuilder;
import com.philips.lighting.hue.sdk.wrapper.domain.BridgeState;
import com.philips.lighting.hue.sdk.wrapper.domain.HueError;
import com.philips.lighting.hue.sdk.wrapper.domain.ReturnCode;
import com.philips.lighting.hue.sdk.wrapper.domain.clip.ClipResponse;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightPoint;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightState;
import com.philips.lighting.hue.sdk.wrapper.utilities.HueColor;

import java.util.List;

import java.util.UUID;

import clquebec.com.framework.controllable.ActionNotSupported;
import clquebec.com.framework.controllable.ControllableDeviceType;
import clquebec.com.framework.controllable.ControllableLightDevice;


/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 */

public class PhilipsHue implements ControllableLightDevice {
    //TODO: Implement this.
    private static Bridge bridge = null;
    private boolean enabled = true;

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
        }
    };

    public PhilipsHue(Context c) {
        //Scan for Hues on local network

        //Pick the right one(s)

        //Initialise internal state.
        //connect to bridge. This is currently hardcoded. Need to implement discovery
        if (bridge == null){
            bridge = new BridgeBuilder("Wearable House Control", "CLWatch")
                    .setIpAddress("192.168.14.136")
                    .setConnectionType(BridgeConnectionType.LOCAL)
                    .setBridgeConnectionCallback(bridgeConnectionCallback)
                    .addBridgeStateUpdatedCallback(bridgeStateUpdatedCallback)
                    .build();

            Log.d("Hue", "Connected to bridge");
            bridge.connect();
            Log.d("Hue", bridge.toString());
        }

    }

    @Override
    public void setLightColor(int color) throws ActionNotSupported {

    }

    @Override
    public boolean enable() {
        Log.d("Hue", "Running this");
        BridgeState bs = bridge.getBridgeState();

        List<LightPoint> lights = bs.getLights();

        for (LightPoint light : lights) {

            final LightState lightState = new LightState();

            //lightState.setOn(true);
            HueColor hc = new HueColor(new HueColor.RGB(255,0,217),
                    light.getLightConfiguration().getModelIdentifier(),
                    light.getLightConfiguration().getSwVersion());
            lightState.setXY(hc.getXY().x,hc.getXY().y);
            Log.d("Hue", "sent turn on request");

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
        enabled = true;
        return true;
    }



    @Override
    public boolean disable() {
        BridgeState bs = bridge.getBridgeState();

        List<LightPoint> lights = bs.getLights();

        for (LightPoint light : lights) {

            final LightState lightState = new LightState();

            HueColor hc = new HueColor(new HueColor.RGB(0,255,0),
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
        enabled = false;
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setName(String name) {

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
        return UUID.randomUUID();
    }

    @Override
    public boolean quickAction() {
        return isEnabled() ? disable() : enable();
    }

    @Override
    public boolean extendedAction() {
        return false;
    }
}
