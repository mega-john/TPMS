package com.cz.usbserial.activity;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.cz.usbserial.util.Tools;

import java.util.Timer;
import java.util.TimerTask;

public class HeartbeatServer extends Service {
    static final String EXIT_APP_ACTION = "com.cz.action.exit_app";
    private final String TAG = HeartbeatServer.class.getSimpleName();
    Context mContext = this;
    Timer mTimer = null;
    TimerTask mTimerTask = null;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(HeartbeatServer.EXIT_APP_ACTION)) {
                Log.i(HeartbeatServer.this.TAG, "cz com.cz.action.exit_app");
                try {
                    Intent ii = new Intent(HeartbeatServer.this.mContext, TpmsServer.class);
                    if (ii != null) {
                        HeartbeatServer.this.mContext.stopService(ii);
                    }
                    HeartbeatServer.this.stopSelf();
                } catch (Exception e) {
                }
            }
        }
    };

    private boolean isRunningForeground(Context context) {
        String currentPackageName = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1).get(0).topActivity.getPackageName();
        if (TextUtils.isEmpty(currentPackageName) || !currentPackageName.equals(getPackageName())) {
            return false;
        }
        return true;
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Log.e(this.TAG, "cz onCreate ");
        IntentFilter filter = new IntentFilter();
        filter.addAction(EXIT_APP_ACTION);
        this.mContext.registerReceiver(this.mReceiver, filter);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(this.TAG, "cz onStartCommand ");
        stopTimer();
        startTimer();
        setTimeNs(1);
        return Service.START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    private void startTimer() {
        if (this.mTimer == null) {
            this.mTimer = new Timer();
        }
        if (this.mTimerTask == null) {
            this.mTimerTask = new TimerTask() {
                public void run() {
                    if (!Tools.isUSBService(HeartbeatServer.this.mContext)) {
                        Intent intent = new Intent(HeartbeatServer.this.mContext, TpmsServer.class);
                        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY | Intent.FLAG_RECEIVER_REPLACE_PENDING);
                        HeartbeatServer.this.mContext.startService(intent);
                    }
                }
            };
        }
    }

    public void setTimeNs(long dalayms) {
        if (this.mTimer != null && this.mTimerTask != null) {
            this.mTimer.schedule(this.mTimerTask, dalayms * 1000, 1000 * dalayms);
        }
    }

    private void stopTimer() {
        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.mTimer = null;
        }
        if (this.mTimerTask != null) {
            this.mTimerTask.cancel();
            this.mTimerTask = null;
        }
    }
}
