package com.gorontalo.chair.pelangganapp.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.gorontalo.chair.pelangganapp.adapter.SessionAdapter;

public class GettingDeviceTokenService extends FirebaseInstanceIdService {
    private SessionAdapter sessionAdapter;

    @Override
    public void onTokenRefresh() {
        String DeviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("DeviceToken ==> ",  DeviceToken);

        sessionAdapter = new SessionAdapter(getApplicationContext());
        sessionAdapter.simpanToken(DeviceToken);
    }
}
