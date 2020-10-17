package main.com.iglobdriver.service;

/**
 * Created by ritesh on 20/3/17.
 */

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import main.com.iglobdriver.MainActivity;
import main.com.iglobdriver.R;
import main.com.iglobdriver.activity.ChatingAct;
import main.com.iglobdriver.activity.NotificationAct;
import main.com.iglobdriver.activity.SupportAct;
import main.com.iglobdriver.app.Config;
import main.com.iglobdriver.constant.MySession;
import main.com.iglobdriver.utils.NotificationUtils;

import static main.com.iglobdriver.utils.NotificationUtils.isAppIsInBackground;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;
    public static String notification_data = "";
    MySession mySession;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        storeRegIdInPref(refreshedToken);
        sendRegistrationToServer(refreshedToken);
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
    private void sendRegistrationToServer(final String token) {
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        Log.e(TAG, "storepref: " + token);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        mySession = new MySession(this);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean result = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && powerManager.isInteractive() || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH && powerManager.isScreenOn();

        if (!result) {
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MH24_SCREENLOCK");
            wl.acquire(10000);
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl_cpu = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MH24_SCREENLOCK");
            wl_cpu.acquire(10000);
        } else {
            PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
            wakeLock.acquire(10000);
        }


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                if (mySession.IsLoggedIn()) {
                    handleDataMessage(json);
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {

        if (!isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

        }
    }


    private void handleDataMessage(JSONObject json) {
        String format = "";
        // notification_data = "";
        notification_data = json.toString();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format = simpleDateFormat.format(new Date());
            Log.e(TAG, "push json: " + json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONObject data = json.getJSONObject("message");
            String keyMessage = data.getString("key").trim();
            Log.e("IN Service", "KEY: " + keyMessage);
            if (!isAppIsInBackground(getApplicationContext())) {
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", data.toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                if (keyMessage.equalsIgnoreCase("you have a new notification")) {
                    String msg = getResources().getString(R.string.newnotificationfromadmin);
                    Intent resultIntent = new Intent(getApplicationContext(), NotificationAct.class);
                    resultIntent.putExtra("message", data.toString());
                    showNotificationMessage(getApplicationContext(), getResources().getString(R.string.app_name), "" + msg, format, resultIntent, null);

                } else if (keyMessage.equalsIgnoreCase("You have a new message")) {
                    if (!ChatingAct.isInFront) {
                        String msg = getResources().getString(R.string.newmessagecome);
                        if (data.getString("type").equalsIgnoreCase("Support")) {
                            msg = getResources().getString(R.string.supportmessage);
                            String name = getResources().getString(R.string.support);
                            Intent resultIntent = new Intent(getApplicationContext(), SupportAct.class);
                            resultIntent.putExtra("receiver_id", data.getString("user_id"));
                            resultIntent.putExtra("receiver_name", name);
                            resultIntent.putExtra("receiver_img", data.getString("userimage"));
                            resultIntent.putExtra("request_id", data.getString("request_id"));
                            resultIntent.putExtra("block_status", "");
                            showNotificationMessage(getApplicationContext(), getResources().getString(R.string.app_name), "" + msg, format, resultIntent, null);
                        } else {
                            String name = data.getString("first_name") + " " + data.getString("last_name");
                            Intent resultIntent = new Intent(getApplicationContext(), ChatingAct.class);
                            resultIntent.putExtra("receiver_id", data.getString("user_id"));
                            resultIntent.putExtra("receiver_name", name);
                            resultIntent.putExtra("receiver_img", data.getString("userimage"));
                            resultIntent.putExtra("request_id", data.getString("request_id"));
                            resultIntent.putExtra("block_status", "");
                            showNotificationMessage(getApplicationContext(), getResources().getString(R.string.app_name), "" + msg, format, resultIntent, null);
                        }
                    }
                } else {
                    if (!keyMessage.equalsIgnoreCase("Tip amount is updated")){
                        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                        notificationUtils.playNotificationSound();
                    }



                }


            } else {
                String msg = keyMessage;
                Log.e("Comming","===========>"+keyMessage);
                if (keyMessage.equalsIgnoreCase("your booking request is Now")) {
                    msg = getResources().getString(R.string.bookingrequest);
                    Log.e("Comming","===========>"+true);
                    KeyguardManager manager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
                    KeyguardManager.KeyguardLock lock = manager.newKeyguardLock("abc");
                    lock.disableKeyguard();
                    Intent i = new Intent(this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setType(Intent.ACTION_SCREEN_ON);
                    startActivity(i);
                } else if (keyMessage.equalsIgnoreCase("you have a new notification")) {
                    msg = getResources().getString(R.string.newnotificationfromadmin);
                    Intent resultIntent = new Intent(getApplicationContext(), NotificationAct.class);
                    resultIntent.putExtra("message", data.toString());
                    showNotificationMessage(getApplicationContext(), getResources().getString(R.string.app_name), "" + msg, format, resultIntent, null);

                } else if (keyMessage.equalsIgnoreCase("You have a new message")) {
                    msg = getResources().getString(R.string.newmessagecome);
                    if (data.getString("type").equalsIgnoreCase("Support")) {
                        msg = getResources().getString(R.string.supportmessage);
                        String name = getResources().getString(R.string.support);
                        Intent resultIntent = new Intent(getApplicationContext(), SupportAct.class);
                        resultIntent.putExtra("receiver_id", data.getString("user_id"));
                        resultIntent.putExtra("receiver_name", name);
                        resultIntent.putExtra("receiver_img", data.getString("userimage"));
                        resultIntent.putExtra("request_id", data.getString("request_id"));
                        resultIntent.putExtra("block_status", "");

                        showNotificationMessage(getApplicationContext(), getResources().getString(R.string.app_name), "" + msg, format, resultIntent, null);

                    } else {
                        String name = data.getString("first_name") + " " + data.getString("last_name");
                        Intent resultIntent = new Intent(getApplicationContext(), ChatingAct.class);
                        resultIntent.putExtra("receiver_id", data.getString("user_id"));
                        resultIntent.putExtra("receiver_name", name);
                        resultIntent.putExtra("receiver_img", data.getString("userimage"));
                        resultIntent.putExtra("request_id", data.getString("request_id"));
                        resultIntent.putExtra("block_status", "");

                        showNotificationMessage(getApplicationContext(), getResources().getString(R.string.app_name), "" + msg, format, resultIntent, null);

                    }
                } else if (keyMessage.equalsIgnoreCase("drop point is added")) {
                    msg = getResources().getString(R.string.newdroppointadded);
                    //msg="New drop point is added by rider.";
                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    resultIntent.putExtra("message", data.toString());
                    showNotificationMessage(getApplicationContext(), getResources().getString(R.string.app_name), "" + msg, format, resultIntent, null);

                } else if (keyMessage.equalsIgnoreCase("your booking request is cancel by user")) {
                    msg = getResources().getString(R.string.bookingrequestcanceledbyuser);
                    // msg="Booking request has been canceled by user.";
                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    resultIntent.putExtra("message", data.toString());
                    showNotificationMessage(getApplicationContext(), getResources().getString(R.string.app_name), "" + msg, format, resultIntent, null);

                } else if (keyMessage.equalsIgnoreCase("your booking request is Cancel")) {
                    msg = getResources().getString(R.string.bookingrequestcanceledbyuser);
                    //  msg="Booking request has been canceled by user.";
                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    resultIntent.putExtra("message", data.toString());
                    showNotificationMessage(getApplicationContext(), getResources().getString(R.string.app_name), "" + msg, format, resultIntent, null);


                } else if (keyMessage.equalsIgnoreCase("your ride is update")) {
                    msg = getResources().getString(R.string.droplocationupdated);
                    //msg="Drop point is updated by user.";
                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    resultIntent.putExtra("message", data.toString());
                    showNotificationMessage(getApplicationContext(), getResources().getString(R.string.app_name), "" + msg, format, resultIntent, null);

                } else {
                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    resultIntent.putExtra("message", data.toString());
                    showNotificationMessage(getApplicationContext(), getResources().getString(R.string.app_name), "" + msg, format, resultIntent, null);

                }


//shared user detail
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }


    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent, String route_img) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, route_img);

    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }


}