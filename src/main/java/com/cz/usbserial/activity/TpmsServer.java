package com.cz.usbserial.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.cz.usbserial.driver.SerialInputOutputManager;
import com.cz.usbserial.driver.UsbSerialDriver;
import com.cz.usbserial.driver.UsbSerialPort;
import com.cz.usbserial.driver.UsbSerialProber;
import com.cz.usbserial.tpms.R;
import com.cz.usbserial.util.Tools;
import com.cz.usbserial.util.UnitTools;
import com.cz.usbserial.view.Alart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TpmsServer extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final int HP_PROGRESS = 120;
    public static final int HP_PROGRESS_STATAR = 100;
    public static final int HT_PROGRESS = 65;
    public static final int LP_PROGRESS = 80;
    public static final int LP_PROGRESS_STATAR = 100;
    public static final int LT_PROGRESS_STATAR = 10;
    public static final int P_UNIT = 2;
    public static final int T_UNIT = 0;
    static final String EXIT_APP_ACTION = "com.cz.action.exit_app";
    private static final String TAG = TpmsServer.class.getSimpleName();
    private static final String ACTION_USB_PERMISSION = "com.android.cz.USB_PERMISSION";
    private static final int MESSAGE_DATA = 105;
    private static final int MESSAGE_HANDSHAKE_NO = 107;
    private static final int MESSAGE_HANDSHAKE_OK = 106;
    private static final int MESSAGE_USB_CONNECT = 103;
    private static final int MESSAGE_USB_OPEN_FAIL = 101;
    private static final int MESSAGE_USB_OPEN_OK = 102;
    private static final int MESSAGE_VOICE_SPEK = 104;
    private static final int MESSAGE_WARN_HIGH_TIRE_PRESSURE = 110;
    private static final int MESSAGE_WARN_HIGH_TIRE_TEMPERATURE = 111;
    private static final int MESSAGE_WARN_LOW_BATTERY = 112;
    private static final int MESSAGE_WARN_LOW_TIRE_PRESSURE = 109;
    private static final int MESSAGE_WARN_NO_RF_SIGNAL = 113;
    private static final int MESSAGE_WARN_TIRE_LEAK = 108;
    public static boolean DEBUG = true;
    public static int DEF_WARN_COUNT = 1;
    public static String VERS_INFO = "";
    public static boolean activityFlag = true;
    public static byte backup_Byte = 0;
    public static int backup_TyrePressure = 0;
    public static int backup_TyrePressure_Hight_count = 0;
    public static int backup_TyrePressure_Low_count = 0;
    public static int backup_TyreTemperature = 0;
    public static int backup_TyreTemperature_Hight_count = 0;
    public static int backup_Warning_Signal_count = 0;
    public static byte left1_Byte = 0;
    public static int left1_TyrePressure = 0;
    public static int left1_TyrePressure_Hight_count = 0;
    public static int left1_TyrePressure_Low_count = 0;
    public static int left1_TyreTemperature = 0;
    public static int left1_TyreTemperature_Hight_count = 0;
    public static int left1_Warning_Signal_count = 0;
    public static byte left2_Byte = 0;
    public static int left2_TyrePressure = 0;
    public static int left2_TyrePressure_Hight_count = 0;
    public static int left2_TyrePressure_Low_count = 0;
    public static int left2_TyreTemperature = 0;
    public static int left2_TyreTemperature_Hight_count = 0;
    public static int left2_Warning_Signal_count = 0;
    public static Handler mHandler = null;
    public static Handler mHandlerSeriaTest = null;
    public static byte right1_Byte = 0;
    public static int right1_TyrePressure = 0;
    public static int right1_TyrePressure_Hight_count = 0;
    public static int right1_TyrePressure_Low_count = 0;
    public static int right1_TyreTemperature = 0;
    public static int right1_TyreTemperature_Hight_count = 0;
    public static int right1_Warning_Signal_count = 0;
    public static byte right2_Byte = 0;
    public static int right2_TyrePressure = 0;
    public static int right2_TyrePressure_Hight_count = 0;
    public static int right2_TyrePressure_Low_count = 0;
    public static int right2_TyreTemperature = 0;
    public static int right2_TyreTemperature_Hight_count = 0;
    public static int right2_Warning_Signal_count = 0;
    static byte backup_temp_Byte;
    static byte[] buf = new byte[40];
    static byte[] buf_temp = new byte[40];
    static byte left1_temp_Byte;
    static byte left2_temp_Byte;
    static byte right1_temp_Byte;
    static byte right2_temp_Byte;
    static byte[] temp = new byte[40];
    private static Boolean HandShake = false;
    private static int HandShakeCount = 0;
    private static int HandShakeTotal = 120;
    private static byte time = 0;
    private static List<Activity> activitys = new ArrayList();
    private static PendingIntent mPermissionIntent;
    private static SerialInputOutputManager mSerialIoManager;
    private static SharedPreferences sp = null;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    protected Application mApplication;
    int buf_len = 0;
    int buf_temp_len = 0;
    boolean data_head_falg = false;
    int j = 0;
    Context mContext = this;
    UsbSerialPort sPort = null;
    private int data_count = 0;
    private List<UsbSerialPort> mEntries = null;
    private UsbManager mUsbManager = null;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (TpmsServer.ACTION_USB_PERMISSION.equals(intent.getAction())) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra("device");
                    if (intent.getBooleanExtra("permission", false)) {
                        if (device != null) {
                            if (TpmsServer.DEBUG) {
                                Log.i("usb", "permission granted for device " + device);
                            }
                            TpmsServer.this.onStartUsbConnent();
                        }
                    } else if (TpmsServer.DEBUG) {
                        Log.i("usb", "permission denied for device " + device);
                    }
                }
            }
        }
    };

    private MediaPlayer mediaPlayer;
    private Alart view;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            if (action.equals(TpmsServer.EXIT_APP_ACTION)) {
                if (TpmsServer.DEBUG) {
                    Log.i(TpmsServer.TAG, "cz com.cz.action.exit_app");
                }
                TpmsServer.this.closeActivity();
                if (TpmsServer.this.mediaPlayer != null || TpmsServer.this.mediaPlayer.isPlaying()) {
                    TpmsServer.this.mediaPlayer.stop();
                    TpmsServer.this.mediaPlayer.release();
                    TpmsServer.this.mediaPlayer = null;
                }
                if (TpmsServer.this.view != null) {
                    TpmsServer.this.view.closeDialog();
                }
                try {
                    Intent ii = new Intent(TpmsServer.this.mContext, HeartbeatServer.class);
                    if (ii != null) {
                        TpmsServer.this.mContext.stopService(ii);
                    }
                    TpmsServer.this.stopSelf();
                } catch (Exception e) {
                }
            } else if (action.equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
                if (TpmsServer.DEBUG) {
                    Log.e(TpmsServer.TAG, " ACTION_USB_DEVICE_DETACHED usb Unplug");
                }
                TpmsServer.this.view.closeDialog();
            } else if (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                if (TpmsServer.DEBUG) {
                    Log.e(TpmsServer.TAG, " ACTION_USB_ACCESSORY_ATTACHED usb insert");
                }
                TpmsServer.this.stopIoManager();
                try {
                    if (TpmsServer.this.sPort != null) {
                        TpmsServer.this.sPort.close();
                    }
                } catch (IOException e2) {
                }
                TpmsServer.this.sPort = null;
                try {
                    SystemClock.sleep(3000);
                } catch (Exception e3) {
                }
                TpmsServer.this.onStartUsbConnent();
            }
        }
    };
    private Timer mTimer = null;
    private Timer mTimerHandShake = null;
    private TimerTask mTimerTask = null;
    private TimerTask mTimerTaskHandShake = null;

    private Handler serviceHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TpmsServer.MESSAGE_USB_CONNECT /*103*/:
                    if (TpmsServer.DEBUG) {
                        Log.d(TpmsServer.TAG, "USB MESSAGE_USB_CONNECT  ");
                    }
                    TpmsServer.this.onStartUsbConnent();
                    return;
                case TpmsServer.MESSAGE_VOICE_SPEK /*104*/:
                    if (!TpmsServer.getMuteStaus().booleanValue() && TpmsServer.activityFlag) {
                        final String data = (String) msg.obj;
                        if (TpmsServer.DEBUG) {
                            Log.d(TpmsServer.TAG, "isZh " + TpmsServer.this.isZh() + "MESSAGE_VOICE_SPEK " + data);
                        }
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                try {
                                    if (!TpmsServer.this.isZh()) {
                                        TpmsServer.this.play();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 10);
                        return;
                    }
                    return;
                case TpmsServer.MESSAGE_DATA /*105*/:
                case TpmsServer.MESSAGE_HANDSHAKE_NO /*107*/:
                    return;
                case TpmsServer.MESSAGE_HANDSHAKE_OK /*106*/:
                    Tools.Toast(TpmsServer.this.mContext, "handshake ok");
                    return;
                case TpmsServer.MESSAGE_WARN_TIRE_LEAK /*108*/:
                    ShowWarnMessage(TpmsServer.this.getString(R.string.alarm_leak));
                    return;
                case TpmsServer.MESSAGE_WARN_LOW_TIRE_PRESSURE /*109*/:
                    ShowWarnMessage(TpmsServer.this.getString(R.string.alarm_low_pressure));
                    return;
                case TpmsServer.MESSAGE_WARN_HIGH_TIRE_PRESSURE /*110*/:
                    ShowWarnMessage(TpmsServer.this.getString(R.string.alarm_high_pressure));
                    return;
                case TpmsServer.MESSAGE_WARN_HIGH_TIRE_TEMPERATURE /*111*/:
                    ShowWarnMessage(TpmsServer.this.getString(R.string.alarm_high_temprature));
                    return;
                case TpmsServer.MESSAGE_WARN_LOW_BATTERY /*112*/:
                    ShowWarnMessage(TpmsServer.this.getString(R.string.alarm_low_battery));
                    return;
                case TpmsServer.MESSAGE_WARN_NO_RF_SIGNAL /*113*/:
                    ShowWarnMessage(TpmsServer.this.getString(R.string.alarm_signal_error));
                    return;
                default:
                    TpmsServer.this.isDataWarn((byte[]) msg.getData().get("data"));
                    return;
            }
        }
    };
    private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {
        public void onRunError(Exception e) {
            if (TpmsServer.DEBUG) {
                Log.d(TpmsServer.TAG, "Runner stopped.");
            }
        }

        public void onNewData(byte[] data) {
            if (TpmsServer.DEBUG) {
                Log.e(TpmsServer.TAG, "cz onNewData" + Tools.bytesToHexString(data));
            }
            TpmsServer.this.dealData(data);
            try {
                byte[] bArr = new byte[6];
                bArr[0] = 85;
                bArr[1] = -86;
                bArr[2] = 6;
                bArr[3] = 25;
                TpmsServer.writeData(Tools.sum(bArr));
            } catch (Exception e) {
            }
        }
    };
    private Timer timer1;

    public static TpmsServer getInstance() {
        return SingletonHolder.mUSBService;
    }

    public static void writeData(byte[] data) {
        if (mSerialIoManager != null) {
            try {
                mSerialIoManager.writeAsync(data);
            } catch (Exception e) {
            }
            if (DEBUG) {
                Log.i(TAG, "cz writeAsync " + Tools.bytesToHexString(data));
                return;
            }
            return;
        }
        Log.e(TAG, "cz writeAsync mSerialIoManager =null ");
    }

    public static Boolean getBackUpTyreStaus() {
        if (sp != null) {
            return Boolean.valueOf(sp.getBoolean("BACKUP_STAUS", false));
        }
        return false;
    }

    public static void setBackUpTyreStaus(Boolean val) {
        if (sp != null) {
            sp.edit().putBoolean("BACKUP_STAUS", val.booleanValue()).commit();
        }
    }

    public static Boolean getMuteStaus() {
        if (sp != null) {
            return Boolean.valueOf(sp.getBoolean("MUTE_STAUS", false));
        }
        return false;
    }

    public static void setMuteStaus(Boolean val) {
        if (sp != null) {
            sp.edit().putBoolean("MUTE_STAUS", val.booleanValue()).commit();
        }
    }

    public static Boolean getBootStaus() {
        if (sp != null) {
            return Boolean.valueOf(sp.getBoolean("BOOT_STAUS", true));
        }
        return true;
    }

    public static void setBootStaus(Boolean val) {
        if (sp != null) {
            sp.edit().putBoolean("BOOT_STAUS", val.booleanValue()).commit();
        }
    }

    public static int getPressure_UNIT() {
        if (sp != null) {
            return sp.getInt("P", P_UNIT);
        }
        return 0;
    }

    public static void setPressure_UNIT(int i) {
        if (sp != null) {
            sp.edit().putInt("P", i).commit();
        }
    }

    public static int getTemperature_UNIT() {
        if (sp != null) {
            return sp.getInt("T", T_UNIT);
        }
        return 0;
    }

    public static void setTemperature_UNIT(int i) {
        if (sp != null) {
            sp.edit().putInt("T", i).commit();
        }
    }

    public static int getWarnHighPressure_Progress() {
        if (sp != null) {
            return sp.getInt("HP_PROGRESS", HP_PROGRESS);
        }
        return 0;
    }

    public static void setWarnHighPressure_Progress(int i) {
        if (sp != null) {
            sp.edit().putInt("HP_PROGRESS", i).commit();
        }
    }

    public static int getWarnLowPressure_Progress() {
        if (sp != null) {
            return sp.getInt("LP_PROGRESS", LP_PROGRESS);
        }
        return 0;
    }

    public static void setWarnLowPressure_Progress(int i) {
        if (sp != null) {
            sp.edit().putInt("LP_PROGRESS", i).commit();
        }
    }

    public static int getWarnHighTemperature_Progress() {
        if (sp != null) {
            return sp.getInt("HT_PROGRESS", HT_PROGRESS);
        }
        return 0;
    }

    public static void setWarnHighTemperature_Progress(int i) {
        if (sp != null) {
            sp.edit().putInt("HT_PROGRESS", i).commit();
        }
    }

    public static int getWarnHighPressure() {
        if (sp != null) {
            return sp.getInt("HP", HP_PROGRESS);
        }
        return 0;
    }

    public static void setWarnHighPressure(int i) {
        if (sp != null) {
            sp.edit().putInt("HP", i).commit();
        }
    }

    public static int getWarnLowPressure() {
        if (sp != null) {
            return sp.getInt("LP", 180);
        }
        return 0;
    }

    public static void setWarnLowPressure(int i) {
        if (sp != null) {
            sp.edit().putInt("LP", i).commit();
        }
    }

    public static int getWarnHighTemperature() {
        if (sp != null) {
            return sp.getInt("HT", 75);
        }
        return 0;
    }

    public static void setWarnHighTemperature(int i) {
        if (sp != null) {
            sp.edit().putInt("HT", i).commit();
        }
    }

    public static boolean getALARM() {
        if (sp != null) {
            return sp.getBoolean("ALARM", true);
        }
        return true;
    }

    public static Boolean getDebugTest() {
        if (sp != null) {
            return Boolean.valueOf(sp.getBoolean("DEBUG_TEST", false));
        }
        return false;
    }

    public static void setDebugTest(Boolean val) {
        if (sp != null) {
            sp.edit().putBoolean("DEBUG_TEST", val.booleanValue()).commit();
        }
    }

    public static String getLeft1_ID() {
        if (sp != null) {
            return sp.getString("LEFT1_ID", "");
        }
        return "";
    }

    public static void setLeft1_ID(String val) {
        if (sp != null) {
            sp.edit().putString("LEFT1_ID", val).commit();
        }
    }

    public static String getLeft2_ID() {
        if (sp != null) {
            return sp.getString("LEFT2_ID", "");
        }
        return "";
    }

    public static void setLeft2_ID(String val) {
        if (sp != null) {
            sp.edit().putString("LEFT2_ID", val).commit();
        }
    }

    public static String getRIGHT1_ID() {
        if (sp != null) {
            return sp.getString("RIGHT1_ID", "");
        }
        return "";
    }

    public static void setRIGHT1_ID(String val) {
        if (sp != null) {
            sp.edit().putString("RIGHT1_ID", val).commit();
        }
    }

    public static String getRIGHT2_ID() {
        if (sp != null) {
            return sp.getString("RIGHT2_ID", "");
        }
        return "";
    }

    public static void setRIGHT2_ID(String val) {
        if (sp != null) {
            sp.edit().putString("RIGHT2_ID", val).commit();
        }
    }

    public static String getSPARE_ID() {
        if (sp != null) {
            return sp.getString("SPARE_ID", "");
        }
        return "";
    }

    public static void setSPARE_ID(String val) {
        if (sp != null) {
            sp.edit().putString("SPARE_ID", val).commit();
        }
    }

    private void ShowWarnMessage(String msg) {
        TpmsServer.this.serviceHandler.removeMessages(TpmsServer.MESSAGE_WARN_HIGH_TIRE_PRESSURE);
        TpmsServer.this.serviceHandler.removeMessages(TpmsServer.MESSAGE_WARN_LOW_TIRE_PRESSURE);
        TpmsServer.this.serviceHandler.removeMessages(TpmsServer.MESSAGE_WARN_HIGH_TIRE_TEMPERATURE);
        TpmsServer.this.serviceHandler.removeMessages(TpmsServer.MESSAGE_WARN_TIRE_LEAK);
        TpmsServer.this.serviceHandler.removeMessages(TpmsServer.MESSAGE_WARN_LOW_BATTERY);
        TpmsServer.this.serviceHandler.removeMessages(TpmsServer.MESSAGE_WARN_NO_RF_SIGNAL);
        if (TpmsServer.activityFlag && !TpmsServer.this.isRunningForeground(TpmsServer.this.mApplication)) {
            TpmsServer.this.view.closeDialog();
            TpmsServer.this.view.showDialog(TpmsServer.this.getString(R.string.alarm) + " " + msg);
            return;
        }
    }

//    private void reset() {
//        this.timer1 = new Timer();
//        this.timer1.schedule(new TimerTask() {
//            public void run() {
//                if (TpmsServer.this.data_count > 10) {
//                    try {
//                        byte[] bArr = new byte[6];
//                        bArr[0] = 85;
//                        bArr[1] = -86;
//                        bArr[2] = 6;
//                        bArr[3] = 25;
//                        TpmsServer.writeData(Tools.sum(bArr));
//                    } catch (Exception e) {
//                    }
//                    TpmsServer.this.data_count = 0;
//                }
//                TpmsServer tpmsServer = TpmsServer.this;
//                tpmsServer.data_count = tpmsServer.data_count + 1;
//            }
//        }, 8000, 8000);
//    }

//    private boolean isDataBoolean(byte[] buff) {
//        byte sum = buff[0];
//        if (buff[0] != 85 || buff[1] != -86) {
//            return false;
//        }
//        for (int i = 1; i < buff.length - 1; i++) {
//            sum = (byte) (buff[i] ^ sum);
//        }
//        return sum == buff[buff.length - 1];
//    }

    private boolean isDataBoolean(byte[] buff, int len) {
        byte sum = buff[0];
        if (len > 20) {
            len = 10;
        }
        if (buff[0] != 85 || buff[1] != -86) {
            return false;
        }
        for (int i = 1; i < len - 1; i++) {
            sum = (byte) (buff[i] ^ sum);
        }
        return sum == buff[len - 1];
    }


    public void dealData(byte[] buff) {
        int length = buff.length / 10;
        if (mHandlerSeriaTest != null) {
            sendMessage(mHandlerSeriaTest, buff);
        }
        if (DEBUG && buff != null) {
            Log.v(TAG, "cz1 buff " + Tools.bytesToHexString(buff) + " len " + buff.length);
        }
        if (buff == null) {
            Log.e(TAG, "cz2 buff null " + Tools.bytesToHexString(buff) + " len " + buff.length);
        } else if (buff.length <= 3 || buff[0] != 85 || buff[1] != -86 || buff.length < buff[2] || !isDataBoolean(buff, buff[2])) {
            if (buff.length < 20 && buf_temp.length > this.buf_temp_len + buff.length) {
                if (DEBUG) {
                    Log.i(TAG, "cz44 buf_temp " + Tools.bytesToHexString(buf_temp) + "  buf_temp_len " + this.buf_temp_len);
                }
                temp = Tools.getMergeBytes(buf_temp, this.buf_temp_len, buff, buff.length);
                for (int i = 0; i < temp.length; i++) {
                    buf_temp[i] = temp[i];
                }
                this.buf_temp_len = temp.length;
                if (DEBUG) {
                    Log.i(TAG, "cz44--- buf_temp " + Tools.bytesToHexString(buf_temp) + "  buf_temp_len " + this.buf_temp_len + " temp.length " + temp.length);
                }
            }
            if (DEBUG) {
                Log.i(TAG, "cz5 buf_temp " + Tools.bytesToHexString(buf_temp) + "  buf_temp_len " + this.buf_temp_len);
            }
            for (int i2 = 0; i2 < this.buf_temp_len; i2++) {
                if (buf.length - 1 > i2 && buf_temp.length - 1 > i2) {
                    buf[i2] = buf_temp[i2];
                    buf_temp[i2] = 0;
                }
            }
            this.buf_len = this.buf_temp_len;
            if (DEBUG) {
                Log.i(TAG, "cz5-- buf " + Tools.bytesToHexString(buf) + "  buf_len " + this.buf_len);
            }
            this.data_head_falg = false;
            this.j = 0;
            int i3 = 0;
            while (true) {
                if (i3 < this.buf_len) {
                    if (i3 + 1 < buf.length && buf[i3] == 85 && buf[i3 + 1] == -86) {
                        this.data_head_falg = true;
                        this.j = 0;
                        break;
                    }
                    i3++;
                } else {
                    break;
                }
            }
            if (DEBUG) {
                Log.i(TAG, "cz556-- " + this.data_head_falg + " buf " + Tools.bytesToHexString(buf) + "  i " + i3);
            }
            if (this.data_head_falg) {
                this.j = 0;
                while (i3 < this.buf_len) {
                    if (buf.length > i3 && buf_temp.length > this.j) {
                        buf_temp[this.j] = buf[i3];
                    }
                    this.j++;
                    i3++;
                }
            }
            if (this.data_head_falg) {
                this.buf_temp_len = this.j;
            }
            if (DEBUG) {
                Log.i(TAG, "cz--77 " + this.data_head_falg + " buf " + Tools.bytesToHexString(buf_temp) + "  buf_temp_len " + this.buf_temp_len);
            }
            if (this.buf_temp_len < buf_temp.length - 10 && this.buf_temp_len > 5 && this.data_head_falg && this.buf_temp_len >= buf_temp[2] && isDataBoolean(buf_temp, buf_temp[2])) {
                if (DEBUG) {
                    Log.i(TAG, "cz6 buf_temp " + Tools.bytesToHexString(buf_temp) + "  buf_temp_len " + this.buf_temp_len + "  len " + buf_temp[2]);
                }
                byte[] b = new byte[buf_temp[2]];
                for (int y = 0; y < buf_temp[2]; y++) {
                    b[y] = buf_temp[y];
                }
                if (this.buf_temp_len > buf_temp[2]) {
                    int i4 = 0;
                    while (i4 < this.buf_temp_len - buf_temp[2]) {
                        if (buf_temp[2] + i4 < buf_temp.length && i4 < buf_temp.length) {
                            buf_temp[i4] = buf_temp[buf_temp[2] + i4];
                        }
                        i4++;
                    }
                    if (i4 > 0) {
                        this.buf_temp_len = i4;
                    }
                } else {
                    for (int ii = 0; ii < buf_temp.length; ii++) {
                        buf_temp[ii] = 0;
                    }
                    this.buf_temp_len = 0;
                }
                if (this.mTimerHandShake == null && !HandShake.booleanValue()) {
                    startTimerHandShake();
                    setTimeNsHandShake(1);
                }
                if (mHandler != null) {
                    sendMessage(mHandler, b);
                }
                isDataWarn(b);
                if (DEBUG) {
                    Log.v(TAG, "cz7 buf_temp " + Tools.bytesToHexString(b));
                }
            }
            if (this.buf_temp_len > buf_temp.length - 2) {
                this.buf_temp_len = 0;
            }
            if (this.buf_len > buf.length - 2) {
                this.buf_len = 0;
            }
        } else {
            if (mHandler != null) {
                sendMessage(mHandler, buff);
            }
            if (this.mTimerHandShake == null && !HandShake.booleanValue()) {
                startTimerHandShake();
                setTimeNsHandShake(1);
            }
            isDataWarn(buff);
            if (buff.length > buff[2]) {
                byte buff_len = buff[2];
                int i5 = 0;
                while (i5 < buff.length - buff_len) {
                    if (i5 + buff_len < buff.length && i5 < buf_temp.length) {
                        buf_temp[i5] = buff[i5 + buff_len];
                    }
                    i5++;
                }
                if (i5 > 0) {
                    this.buf_temp_len = buff.length - buff_len;
                }
            }
        }
    }


    private boolean isDataWarn(byte[] b) {
        if (DEBUG) {
            Log.e(TAG, "cz  isDataWarn  " + Tools.bytesToHexString(b));
        }
        if (b[0] != 85 || b[1] != -86) {
            return false;
        }
        try {
            int ret = ServerDataP(b);
            ServerDataT(b);
            ServerDataWarn(b);
            int retPH = getWarnHighPressure();
            int retPL = getWarnLowPressure();
            int retHT = getWarnHighTemperature();
            HandShakeData(b);
            if (ret == 0 || retPH == 0 || retPL == 0 || retHT == 0) {
                return false;
            }
            if (ret == 1) {
                if (left1_TyrePressure > retPH ||
                        left1_TyrePressure < retPL ||
                        left1_TyreTemperature > retHT ||
                        UnitTools.warning_AIR(left1_Byte).booleanValue() ||
                        UnitTools.warning_P(left1_Byte).booleanValue() ||
                        UnitTools.warning_Signal(left1_Byte).booleanValue()) {
                    if (left1_TyrePressure > retPH) {
                        left1_TyrePressure_Low_count = 0;
                        if (left1_TyrePressure_Hight_count > DEF_WARN_COUNT) {
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Left front wheel pressure is too high, please note");
                            left1_TyrePressure_Hight_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_HIGH_TIRE_PRESSURE, "");
                        }
                        left1_TyrePressure_Hight_count++;
                    } else if (left1_TyrePressure < retPL) {
                        left1_TyrePressure_Hight_count = 0;
                        if (left1_TyrePressure_Low_count > DEF_WARN_COUNT) {
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Left front wheel pressure is too low, please note");
                            left1_TyrePressure_Low_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_LOW_TIRE_PRESSURE, "");
                        }
                        left1_TyrePressure_Low_count++;
                    } else {
                        left1_TyrePressure_Hight_count = 0;
                        left1_TyrePressure_Low_count = 0;
                    }
                    if (left1_TyreTemperature > retHT) {
                        if (left1_TyreTemperature_Hight_count > DEF_WARN_COUNT) {
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Left front wheel temperature is too high, please note");
                            left1_TyreTemperature_Hight_count = 0;
                        }
                        left1_TyreTemperature_Hight_count++;
                    } else {
                        left1_TyreTemperature_Hight_count = 0;
                    }
                    if (UnitTools.warning_AIR(left1_Byte).booleanValue()) {
                        sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Left front wheel leaks, please note");
                        sendServerMessage(this.serviceHandler, MESSAGE_WARN_TIRE_LEAK, "");
                    }
                    if (UnitTools.warning_P(left1_Byte).booleanValue()) {
                        sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Left front wheel battery voltage is low, please note");
                        sendServerMessage(this.serviceHandler, MESSAGE_WARN_LOW_BATTERY, "");
                    }
                    if (UnitTools.warning_Signal(left1_Byte).booleanValue()) {
                        if (left1_Warning_Signal_count > DEF_WARN_COUNT + 8) {
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Left front wheel signal is lost, please note");
                            left1_Warning_Signal_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_NO_RF_SIGNAL, "");
                        }
                        left1_Warning_Signal_count++;
                    } else {
                        left1_Warning_Signal_count = 0;
                    }
                } else {
                    left1_TyrePressure_Hight_count = 0;
                    left1_TyrePressure_Low_count = 0;
                    left1_TyreTemperature_Hight_count = 0;
                    left1_Warning_Signal_count = 0;
                }
            }
            if (ret == 2) {
                if (left2_TyrePressure > retPH ||
                        left2_TyrePressure < retPL ||
                        left2_TyreTemperature > retHT ||
                        UnitTools.warning_AIR(left2_Byte).booleanValue() ||
                        UnitTools.warning_P(left2_Byte).booleanValue() ||
                        UnitTools.warning_Signal(left2_Byte).booleanValue()) {
                    if (left2_TyrePressure > retPH) {
                        left2_TyrePressure_Low_count = 0;
                        if (left2_TyrePressure_Hight_count > DEF_WARN_COUNT) {
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Left rear wheel pressure is too high, please note");
                            left2_TyrePressure_Hight_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_HIGH_TIRE_PRESSURE, "");
                        }
                        left2_TyrePressure_Hight_count++;
                    } else if (left2_TyrePressure < retPL) {
                        left2_TyrePressure_Hight_count = 0;
                        if (left2_TyrePressure_Low_count > DEF_WARN_COUNT) {
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Left rear wheel pressure is too low, please note");
                            left2_TyrePressure_Low_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_LOW_TIRE_PRESSURE, "");
                        }
                        left2_TyrePressure_Low_count++;
                    } else {
                        left2_TyrePressure_Hight_count = 0;
                        left2_TyrePressure_Low_count = 0;
                    }
                    if (left2_TyreTemperature > retHT) {
                        if (left2_TyreTemperature_Hight_count > DEF_WARN_COUNT) {
                            left2_TyreTemperature_Hight_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Left rear wheel temperature is too high, please note");
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_HIGH_TIRE_TEMPERATURE, "");
                        }
                        left2_TyreTemperature_Hight_count++;
                    } else {
                        left2_TyreTemperature_Hight_count = 0;
                    }
                    if (UnitTools.warning_AIR(left2_Byte).booleanValue()) {
                        sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Left rear wheel leaks, please note");
                        sendServerMessage(this.serviceHandler, MESSAGE_WARN_TIRE_LEAK, "");
                    }
                    if (UnitTools.warning_P(left2_Byte).booleanValue()) {
                        sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Left rear wheel battery voltage is low, please note");
                        sendServerMessage(this.serviceHandler, MESSAGE_WARN_LOW_BATTERY, "");
                    }
                    if (UnitTools.warning_Signal(left2_Byte).booleanValue()) {
                        if (left2_Warning_Signal_count > DEF_WARN_COUNT + 8) {
                            left2_Warning_Signal_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Left rear wheel signal is lost, please note");
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_NO_RF_SIGNAL, "");
                        }
                        left2_Warning_Signal_count++;
                    } else {
                        left2_Warning_Signal_count = 0;
                    }
                } else {
                    left2_TyrePressure_Hight_count = 0;
                    left2_TyrePressure_Low_count = 0;
                    left2_TyreTemperature_Hight_count = 0;
                    left2_Warning_Signal_count = 0;
                }
            }
            if (ret == 3) {
                if (right1_TyrePressure > retPH ||
                        right1_TyrePressure < retPL ||
                        right1_TyreTemperature > retHT ||
                        UnitTools.warning_AIR(right1_Byte).booleanValue() ||
                        UnitTools.warning_P(right1_Byte).booleanValue() ||
                        UnitTools.warning_Signal(right1_Byte).booleanValue()) {
                    if (right1_TyrePressure > retPH) {
                        right1_TyrePressure_Low_count = 0;
                        if (right1_TyrePressure_Hight_count > DEF_WARN_COUNT) {
                            right1_TyrePressure_Hight_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Front right wheel pressure is too high, please note");
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_HIGH_TIRE_PRESSURE, "");
                        }
                        right1_TyrePressure_Hight_count++;
                    } else if (right1_TyrePressure < retPL) {
                        right1_TyrePressure_Hight_count = 0;
                        if (right1_TyrePressure_Low_count > DEF_WARN_COUNT) {
                            right1_TyrePressure_Low_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Right front wheel pressure is too low, please note");
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_LOW_TIRE_PRESSURE, "");
                        }
                        right1_TyrePressure_Low_count++;
                    } else {
                        right1_TyrePressure_Hight_count = 0;
                        right1_TyrePressure_Low_count = 0;
                    }
                    if (right1_TyreTemperature > retHT) {
                        if (right1_TyreTemperature_Hight_count > DEF_WARN_COUNT) {
                            right1_TyreTemperature_Hight_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "The temperature of the right front wheel is too high, please note");
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_HIGH_TIRE_TEMPERATURE, "");
                        }
                        right1_TyreTemperature_Hight_count++;
                    } else {
                        right1_TyreTemperature_Hight_count = 0;
                    }
                    if (UnitTools.warning_AIR(right1_Byte).booleanValue()) {
                        sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Front right wheel leaks, please note");
                        sendServerMessage(this.serviceHandler, MESSAGE_WARN_TIRE_LEAK, "");
                    }
                    if (UnitTools.warning_P(right1_Byte).booleanValue()) {
                        sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Right front wheel battery voltage is low, please note");
                        sendServerMessage(this.serviceHandler, MESSAGE_WARN_LOW_BATTERY, "");
                    }
                    if (UnitTools.warning_Signal(right1_Byte).booleanValue()) {
                        if (right1_Warning_Signal_count > DEF_WARN_COUNT + 8) {
                            right1_Warning_Signal_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Right front wheel signal is lost, please note");
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_NO_RF_SIGNAL, "");
                        }
                        right1_Warning_Signal_count++;
                    } else {
                        right1_Warning_Signal_count = 0;
                    }
                } else {
                    right1_TyrePressure_Hight_count = 0;
                    right1_TyrePressure_Low_count = 0;
                    right1_TyreTemperature_Hight_count = 0;
                    right1_Warning_Signal_count = 0;
                }
            }
            if (ret == 4) {
                if (right2_TyrePressure > retPH ||
                        right2_TyrePressure < retPL ||
                        right2_TyreTemperature > retHT ||
                        UnitTools.warning_AIR(right2_Byte).booleanValue() ||
                        UnitTools.warning_P(right2_Byte).booleanValue() ||
                        UnitTools.warning_Signal(right2_Byte).booleanValue()) {
                    if (right2_TyrePressure > retPH) {
                        right2_TyrePressure_Low_count = 0;
                        if (right2_TyrePressure_Hight_count > DEF_WARN_COUNT) {
                            right2_TyrePressure_Hight_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Rear right wheel pressure is too high, please note");
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_HIGH_TIRE_PRESSURE, "");
                        }
                        right2_TyrePressure_Hight_count++;
                    } else if (right2_TyrePressure < retPL) {
                        right2_TyrePressure_Hight_count = 0;
                        if (right2_TyrePressure_Low_count > DEF_WARN_COUNT) {
                            right2_TyrePressure_Low_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Right rear wheel pressure is too low, please note");
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_LOW_TIRE_PRESSURE, "");
                        }
                        right2_TyrePressure_Low_count++;
                    } else {
                        right2_TyrePressure_Hight_count = 0;
                        right2_TyrePressure_Low_count = 0;
                    }
                    if (right2_TyreTemperature > retHT) {
                        if (right2_TyreTemperature_Hight_count > DEF_WARN_COUNT) {
                            right2_TyreTemperature_Hight_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "The temperature of the right rear wheel is too high, please note");
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_HIGH_TIRE_TEMPERATURE, "");
                        }
                        right2_TyreTemperature_Hight_count++;
                    } else {
                        right2_TyreTemperature_Hight_count = 0;
                    }
                    if (UnitTools.warning_AIR(right2_Byte).booleanValue()) {
                        sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Rear right wheel leaks, please note");
                        sendServerMessage(this.serviceHandler, MESSAGE_WARN_TIRE_LEAK, "");
                    }
                    if (UnitTools.warning_P(right2_Byte).booleanValue()) {
                        sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Right rear wheel battery voltage is low, please note");
                        sendServerMessage(this.serviceHandler, MESSAGE_WARN_LOW_BATTERY, "");
                    }
                    if (UnitTools.warning_Signal(right2_Byte).booleanValue()) {
                        if (right2_Warning_Signal_count > DEF_WARN_COUNT + 8) {
                            right2_Warning_Signal_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Right rear wheel signal is lost, please note");
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_NO_RF_SIGNAL, "");
                        }
                        right2_Warning_Signal_count++;
                    } else {
                        right2_Warning_Signal_count = 0;
                    }
                } else {
                    right2_TyrePressure_Hight_count = 0;
                    right2_TyrePressure_Low_count = 0;
                    right2_TyreTemperature_Hight_count = 0;
                    right2_Warning_Signal_count = 0;
                }
            }
            if (ret == 5 && getBackUpTyreStaus().booleanValue()) {
                if (backup_TyrePressure > retPH ||
                        backup_TyrePressure < retPL ||
                        backup_TyreTemperature > retHT ||
                        UnitTools.warning_AIR(backup_Byte).booleanValue() ||
                        UnitTools.warning_P(backup_Byte).booleanValue() ||
                        UnitTools.warning_Signal(backup_Byte).booleanValue()) {
                    if (backup_TyrePressure > retPH) {
                        backup_TyrePressure_Low_count = 0;
                        if (backup_TyrePressure_Hight_count > DEF_WARN_COUNT) {
                            backup_TyrePressure_Hight_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Backup tire pressure is too high, please note");
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_HIGH_TIRE_PRESSURE, "");
                        }
                        backup_TyrePressure_Hight_count++;
                    } else if (backup_TyrePressure < retPL) {
                        backup_TyrePressure_Hight_count = 0;
                        if (backup_TyrePressure_Low_count > DEF_WARN_COUNT) {
                            backup_TyrePressure_Low_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Backup tire pressure is too low, please note");
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_LOW_TIRE_PRESSURE, "");
                        }
                        backup_TyrePressure_Low_count++;
                    } else {
                        backup_TyrePressure_Hight_count = 0;
                        backup_TyrePressure_Low_count = 0;
                    }
                    if (backup_TyreTemperature > retHT) {
                        if (backup_TyreTemperature_Hight_count > DEF_WARN_COUNT) {
                            backup_TyreTemperature_Hight_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Spare tire temperature is too high, please note");
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_HIGH_TIRE_TEMPERATURE, "");
                        }
                        backup_TyreTemperature_Hight_count++;
                    } else {
                        backup_TyreTemperature_Hight_count = 0;
                    }
                    if (UnitTools.warning_AIR(backup_Byte).booleanValue()) {
                        sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Leakage of spare tire, please note");
                        sendServerMessage(this.serviceHandler, MESSAGE_WARN_TIRE_LEAK, "");
                    }
                    if (UnitTools.warning_P(backup_Byte).booleanValue()) {
                        sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Backup battery voltage is low, please note");
                        sendServerMessage(this.serviceHandler, MESSAGE_WARN_LOW_BATTERY, "");
                    }
                    if (UnitTools.warning_Signal(backup_Byte).booleanValue()) {
                        if (backup_Warning_Signal_count > DEF_WARN_COUNT + 8) {
                            backup_Warning_Signal_count = 0;
                            sendServerMessage(this.serviceHandler, MESSAGE_VOICE_SPEK, "Backup tire signal is lost, please note");
                            sendServerMessage(this.serviceHandler, MESSAGE_WARN_NO_RF_SIGNAL, "");
                        }
                        backup_Warning_Signal_count++;
                    } else {
                        backup_Warning_Signal_count = 0;
                    }
                } else {
                    backup_TyrePressure_Hight_count = 0;
                    backup_TyrePressure_Low_count = 0;
                    backup_TyreTemperature_Hight_count = 0;
                    backup_Warning_Signal_count = 0;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private int ServerDataP(byte[] buff) {
        if (buff[2] != 10) {
            return 0;
        }
        int data = (int) (((double) Integer.valueOf(Integer.toBinaryString(buff[4] & 0xFF), 2).intValue()) * 3.44d);
        switch (buff[3]) {
            case 0:
                left1_TyrePressure = data;
                return 1;
            case 1:
                right1_TyrePressure = data;
                return 3;
            case 5:
                backup_TyrePressure = data;
                return 5;
            case 16:
                left2_TyrePressure = data;
                return 2;
            case 17:
                right2_TyrePressure = data;
                return 4;
            default:
                return 0;
        }
    }

    private int ServerDataT(byte[] buff) {
        if (buff[2] != 10) {
            return 0;
        }
        int data = Integer.valueOf(Integer.toBinaryString(buff[5] & 0xFF), 2).intValue() - 50;
        switch (buff[3]) {
            case 0:
                left1_TyreTemperature = data;
                return 0;
            case 1:
                right1_TyreTemperature = data;
                return 0;
            case 5:
                backup_TyreTemperature = data;
                return 0;
            case 16:
                left2_TyreTemperature = data;
                return 0;
            case 17:
                right2_TyreTemperature = data;
                return 0;
            default:
                return 0;
        }
    }

    private int ServerDataWarn(byte[] buff) {
        if (buff[2] != 10) {
            return 0;
        }
        byte data = (byte) (buff[6] & 0xFF);
        switch (buff[3]) {
            case 0:
                if (data == left1_temp_Byte) {
                    left1_Byte = data;
                    return 0;
                }
                left1_temp_Byte = data;
                return 0;
            case 1:
                if (data == right1_temp_Byte) {
                    right1_Byte = data;
                    return 0;
                }
                right1_temp_Byte = data;
                return 0;
            case 5:
                if (backup_temp_Byte == data) {
                    backup_Byte = data;
                    return 0;
                }
                backup_temp_Byte = data;
                return 0;
            case 16:
                if (data == left2_temp_Byte) {
                    left2_Byte = data;
                    return 0;
                }
                left2_temp_Byte = data;
                return 0;
            case 17:
                if (data == right2_temp_Byte) {
                    right2_Byte = data;
                    return 0;
                }
                right2_temp_Byte = data;
                return 0;
            default:
                return 0;
        }
    }

    private void HandShakeData(byte[] buff) {
        try {
            if (buff[2] == 6 && buff[3] == -91 && ((byte) ((((((((time ^ 32) ^ 21) ^ 16) ^ 1) ^ 2) ^ 3) ^ 4) ^ 5)) == buff[4]) {
                HandShake = true;
                sendServerMessage(this.serviceHandler, MESSAGE_HANDSHAKE_OK, "");
                stopTimerHandShake();
            }
        } catch (Exception e) {
        }
    }

//    private void delay(int ms) {
//        try {
//            Thread.currentThread();
//            Thread.sleep((long) ms);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    public void registerHandler(Handler h) {
        mHandler = h;
    }

    public void unregisterHandler() {
        if (mHandler != null) {
            mHandler = null;
        }
    }

//    public void registerSeriaTestHandler(Handler h) {
//        mHandlerSeriaTest = h;
//    }
//
//    public void unregisterSeriaTestHandler() {
//        if (mHandlerSeriaTest != null) {
//            mHandlerSeriaTest = null;
//        }
//    }

    private void sendMessage(Handler mHandler2, byte[] data) {
        Message msg = Message.obtain();
        msg.arg1 = data.length;
        Bundle bundle = new Bundle();
        bundle.putByteArray("data", data);
        msg.setData(bundle);
        mHandler2.sendMessage(msg);
    }

    private void sendServerMessage(Handler mHandler2, int arg1, String str) {
        Message msg = Message.obtain();
        msg.what = arg1;
        msg.obj = str;
        msg.setData(new Bundle());
        mHandler2.sendMessage(msg);
    }

    @SuppressLint("NewApi")
    private boolean isRunningForeground(Context context) {
        String currentPackageName = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1).get(0).topActivity.getPackageName();
        return !TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(getPackageName());
    }

    public void addActivity(Activity activity) {
        if (!activitys.contains(activity)) {
            activitys.add(activity);
        }
    }

    private void closeActivity() {
        for (Activity activity : activitys) {
            activity.finish();
        }
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "cz onCreate ");
        sp = getSharedPreferences("TAG", 0);
        this.mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        this.mEntries = new ArrayList();
        time = (byte) ((int) (255 & System.currentTimeMillis()));
        IntentFilter filter = new IntentFilter();
        filter.addAction(EXIT_APP_ACTION);
        filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        this.mContext.registerReceiver(this.mReceiver, filter);
        mPermissionIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        this.mContext.registerReceiver(this.mUsbReceiver, new IntentFilter(ACTION_USB_PERMISSION));
        this.mApplication = getApplication();
        if (this.mediaPlayer == null) {
            this.mediaPlayer = MediaPlayer.create(this.mApplication, R.raw.alarm1);
        }
        if (this.view == null) {
            this.view = Alart.initEasyTouch(this.mApplication);
        }
        onStartUsbConnent();
    }

    private void onStartUsbConnent() {
        new AsyncTask<Void, Void, List<UsbSerialPort>>() {
            protected List<UsbSerialPort> doInBackground(Void... params) {
                String str;
                if (TpmsServer.DEBUG) {
                    Log.d(TpmsServer.TAG, "Refreshing device list ...");
                }
                try {
                    SystemClock.sleep(1000);
                } catch (Exception e) {
                }
                List<UsbSerialDriver> drivers = UsbSerialProber.getDefaultProber().findAllDrivers(TpmsServer.this.mUsbManager);
                List<UsbSerialPort> result = new ArrayList<>();
                for (UsbSerialDriver driver : drivers) {
                    List<UsbSerialPort> ports = driver.getPorts();
                    if (TpmsServer.DEBUG) {
                        String access$0 = TpmsServer.TAG;
                        Object[] objArr = new Object[3];
                        objArr[0] = driver;
                        objArr[1] = Integer.valueOf(ports.size());
                        if (ports.size() == 1) {
                            str = "";
                        } else {
                            str = "s";
                        }
                        objArr[2] = str;
                        Log.d(access$0, String.format("+ %s: %s port%s", objArr));
                    }
                    result.addAll(ports);
                }
                return result;
            }

//            private void doInBackground() {
//                if (TpmsServer.DEBUG) {
//                    Log.e(TpmsServer.TAG, "doInBackground ...");
//                }
//                TpmsServer.this.stopIoManager();
//                try {
//                    if (TpmsServer.this.sPort != null) {
//                        TpmsServer.this.sPort.close();
//                    }
//                } catch (IOException e) {
//                }
//                TpmsServer.this.sPort = null;
//            }

            protected void onPostExecute(List<UsbSerialPort> result) {
                TpmsServer.this.mEntries.clear();
                TpmsServer.this.mEntries.addAll(result);
                if (TpmsServer.DEBUG) {
                    Log.d(TpmsServer.TAG, "Done refreshing, " + TpmsServer.this.mEntries.size() + " entries found.");
                }
                for (int i = 0; i < TpmsServer.this.mEntries.size(); i++) {
                    UsbSerialPort port = TpmsServer.this.mEntries.get(i);
                    if (port != null) {
                        if ("1027_24577".equals(port.getDriver().getDevice().getVendorId() + "_" + port.getDriver().getDevice().getProductId())) {
                            TpmsServer.this.showConsoleActivity(port);
                        } else if ("1027_24597".equals(port.getDriver().getDevice().getVendorId() + "_" + port.getDriver().getDevice().getProductId())) {
                            TpmsServer.this.showConsoleActivity(port);
                        } else if ("6790_29987".equals(port.getDriver().getDevice().getVendorId() + "_" + port.getDriver().getDevice().getProductId())) {
                            TpmsServer.this.showConsoleActivity(port);
                        }
                    }
                }
            }
        }.execute(new Void[]{null});
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "cz onStartCommand ");
        stopTimer();
        startTimer();
        setTimeNs(1);
        //TODO
//        Notification notification = new Notification(R.drawable.ic_launcher1, getString(R.string.app_name), System.currentTimeMillis());
//        notification.setLatestEventInfo(this, "TPMS", getString(R.string.msg_tpms_running), PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));
//        startForeground(273, notification);
        return Service.START_STICKY_COMPATIBILITY;
    }


    private void showConsoleActivity(UsbSerialPort Port) {
        UsbInterface mInterface = null;
        this.sPort = Port;
        if (this.sPort != null) {
            if (0 < this.sPort.getDriver().getDevice().getInterfaceCount()) {
                mInterface = this.sPort.getDriver().getDevice().getInterface(0);
            }
            if (mInterface == null) {
                Log.e(TAG, "USB device NO  Interface");
            } else if (this.mUsbManager.hasPermission(this.sPort.getDriver().getDevice())) {
                UsbDeviceConnection connection = this.mUsbManager.openDevice(this.sPort.getDriver().getDevice());
                if (connection == null) {
                    try {
                        if (this.sPort != null) {
                            this.sPort.close();
                        }
                    } catch (IOException e) {
                    }
                    this.sPort = null;
                    if (DEBUG) {
                        Log.e(TAG, "Error openDevice:  connection " + connection);
                    }
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(MESSAGE_USB_OPEN_FAIL);
                        return;
                    }
                    return;
                }
                try {
                    this.sPort.open(connection);
                    try {
                        this.sPort.setParameters(19200, 8, 1, 0);
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(MESSAGE_USB_OPEN_OK);
                        }
                        if (this.sPort != null) {
                            try {
                                VERS_INFO = Tools.getVersionName(this.mContext) + " " + this.sPort.getClass().getSimpleName();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                        onDeviceStateChange();
                    } catch (IOException e3) {
                        if (DEBUG) {
                            Log.e(TAG, "Error setting up device: " + e3.getMessage(), e3);
                        }
                        try {
                            if (this.sPort != null) {
                                this.sPort.close();
                            }
                        } catch (IOException e4) {
                        }
                        this.sPort = null;
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(MESSAGE_USB_OPEN_FAIL);
                        }
                    }
                } catch (IOException e22) {
                    if (DEBUG) {
                        Log.e(TAG, "cz open device: " + e22.getMessage(), e22);
                    }
                }
            } else {
                if (DEBUG) {
                    Log.e(TAG, "permission denied for device ");
                }
                this.mUsbManager.requestPermission(this.sPort.getDriver().getDevice(), mPermissionIntent);
            }
        }
    }

    public void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (this.sPort != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(this.sPort, this.mListener);
            this.mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, " onDestroy.");
        stopForeground(true);
        this.view.closeDialog();
        if (this.mReceiver != null) {
            this.mContext.unregisterReceiver(this.mReceiver);
        }
        if (this.mUsbReceiver != null) {
            this.mContext.unregisterReceiver(this.mUsbReceiver);
        }
        stopTimer();
        if (this.timer1 != null) {
            this.timer1.cancel();
        }
        if (sp != null) {
            sp.unregisterOnSharedPreferenceChangeListener(this);
            sp = null;
        }
        if (this.mediaPlayer != null || this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        if (this.view != null) {
            this.view.closeDialog();
        }
        Log.i(TAG, "onDestroy ..");
        try {
            stopIoManager();
            if (this.sPort != null) {
                this.sPort.close();
            }
        } catch (IOException e2) {
        }
        this.sPort = null;
    }

    private void startTimer() {
        if (this.mTimer == null) {
            this.mTimer = new Timer();
        }
        if (this.mTimerTask == null) {
            this.mTimerTask = new TimerTask() {
                public void run() {
                    if (Tools.getCurProcessName(TpmsServer.this.mContext) == null) {
                        try {
                            Intent ii = new Intent(TpmsServer.this.mContext, HeartbeatServer.class);
                            if (ii != null) {
                                TpmsServer.this.mContext.stopService(ii);
                            }
                            Intent intent = new Intent(TpmsServer.this.mContext, MainActivity.class);
                            intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY | Intent.FLAG_RECEIVER_REPLACE_PENDING);
                            TpmsServer.this.mContext.startActivity(intent);
                            TpmsServer.this.stopSelf();
                        } catch (Exception e) {
                        }
                    }
                    if (!Tools.isUSBHeartbeatServer(TpmsServer.this.mContext)) {
                        Intent intent2 = new Intent(TpmsServer.this.mContext, HeartbeatServer.class);
                        intent2.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY | Intent.FLAG_RECEIVER_REPLACE_PENDING);
                        TpmsServer.this.mContext.startService(intent2);
                    }
                }
            };
        }
    }

    /* access modifiers changed from: package-private */
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

    private void startTimerHandShake() {
        if (this.mTimerHandShake == null) {
            this.mTimerHandShake = new Timer();
        }
        if (this.mTimerTaskHandShake == null) {
            this.mTimerTaskHandShake = new TimerTask() {
                public void run() {
                    if (!TpmsServer.HandShake.booleanValue()) {
                        if (TpmsServer.DEBUG) {
                            Log.e(TpmsServer.TAG, "HandShakeCount " + TpmsServer.HandShakeCount + " " + TpmsServer.time);
                        }
                        try {
                            byte[] bArr = new byte[6];
                            bArr[0] = 85;
                            bArr[1] = -86;
                            bArr[2] = 6;
                            bArr[3] = 90;
                            bArr[4] = TpmsServer.time;
                            TpmsServer.writeData(Tools.sum(bArr));
                        } catch (Exception e) {
                        }
                        TpmsServer.HandShakeCount = TpmsServer.HandShakeCount + 1;
                        if (TpmsServer.HandShakeCount > TpmsServer.HandShakeTotal) {
                            if (TpmsServer.mHandler != null) {
                                TpmsServer.mHandler.sendEmptyMessage(TpmsServer.MESSAGE_HANDSHAKE_NO);
                            }
                            TpmsServer.HandShakeCount = TpmsServer.HandShakeTotal + 1;
                            TpmsServer.this.stopTimerHandShake();
                            return;
                        }
                        return;
                    }
                    TpmsServer.this.stopTimerHandShake();
                }
            };
        }
    }

    /* access modifiers changed from: package-private */
    public void setTimeNsHandShake(long dalayms) {
        if (this.mTimerHandShake != null && this.mTimerTaskHandShake != null) {
            this.mTimerHandShake.schedule(this.mTimerTaskHandShake, dalayms * 1000, 1000 * dalayms);
        }
    }

    private void stopTimerHandShake() {
        if (this.mTimerHandShake != null) {
            this.mTimerHandShake.cancel();
            this.mTimerHandShake = null;
        }
        if (this.mTimerTaskHandShake != null) {
            this.mTimerTaskHandShake.cancel();
            this.mTimerTaskHandShake = null;
        }
    }

    private void play() {
        if (!this.mediaPlayer.isPlaying() && getALARM()) {
            try {
                if (DEBUG) {
                    Log.v(TAG, "isPlaying");
                }
                this.mediaPlayer.setVolume(0.5f, 0.5f);
                this.mediaPlayer.start();
            } catch (Exception e) {
            }
        }
    }

    private boolean isZh() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return locale.equals(Locale.SIMPLIFIED_CHINESE);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

//    public void setALARMSound(int i) {
//        if (sp != null) {
//            sp.edit().putInt("ALARMSOUND", i).commit();
//        }
//    }

    private static class SingletonHolder {
        static final TpmsServer mUSBService = new TpmsServer();

        private SingletonHolder() {
        }
    }
}
