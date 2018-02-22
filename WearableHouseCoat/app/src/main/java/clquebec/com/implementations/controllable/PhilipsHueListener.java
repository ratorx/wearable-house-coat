package clquebec.com.implementations.controllable;

import com.philips.lighting.hue.sdk.wrapper.domain.BridgeState;

/**
 * Created by me on 22/02/2018.
 */

public interface PhilipsHueListener {

    public void updateState(BridgeState bs);
}
