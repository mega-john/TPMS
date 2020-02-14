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
    static Drawable[] layers = new Drawable[6];

    public static boolean isBuff(byte[] buff) {
        int sum = 0;
        for (int i = 0; i < buff.length - 1; i++) {
            sum ^= buff[i];
            if (sum == buff[i + 1]) {
                return true;
            }
        }
        return false;
    }

    public static void returnP(byte b, TextView t, int unit, TextView unit_id) {
        if (t != null && unit_id != null) {
            double data = (double) ((int) (((double) Integer.valueOf(Integer.toBinaryString(b & 0xFF), 2).intValue()) * 3.44d));
            switch (unit) {
                case 0:
                    t.setText(((int) Math.round(data * 10.0d)) / 10 + " ");
                    unit_id.setText("Kpa");
                    return;
                case 1:
                    t.setText(((double) Math.round((data / 6.89d) * 10.0d)) / 10.0d + " ");
                    unit_id.setText("Psi");
                    return;
                case 2:
                    t.setText(((double) Math.round(data / 10.0d)) / 10.0d + " ");
                    unit_id.setText("Bar");
                    return;
                default:
                    return;
            }
        }
    }

    public static boolean returnT(byte b, TextView t, int unit, int limit, TextView unit_id) {
        if (t == null || unit_id == null) {
            return false;
        }
        int data = Integer.valueOf(Integer.toBinaryString(b & 0xFF), 2).intValue() - 50;
        switch (unit) {
            case 0:
                t.setText(data + " ");
                unit_id.setText(" \u2103");
                break;
            case 1:
                t.setText(((double) Math.round(((((double) data) * 1.8d) + 32.0d) * 10.0d)) / 10.0d + " ");
                unit_id.setText(" \u2109");
                break;
        }
        return Tools.isHT(data, TpmsServer.getWarnHighTemperature());
    }

    public static int returnWarnP_HIGHT(byte b, int id) {
        double data = (double) ((int) (((double) Integer.valueOf(Integer.toBinaryString(b & 0xFF), 2).intValue()) * 3.44d));
        if (0 == id) {
            if (data > ((double) TpmsServer.getWarnHighTemperature())) {
                return 1;
            }
        } else if (16 == id) {
            if (data > ((double) TpmsServer.getWarnHighTemperature())) {
                return 2;
            }
        } else if (1 == id) {
            if (data > ((double) TpmsServer.getWarnHighTemperature())) {
                return 3;
            }
        } else if (17 == id) {
            if (data > ((double) TpmsServer.getWarnHighTemperature())) {
                return 4;
            }
        } else if (5 == id && data > ((double) TpmsServer.getWarnHighTemperature())) {
            return 5;
        }
        return 0;
    }

    public static int returnWarnP_LOW(byte b, int id) {
        double data = (double) ((int) (((double) Integer.valueOf(Integer.toBinaryString(b & 0xFF), 2).intValue()) * 3.44d));
        if (0 == id) {
            if (data < ((double) TpmsServer.getWarnLowPressure())) {
                return 1;
            }
        } else if (16 == id) {
            if (data < ((double) TpmsServer.getWarnLowPressure())) {
                return 2;
            }
        } else if (1 == id) {
            if (data < ((double) TpmsServer.getWarnLowPressure())) {
                return 3;
            }
        } else if (17 == id) {
            if (data < ((double) TpmsServer.getWarnLowPressure())) {
                return 4;
            }
        } else if (5 == id && data < ((double) TpmsServer.getWarnLowPressure())) {
            return 5;
        }
        return 0;
    }

    public static void returnHP(int progress, TextView t, int unit) {
        if (t != null) {
            TpmsServer.setWarnHighPressure(progress + 100);
            switch (unit) {
                case 0:
                    t.setText((progress + 100) + "Kpa");
                    return;
                case 1:
                    t.setText(df1.format((Tools.div((double) progress, 100.0d, 2) * 14.5d) + 14.5d) + "Psi");
                    return;
                case 2:
                    t.setText(df.format(Tools.div((double) progress, 100.0d, 2) + 1.0d) + "Bar");
                    return;
                default:
                    return;
            }
        }
    }

    public static void returnDP(int progress, TextView t, int unit) {
        if (t != null) {
            TpmsServer.setWarnLowPressure(progress + 100);
            switch (unit) {
                case 0:
                    t.setText((progress + 100) + "Kpa");
                    return;
                case 1:
                    t.setText(df1.format((Tools.div((double) progress, 100.0d, 2) * 14.5d) + 14.5d) + "Psi");
                    return;
                case 2:
                    t.setText(df.format(Tools.div((double) progress, 100.0d, 2) + 1.0d) + "Bar");
                    return;
                default:
                    return;
            }
        }
    }

    public static void returnT(int progress, TextView t, int unit) {
        if (t != null) {
            TpmsServer.setWarnHighTemperature(progress + 10);
            switch (unit) {
                case 0:
                    t.setText((progress + 10) + "\u2103");
                    return;
                case 1:
                    t.setText(Math.round(((((double) (progress + 50)) * 1.8d) + 32.0d) - 72.0d) + "\u2109");
                    return;
                default:
                    return;
            }
        }
    }

    public static void show_car_iamge_warn(Context mContext, ImageView v, boolean left1, boolean left2, boolean right1, boolean right2, boolean space) {
        if (v != null) {
            Resources r = mContext.getResources();
            layers[0] = r.getDrawable(R.drawable.car);
            if (left1) {
                layers[1] = r.getDrawable(R.drawable.car_left1_warn);
            } else {
                layers[1] = r.getDrawable(R.drawable.car0);
            }
            if (left2) {
                layers[2] = r.getDrawable(R.drawable.car_left2_warn);
            } else {
                layers[2] = r.getDrawable(R.drawable.car0);
            }
            if (right1) {
                layers[3] = r.getDrawable(R.drawable.car_right1_warn);
            } else {
                layers[3] = r.getDrawable(R.drawable.car0);
            }
            if (right2) {
                layers[4] = r.getDrawable(R.drawable.car_right2_warn);
            } else {
                layers[4] = r.getDrawable(R.drawable.car0);
            }
            if (!TpmsServer.getBackUpTyreStaus().booleanValue()) {
                layers[5] = r.getDrawable(R.drawable.car0);
            } else if (space) {
                layers[5] = r.getDrawable(R.drawable.car_spare_warn);
            } else {
                layers[5] = r.getDrawable(R.drawable.car0);
            }
            v.setImageDrawable(new LayerDrawable(layers));
        }
    }

    public static boolean returnState(byte b, TextView t, Context context) {
        return warning_AIR(b).booleanValue() || warning_P(b).booleanValue() || warning_Signal(b).booleanValue();
    }

    public static boolean returnW(byte b) {
        return warning_AIR(b).booleanValue() || warning_P(b).booleanValue() || warning_Signal(b).booleanValue();
    }

    public static boolean returnW2(byte data) {
        boolean z;
        z = getBit(data, 3) || getBit(data, 4) || getBit(data, 5);
        setTag(new StringBuilder(String.valueOf(z)).toString());
        setTag(getBit(data, 3) + "1");
        setTag(getBit(data, 4) + "2");
        setTag(getBit(data, 5) + "3");
        return getBit(data, 3) || getBit(data, 4) || getBit(data, 5);
    }

    public static boolean getBit(byte data, int index) {
        return ((1 << index) & data) != 0;
    }

    public static void setTag(String tag) {
    }

    public static String byteToHexString(byte b) {
        String stmp = Integer.toHexString(b & 0xFF);
        if (stmp.length() == 1) {
            stmp = "0" + stmp;
        }
        return stmp.toUpperCase();
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
