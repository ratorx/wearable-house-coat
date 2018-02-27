package com.clquebec.framework.listenable;

public class Track {

    public final String trackName;
    public final String artist;
    public final String album;

    // Set these to empty string if not available
    public Track(String name, String artistName, String albumName){
        trackName = name;
        artist = artistName;
        album = albumName;
    }

}
