package com.smsapp.smsapp;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by pawim on 01.04.2016.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        //super.onTokenRefresh();
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).

    }
}