package com.clquebec.framework.listenable;

/**
 * Created by me on 27/02/2018.
 */

public interface PlaybackListener {

    void updateResource(Track resource);

    void updateIsPlaying(boolean playing);

    void updateVolume(int volume);

    void updateArtLocation(String location);
}
