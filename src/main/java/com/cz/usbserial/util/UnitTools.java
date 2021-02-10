package com.cz.usbserial.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.cz.usbserial.activity.TpmsServer;
import com.cz.usbserial.tpms.R;

import java.text.DecimalFormat;

public class UnitTools {
    public static final boolean TAG = false;
    static DecimalFormat df = new DecimalFormat(".##");
    static DecimalFormat df1 = new DecimalFormat(".#");

    public static void returnP(byte b, TextView t) {
        if (t != null) {
            double data = (double) ((int) (((double) Integer.valueOf(Integer.toBinaryString(b & 0xFF), 2).intValue()) * 3.44d));
            t.setText(String.format("%.2f ", data / 10.0d / 10.0d));
        }
    }

    public static boolean returnT(byte b, TextView t) {
        if (t == null) {
            return false;
        }
        int data = Integer.valueOf(Integer.toBinaryString(b & 0xFF), 2).intValue() - 50;
        t.setText(data + " ");
        return Tools.isHT(data, TpmsServer.getWarnHighTemperature());
    }

    public static void returnHP(int progress, TextView t) {
        if (t != null) {
            TpmsServer.setWarnHighPressure(progress + 100);
            t.setText(df.format(Tools.div((double) progress, 100.0d, 2) + 1.0d) + "Bar");
        }
    }

    public static void returnDP(int progress, TextView t) {
        if (t != null) {
            TpmsServer.setWarnLowPressure(progress + 100);
            t.setText(df.format(Tools.div((double) progress, 100.0d, 2) + 1.0d) + "Bar");
        }
    }

    public static void returnT(int progress, TextView t) {
        if (t != null) {
            TpmsServer.setWarnHighTemperature(progress + 10);
            t.setText((progress + 10) + "\u2103");
        }
    }

    public static Boolean warning_AIR(byte b) {
        return Boolean.valueOf((b & 8) == 8);
    }

    public static Boolean warning_P(byte b) {
        return Boolean.valueOf((b & 16) == 16);
    }

    public static Boolean warning_Signal(byte b) {
        return Boolean.valueOf((b & 32) == 32);
    }
}
