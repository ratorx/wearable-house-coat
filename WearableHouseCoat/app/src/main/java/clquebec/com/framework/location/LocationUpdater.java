package clquebec.com.framework.location;

/**
 * Created by reeto on 09/02/18.
 */

public interface LocationUpdater {
    // Send current location fingerprint (+ name) to server
    public boolean update();
}