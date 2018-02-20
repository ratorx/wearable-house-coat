package clquebec.com.framework.location;

/**
 * Created by reeto on 09/02/18.
 */

public interface LocationCalibrator {
    // TODO Fix return values to indicate success or failure - booleans seem lacking

    // Add a room to fingerprint binding to the system
    boolean calibrate(Place place);

    // Remove all data about Room to fingerprint bindings from the system
    boolean reset();
}
