package com.clquebec.framework.controllable;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 03/02/18
 * <p>
 * An interface for playback devices
 */

public interface ControllablePlaybackDevice extends ControllableDevice {

    //Gets the currently playing resource
    String getResource();

    // Returns true if there's another item queued for playing (so the skip button can
    // do something worthwhile).

    // Skips to the next item (song/video)
    boolean skipNext() throws ActionNotSupported;

    // Returns to the previous item
    boolean skipPrevious() throws ActionNotSupported;

    // Pauses/resumes playback
    boolean togglePlaying();

    //Gets whether the device is currently playing
    boolean getPlaying() throws ActionNotSupported;

    // Sets the volume
    boolean setVolume(float volume) throws ActionNotSupported;

    //Gets the volume
    int getVolume() throws ActionNotSupported;

    String getArtLocation();

}
