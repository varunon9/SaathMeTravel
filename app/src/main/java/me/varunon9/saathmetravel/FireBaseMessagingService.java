package me.varunon9.saathmetravel;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.varunon9.saathmetravel.constants.AppConstants;
import me.varunon9.saathmetravel.utils.ContextUtility;
import me.varunon9.saathmetravel.utils.GeneralUtility;

public class FireBaseMessagingService extends FirebaseMessagingService {

    private String TAG = "FireBaseMessagingService";
    private GeneralUtility generalUtility;
    private ContextUtility contextUtility;

    public FireBaseMessagingService() {
        Log.d(TAG, "constructor called");
        generalUtility = new GeneralUtility();
        contextUtility = new ContextUtility(this);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, token);

        // store token to users collection
        contextUtility.storeFcmTokenInSharedPreference(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            generalUtility.showLocalNotification(remoteMessage.getNotification());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject data = new JSONObject(remoteMessage.getData().toString());
                generalUtility.handlePushNotificationDataMessage(data);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }
}
