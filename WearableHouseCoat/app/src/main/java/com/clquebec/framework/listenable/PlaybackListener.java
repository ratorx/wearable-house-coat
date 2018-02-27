package com.clquebec.framework.listenable;

/**
 * Created by me on 27/02/2018.
 */

public interface PlaybackListener {

    void updateResource(String resource);

    void updateIsPlaying(String resource);

    void updateVolume(float volume);

    void updateArtLocation(String location);
}
