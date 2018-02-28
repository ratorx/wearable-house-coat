package com.clquebec.framework.automations;

import android.util.Log;

import com.clquebec.framework.controllable.ControllableDevice;
import com.clquebec.framework.storage.ConfigurationStore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AutomationMessageHandler extends FirebaseMessagingService {
    private static final String TAG = "AutomationMessageHandler";

    private static final String DEVICEIDKEY = "device";
    private static final String METHODKEY = "method";
    private static final String PARAMETERKEY = "parameters";
    private static final String ARGUMENTKEY = "arguments";

    private static final String ACTIONLISTKEY = "actions";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            //Call an action on the device
            ConfigurationStore.getInstance(this).onConfigAvailable(config -> {
                try{
                    JSONArray actions = new JSONArray(remoteMessage.getData().get(ACTIONLISTKEY));

                    for(int j = 0; j < actions.length(); j++){
                        try {
                            JSONObject actionData = actions.getJSONObject(j);

                            UUID deviceId = UUID.fromString(actionData.getString(DEVICEIDKEY));
                            ControllableDevice device = config.getDevice(deviceId);

                            //Get types and their args
                            JSONArray types = actionData.getJSONArray(PARAMETERKEY);
                            JSONArray args = actionData.getJSONArray(ARGUMENTKEY);

                            //Parse parameters, instantiating their type with a single string arg.
                            List<Object> parameters = new ArrayList<>();
                            List<Class<?>> parameterTypes = new ArrayList<>();
                            for (int i = 0; i < types.length(); i++) {
                                String type = types.getString(i);
                                String arg = args.getString(i);

                                //Construct type(arg) and add it to the Object list
                                Class<?> parameterClass = Class.forName(type);
                                parameterTypes.add(parameterClass);

                                Object object = parameterClass.getConstructor(String.class).newInstance(arg);

                                parameters.add(object);
                            }

                            String methodName = actionData.getString(METHODKEY);

                            //Invoke the method with the required arguments
                            device.getClass().getMethod(
                                    methodName,
                                    parameterTypes.toArray(new Class<?>[parameterTypes.size()])
                            ).invoke(device, parameters.toArray(new Object[parameters.size()]));
                        }catch(JSONException e){
                            Log.e(TAG, "Problem unmarshalling action: "+e.getMessage());
                        }
                    }
                }catch(JSONException | NullPointerException e){
                    Log.e(TAG, "Problem unmarshalling Action list "+e.getMessage());
                } catch (InstantiationException e) {
                    Log.e(TAG, "Could not instantiate a parameter: " + e.getMessage());
                } catch (InvocationTargetException e) {
                    Log.e(TAG, "InvocationTargetException: " + e.getMessage());
                } catch (NoSuchMethodException e) {
                    Log.e(TAG, "Given device ID does not have given method: " + e.getMessage());
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "Can't call method or instantiate a parameter: " + e.getMessage());
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, "One of the parameters was not found: " + e.getMessage());
                }
            });
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
}
