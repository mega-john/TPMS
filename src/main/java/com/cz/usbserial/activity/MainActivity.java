package com.cz.usbserial.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.cz.usbserial.tpms.R;
import com.cz.usbserial.util.FileUtils;
import com.cz.usbserial.util.Tools;
import com.cz.usbserial.util.UnitTools;
import com.cz.usbserial.view.UnbindDialog;

public class MainActivity extends Activity implements View.OnClickListener {
    static final String EXIT_APP_ACTION = "com.cz.action.exit_app";
    private static final int MESSAGE_HANDSHAKE_NO = 107;
    private static final int MESSAGE_USB_OPEN_FAIL = 101;
    private static final int MESSAGE_USB_OPEN_OK = 102;
    private static SharedPreferences sp = null;

    Context a;

    private final String TAG = MainActivity.class.getSimpleName();
    Context mContext = this;
    private TextView Left1_P;
    private TextView Left1_T;
    private TextView Left2_P;
    private TextView Left2_T;
    private TextView Right1_P;
    private TextView Right1_T;
    private TextView Right2_P;
    private TextView Right2_T;
    private ImageView ico_car;
    private TpmsServer mTpmsServer = null;
    private ImageView topDataStatuButton;
    private ImageView left1_bat;
    private ImageView left2_bat;
    private ImageView right1_bat;
    private ImageView right2_bat;
    private ImageView topMenuButton;
    private ImageView topMuteButton;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MainActivity.MESSAGE_USB_OPEN_FAIL) {
                if (MainActivity.this.topDataStatuButton != null) {
                    MainActivity.this.topDataStatuButton.setVisibility(View.VISIBLE);
                }
            } else if (msg.what == MainActivity.MESSAGE_USB_OPEN_OK) {
                if (MainActivity.this.topDataStatuButton != null) {
                    MainActivity.this.topDataStatuButton.setVisibility(View.GONE);
                }
            } else if (msg.what == MainActivity.MESSAGE_HANDSHAKE_NO) {
                new UnbindDialog(MainActivity.this.mContext, LayoutInflater.from(MainActivity.this.mContext).inflate(R.layout.tooltip_app_dialog, null)).show();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Intent intent = new Intent();
                        intent.setAction(MainActivity.EXIT_APP_ACTION);
                        MainActivity.this.sendBroadcast(intent);
                    }
                }, 3000);
            }
            try {
                if (TpmsServer.getDebugTest().booleanValue()) {
                    Tools.Toast(MainActivity.this.mContext, " " + Tools.bytesToHexString((byte[]) msg.getData().get("data")));
                }
                if (TpmsServer.DEBUG) {
                    Log.e(MainActivity.this.TAG, "cz111  " + Tools.bytesToHexString((byte[]) msg.getData().get("data")));
                }
                int retDataP = MainActivity.this.ShowDataP((byte[]) msg.getData().get("data"));
                int retDataT = MainActivity.this.ShowDataT((byte[]) msg.getData().get("data"));
                int retPH = TpmsServer.getWarnHighPressure();
                int retPL = TpmsServer.getWarnLowPressure();
                int retHT = TpmsServer.getWarnHighTemperature();
                if ((retDataP != 0 || retDataT != 0) && retPH != 0 && retPL != 0 && retHT != 0) {
                    if (retDataP == 1 || retDataT == 1) {
                        if (TpmsServer.left1_TyrePressure > retPH ||
                                TpmsServer.left1_TyrePressure < retPL ||
                                TpmsServer.left1_TyreTemperature > retHT ||
                                UnitTools.warning_AIR(TpmsServer.left1_Byte).booleanValue() ||
                                UnitTools.warning_P(TpmsServer.left1_Byte).booleanValue() ||
                                UnitTools.warning_Signal(TpmsServer.left1_Byte).booleanValue()) {
                            if (TpmsServer.left1_TyrePressure > retPH) {
                                MainActivity.this.Left1_P.setTextColor(Color.rgb(230, 0, 0));
                            } else if (TpmsServer.left1_TyrePressure < retPL) {
                                MainActivity.this.Left1_P.setTextColor(Color.rgb(230, 0, 0));
                            } else {
                                MainActivity.this.Left1_P.setTextColor(Color.rgb(0, 155, 67));
                            }
                            if (TpmsServer.left1_TyreTemperature > retHT) {
                                MainActivity.this.Left1_T.setTextColor(Color.rgb(230, 0, 0));
                            } else {
                                MainActivity.this.Left1_T.setTextColor(Color.rgb(68, 121, 189));
                            }
                        } else {
                            MainActivity.this.Left1_P.setTextColor(Color.rgb(0, 155, 67));
                            MainActivity.this.Left1_T.setTextColor(Color.rgb(68, 121, 189));
                        }
                    }
                    if (retDataP == 2 || retDataT == 2) {
                        if (TpmsServer.left2_TyrePressure > retPH ||
                                TpmsServer.left2_TyrePressure < retPL ||
                                TpmsServer.left2_TyreTemperature > retHT ||
                                UnitTools.warning_AIR(TpmsServer.left2_Byte).booleanValue() ||
                                UnitTools.warning_P(TpmsServer.left2_Byte).booleanValue() ||
                                UnitTools.warning_Signal(TpmsServer.left2_Byte).booleanValue()) {
                            if (TpmsServer.left2_TyrePressure > retPH) {
                                MainActivity.this.Left2_P.setTextColor(Color.rgb(230, 0, 0));
                            } else if (TpmsServer.left2_TyrePressure < retPL) {
                                MainActivity.this.Left2_P.setTextColor(Color.rgb(230, 0, 0));
                            } else {
                                MainActivity.this.Left2_P.setTextColor(Color.rgb(0, 155, 67));
                            }
                            if (TpmsServer.left2_TyreTemperature > retHT) {
                                MainActivity.this.Left2_T.setTextColor(Color.rgb(230, 0, 0));
                            } else {
                                MainActivity.this.Left2_T.setTextColor(Color.rgb(68, 121, 189));
                            }
                        } else {
                            MainActivity.this.Left2_P.setTextColor(Color.rgb(0, 155, 67));
                            MainActivity.this.Left2_T.setTextColor(Color.rgb(68, 121, 189));
                        }
                    }
                    if (retDataP == 3 || retDataT == 3) {
                        if (TpmsServer.right1_TyrePressure > retPH ||
                                TpmsServer.right1_TyrePressure < retPL ||
                                TpmsServer.right1_TyreTemperature > retHT ||
                                UnitTools.warning_AIR(TpmsServer.right1_Byte).booleanValue() ||
                                UnitTools.warning_P(TpmsServer.right1_Byte).booleanValue() ||
                                UnitTools.warning_Signal(TpmsServer.right1_Byte).booleanValue()) {
                            if (TpmsServer.right1_TyrePressure > retPH) {
                                MainActivity.this.Right1_P.setTextColor(Color.rgb(230, 0, 0));
                            } else if (TpmsServer.right1_TyrePressure < retPL) {
                                MainActivity.this.Right1_P.setTextColor(Color.rgb(230, 0, 0));
                            } else {
                                MainActivity.this.Right1_P.setTextColor(Color.rgb(0, 155, 67));
                            }
                            if (TpmsServer.right1_TyreTemperature > retHT) {
                                MainActivity.this.Right1_T.setTextColor(Color.rgb(230, 0, 0));
                            } else {
                                MainActivity.this.Right1_T.setTextColor(Color.rgb(68, 121, 189));
                            }
                        } else {
                            MainActivity.this.Right1_P.setTextColor(Color.rgb(0, 155, 67));
                            MainActivity.this.Right1_T.setTextColor(Color.rgb(68, 121, 189));
                        }
                    }
                    if (retDataP == 4 || retDataT == 4) {
                        if (TpmsServer.right2_TyrePressure > retPH ||
                                TpmsServer.right2_TyrePressure < retPL ||
                                TpmsServer.right2_TyreTemperature > retHT ||
                                UnitTools.warning_AIR(TpmsServer.right2_Byte).booleanValue() ||
                                UnitTools.warning_P(TpmsServer.right2_Byte).booleanValue() ||
                                UnitTools.warning_Signal(TpmsServer.right2_Byte).booleanValue()) {
                            if (TpmsServer.right2_TyrePressure > retPH) {
                                MainActivity.this.Right2_P.setTextColor(Color.rgb(230, 0, 0));
                            } else if (TpmsServer.right2_TyrePressure < retPL) {
                                MainActivity.this.Right2_P.setTextColor(Color.rgb(230, 0, 0));
                            } else {
                                MainActivity.this.Right2_P.setTextColor(Color.rgb(0, 155, 67));
                            }
                            if (TpmsServer.right2_TyreTemperature > retHT) {
                                MainActivity.this.Right2_T.setTextColor(Color.rgb(230, 0, 0));
                            } else {
                                MainActivity.this.Right2_T.setTextColor(Color.rgb(68, 121, 189));
                            }
                        } else {
                            MainActivity.this.Right2_P.setTextColor(Color.rgb(0, 155, 67));
                            MainActivity.this.Right2_T.setTextColor(Color.rgb(68, 121, 189));
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MainActivity.EXIT_APP_ACTION)) {
                if (TpmsServer.DEBUG) {
                    Log.i(MainActivity.this.TAG, "cz com.cz.action.exit_app");
                }
                MainActivity.this.finish();
            } else if (action.equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
                if (TpmsServer.DEBUG) {
                    Log.i(MainActivity.this.TAG, " ACTION_USB_DEVICE_DETACHED usb Unplug ");
                }
                MainActivity.this.defView(0);
            } else if (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                if (TpmsServer.DEBUG) {
                    Log.i(MainActivity.this.TAG, " ACTION_USB_ACCESSORY_ATTACHED usb insert");
                }
            }
        }
    };

    public static int getT_UNIT() {
        if (sp != null) {
            return sp.getInt("T", 0);
        }
        return 0;
    }

    public static void setT_UNIT(int i) {
        if (sp != null) {
            sp.edit().putInt("T", i).commit();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        this.mTpmsServer = TpmsServer.getInstance();
        this.mTpmsServer.addActivity(this);
        if (!"".equals(FileUtils.getFromRaw(this.mContext))) {
            FileUtils.BufferedWriterFile("vender," + FileUtils.getFromRaw(this.mContext));
        }
        sp = getSharedPreferences("TAG", 0);
        sp.registerOnSharedPreferenceChangeListener(this.mTpmsServer);
        if (!Tools.isUSBService(this)) {
            Intent intent = new Intent(this.mContext, TpmsServer.class);
            intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY | Intent.FLAG_RECEIVER_REPLACE_PENDING);
            this.mContext.startService(intent);
        } else {
            Tools.Toast(this.mContext, "Service running");
        }
        if (!Tools.isUSBHeartbeatServer(this.mContext)) {
            Intent intent2 = new Intent(this.mContext, HeartbeatServer.class);
            intent2.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY | Intent.FLAG_RECEIVER_REPLACE_PENDING);
            this.mContext.startService(intent2);
        }
        initView();
        IntentFilter filter = new IntentFilter();
        filter.addAction(EXIT_APP_ACTION);
        filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        this.mContext.registerReceiver(this.mReceiver, filter);
    }

    private void initView() {
        this.ico_car = (ImageView) findViewById(R.id.ico_car);
        this.topMenuButton = (ImageView) findViewById(R.id.topMenuButton);
        this.topMuteButton = (ImageView) findViewById(R.id.topMuteButton);
        this.topDataStatuButton = (ImageView) findViewById(R.id.topDataStatuButton);
        this.Left1_P = (TextView) findViewById(R.id.left1_p);
        this.Left1_T = (TextView) findViewById(R.id.left1_t);
        this.left1_bat = (ImageView) findViewById(R.id.left1_bat);
        this.Left2_P = (TextView) findViewById(R.id.left2_p);
        this.Left2_T = (TextView) findViewById(R.id.left2_t);
        this.left2_bat = (ImageView) findViewById(R.id.left2_bat);
        this.Right1_P = (TextView) findViewById(R.id.right1_p);
        this.Right1_T = (TextView) findViewById(R.id.right1_t);
        this.right1_bat = (ImageView) findViewById(R.id.right1_bat);
        this.Right2_P = (TextView) findViewById(R.id.right2_p);
        this.Right2_T = (TextView) findViewById(R.id.right2_t);
        this.right2_bat = (ImageView) findViewById(R.id.right2_bat);
        this.topMenuButton.setOnClickListener(this);
        this.topMuteButton.setOnClickListener(this);
        if (FileUtils.BufferReaderFile().contains("Pharos") &&
                !FileUtils.BufferReaderFile().contains("#Pharos")) {
            this.ico_car.setImageResource(R.drawable.ico_car);
        }
    }

    private void defView(int showdata) {
        if (showdata == 0 || showdata == 1) {
            if (this.Left1_P != null) {
                this.Left1_P.setText("--");
            }
            if (this.Left2_P != null) {
                this.Left2_P.setText("--");
            }
            if (this.Right1_P != null) {
                this.Right1_P.setText("--");
            }
            if (this.Right2_P != null) {
                this.Right2_P.setText("--");
            }
            if (this.Left1_T != null) {
                this.Left1_T.setText("--");
            }
            if (this.Left2_T != null) {
                this.Left2_T.setText("--");
            }
            if (this.Right1_T != null) {
                this.Right1_T.setText("--");
            }
            if (this.Right2_T != null) {
                this.Right2_T.setText("--");
            }
        }

        ShowDataBat((byte) 0, this.left1_bat);
        ShowDataBat((byte) 0, this.left2_bat);
        ShowDataBat((byte) 0, this.right1_bat);
        ShowDataBat((byte) 0, this.right2_bat);
    }

    public int ShowDataP(byte[] buff) {
        int ret = 0;
        if (buff.length < 10) {
            return 0;
        }
        if (buff[2] == 10) {
            int data = (int) (((double) Integer.valueOf(Integer.toBinaryString(buff[4] & 0xFF), 2).intValue()) * 3.44d);
            switch (buff[3]) {
                case 0:
                    UnitTools.returnP(buff[4], this.Left1_P);
                    ShowDataBat(buff[7], this.left1_bat);
                    ret = 1;
                    break;
                case 1:
                    UnitTools.returnP(buff[4], this.Right1_P);
                    ShowDataBat(buff[7], this.right1_bat);
                    ret = 3;
                    break;
                case 16:
                    UnitTools.returnP(buff[4], this.Left2_P);
                    ShowDataBat(buff[7], this.left2_bat);
                    ret = 2;
                    break;
                case 17:
                    UnitTools.returnP(buff[4], this.Right2_P);
                    ShowDataBat(buff[7], this.right2_bat);
                    ret = 4;
                    break;
                default:
                    ret = 0;
                    break;
            }
        }
        return ret;
    }

    public int ShowDataT(byte[] buff) {
        int ret = 0;
        if (buff.length < 10) {
            return 0;
        }
        if (buff[2] == 10) {
            switch (buff[3]) {
                case 0:
                    UnitTools.returnT(buff[5], this.Left1_T);
                    ShowDataBat(buff[7], this.left1_bat);
                    ret = 1;
                    break;
                case 1:
                    UnitTools.returnT(buff[5], this.Right1_T);
                    ShowDataBat(buff[7], this.right1_bat);
                    ret = 3;
                    break;
                case 16:
                    UnitTools.returnT(buff[5], this.Left2_T);
                    ShowDataBat(buff[7], this.left2_bat);
                    ret = 2;
                    break;
                case 17:
                    UnitTools.returnT(buff[5], this.Right2_T);
                    ShowDataBat(buff[7], this.right2_bat);
                    ret = 4;
                    break;
                default:
                    ret = 0;
                    break;
            }
        }
        return ret;
    }

    public int ShowDataBat(byte buff, ImageView v) {
        if (buff > 28) {
            if (v == null) {
                return 0;
            }
            v.setImageResource(R.drawable.bat3);
            return 0;
        } else if (buff > 26) {
            if (v == null) {
                return 0;
            }
            v.setImageResource(R.drawable.bat2);
            return 0;
        } else if (buff >= 23) {
            if (v == null) {
                return 0;
            }
            v.setImageResource(R.drawable.bat1);
            return 0;
        } else if (v == null) {
            return 0;
        } else {
            v.setImageResource(R.drawable.bat0);
            return 0;
        }
    }

    protected void onResume() {
        super.onResume();
        if (!Tools.isUSBService(this)) {
            Intent intent = new Intent(this.mContext, TpmsServer.class);
            intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY | Intent.FLAG_RECEIVER_REPLACE_PENDING);
            this.mContext.startService(intent);
        }
        if (this.mTpmsServer == null) {
            this.mTpmsServer = TpmsServer.getInstance();
        }
        this.mTpmsServer.addActivity(this);
        if (this.mTpmsServer != null) {
            this.mTpmsServer.registerHandler(this.mHandler);
        }
        if (getMuteStatus().booleanValue()) {
            if (this.topMuteButton != null) {
                this.topMuteButton.setImageResource(R.drawable.mute_off);
            }
        } else if (this.topMuteButton != null) {
            this.topMuteButton.setImageResource(R.drawable.mute_on);
        }
        TpmsServer.activityFlag = true;
    }

    protected void onPause() {
        super.onPause();
        if (this.mTpmsServer != null) {
            this.mTpmsServer.unregisterHandler();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (sp != null) {
            sp = null;
        }
        if (this.mReceiver != null) {
            unregisterReceiver(this.mReceiver);
            this.mReceiver = null;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topMenuButton /*2131361808*/:
                this.mContext.startActivity(new Intent(this.mContext, Menuset.class));
                return;
            case R.id.topMuteButton /*2131361810*/:
                if (!TpmsServer.getMuteStatus().booleanValue()) {
                    if (this.topMuteButton != null) {
                        this.topMuteButton.setImageResource(R.drawable.mute_off);
                    }
                    TpmsServer.setMuteStatus(true);
                    return;
                }
                if (this.topMuteButton != null) {
                    this.topMuteButton.setImageResource(R.drawable.mute_on);
                }
                TpmsServer.setMuteStatus(false);
                return;
            default:
                return;
        }
    }

    public Boolean getMuteStatus() {
        if (sp != null) {
            return Boolean.valueOf(sp.getBoolean("MUTE_STATUS", false));
        }
        return false;
    }
}