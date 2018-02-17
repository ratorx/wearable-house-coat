package clquebec.com.framework.controllable;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 * <p>
 * An interface for playback devices
 */

public interface ControllablePlaybackDevice extends ControllableDevice {

    //Sets the currently playing resource
    void setResource(String resource);

    //Gets the currently playing resource
    String getResource();

    boolean setVolume(float volume) throws ActionNotSupported;

    //Gets the volume
    float getVolume() throws ActionNotSupported;

    //Sets the brightness
    boolean setBrightness(float brightness) throws ActionNotSupported;

    //Gets the brightness
    float getBrightness() throws ActionNotSupported;
}
