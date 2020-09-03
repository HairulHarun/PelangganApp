package com.gorontalo.chair.pelangganapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.gorontalo.chair.pelangganapp.MainActivity;
import com.gorontalo.chair.pelangganapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    public static final int ID_SMALL_NOTIFICATION = 235;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void sendPushNotification(JSONObject json) {
        //optionally we can display the json into log
        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            //getting the json data
            JSONObject data = json.getJSONObject("data");

            //parsing json data
            String id = data.getString("id");
            String pelanggan = data.getString("pelanggan");
            String hp_pelanggan = data.getString("hp_pelanggan");
            String alamat_pelanggan = data.getString("alamat_pelanggan");
            String photo_pelanggan = data.getString("photo_pelanggan");
            String outlet = data.getString("outlet");
            String jarak = data.getString("jarak");
            String latitude = data.getString("lat");
            String longitude = data.getString("long");
            String lat_outlet = data.getString("lat_outlet");
            String long_outlet = data.getString("long_outlet");
            String jumlah = data.getString("jumlah");
            String status = data.getString("status");

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            showSmallNotification("Pekerjaan", status, intent);

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    public void showSmallNotification(String title, String message, Intent intent) {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        ID_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setContentText(message)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);
    }
}
