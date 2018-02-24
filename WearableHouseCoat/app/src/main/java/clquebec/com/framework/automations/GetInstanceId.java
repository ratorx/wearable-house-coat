package clquebec.com.framework.automations;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * WearableHouseCoat
 * Author: tom
 * Creation Date: 24/02/18
 */

public class GetInstanceId extends FirebaseInstanceIdService {
    private static String TAG = "GetInstanceId";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        //sendRegistrationToServer(refreshedToken);
    }
}
