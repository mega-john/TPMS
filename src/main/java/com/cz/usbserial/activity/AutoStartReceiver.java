package com.cz.usbserial.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

public class AutoStartReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("BOOT_STAUS", true)) {
            Intent i = new Intent(context, TpmsServer.class);
            i.addFlags(1610612736);
            if (i != null) {
                context.startService(i);
            }
            Log.e("boot", "boot ----------->");
        }
    }
}
