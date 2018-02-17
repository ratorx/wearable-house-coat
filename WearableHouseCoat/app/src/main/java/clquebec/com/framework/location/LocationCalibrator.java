package clquebec.com.framework.location;

import org.json.JSONObject;

/**
 * Created by reeto on 09/02/18.
 */

public interface LocationCalibrator {
    // TODO Fix return values to indicate success or failure - booleans seem lacking

    // Add a room to fingerprint binding to the system
    boolean calibrate(Room room);

    // String is to encode arbitrary data - Uses knowledge that underlying system is a server
    boolean calibrate(Room room, String data);

    //Like above, but encodes arbitrary JSON data
    boolean calibrate(Room room, JSONObject data);

    // Remove all data about Room to fingerprint bindings from the system
    boolean reset();
}