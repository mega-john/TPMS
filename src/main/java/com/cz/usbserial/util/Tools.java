package com.cz.usbserial.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import java.math.BigDecimal;

public class Tools {
    static final String usbHeartbeatServer = "com.cz.usbserial.activity.HeartbeatServer";
    static final String usbService = "com.cz.usbserial.activity.TpmsServer";
    static Toast toast = null;
    private static boolean flag = true;

    public static boolean isUSBService(Context context) {
        for (ActivityManager.RunningServiceInfo info : ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(30)) {
            if (usbService.equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isUSBHeartbeatServer(Context context) {
        for (ActivityManager.RunningServiceInfo info : ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(30)) {
            if (usbHeartbeatServer.equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void Toast(Context context, String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void Log(String str) {
        if (flag) {
            Log.v("TPMS", str);
        }
    }

    public static boolean isHT(int b, int i) {
        return b > i;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            String hv = Integer.toHexString(b & 0xFF);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String byteToHexString(byte b) {
        String stmp = Integer.toHexString(b & 0xFF);
        if (stmp.length() == 1) {
            stmp = "0" + stmp;
        }
        return stmp.toUpperCase();
    }

    public static byte[] sum(byte[] s) {
        byte[] buff = s;
        byte checkdata = 0;
        for (int i = 0; i < buff.length - 1; i++) {
            checkdata = (byte) (buff[i] ^ checkdata);
        }
        buff[buff.length - 1] = checkdata;
        return buff;
    }

    public static String byteToHexString(byte[] b) {
        String str;
        StringBuilder sb = new StringBuilder("");
        for (byte c : b) {
            String stmp = Integer.toHexString(c & 0xFF);
            if (stmp.length() == 1) {
                str = "0" + stmp;
            } else {
                str = stmp;
            }
            sb.append(str);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    public static byte[] requestData(byte[] data, int length) {
        byte[] buff = new byte[length];
        for (int i = 0; i < buff.length; i++) {
            buff[i] = data[i];
        }
        Log.d("REQUEST", byteToHexString(buff));
        return buff;
    }

    public static String getCurProcessName(Context context) {
        int pid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo appProcess : ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public static double div(double v1, double v2, int scale) {
        if (scale >= 0) {
            return new BigDecimal(Double.toString(v1)).divide(new BigDecimal(Double.toString(v2)), scale, 4).doubleValue();
        }
        throw new IllegalArgumentException("The scale must be a positive integer or zero");
    }

    public static byte[] getMergeBytes(byte[] pByteA, int numA, byte[] pByteB, int numB) {
        byte[] b = new byte[(numA + numB)];
        for (int i = 0; i < numA; i++) {
            b[i] = pByteA[i];
        }
        for (int i2 = 0; i2 < numB; i2++) {
            b[numA + i2] = pByteB[i2];
        }
        return b;
    }

    public static String getVersionName(Context context) throws Exception {
        return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
    }
}
