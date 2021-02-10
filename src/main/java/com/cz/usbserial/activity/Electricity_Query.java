package com.cz.usbserial.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cz.usbserial.tpms.R;
import com.cz.usbserial.util.FileUtils;
import com.cz.usbserial.util.Tools;

import java.text.DecimalFormat;

public class Electricity_Query extends Activity {
    static DecimalFormat df = new DecimalFormat(".##");
    TextView rl_low_left_id;
    TextView rl_low_right_id;
    TextView rl_top_left_id;
    TextView rl_top_right_id;
    private TpmsServer mUSBService;
    private ImageView ico_car;
    private Context mContext;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (TpmsServer.DEBUG && msg.arg1 > 0) {
                Log.e("ElQuery", "cz111  " + Tools.bytesToHexString((byte[]) msg.getData().get("data")));
            }
            if (msg.arg1 == 10) {
                byte[] buff = (byte[]) msg.getData().get("data");
                if (buff[0] != 85 || buff[1] != -86 || buff[2] != 10) {
                    return;
                }
                if (buff[3] == 0) {
                    if (Electricity_Query.this.rl_top_left_id != null) {
                        Electricity_Query.this.rl_top_left_id.setText(formatElectricicty(buff[7]));
                    }
                } else if (buff[3] == 1) {
                    if (Electricity_Query.this.rl_top_right_id != null) {
                        Electricity_Query.this.rl_top_right_id.setText(formatElectricicty(buff[7]));
                    }
                } else if (buff[3] == 16) {
                    if (Electricity_Query.this.rl_low_left_id != null) {
                        Electricity_Query.this.rl_low_left_id.setText(formatElectricicty(buff[7]));
                    }
                } else if (buff[3] == 17) {
                    if (Electricity_Query.this.rl_low_right_id != null) {
                        Electricity_Query.this.rl_low_right_id.setText(formatElectricicty(buff[7]));
                    }
                }
            }
        }
    };

    private String formatElectricicty(byte b) {
        return Electricity_Query.df.format(((double) Integer.valueOf(Integer.toBinaryString(b & 0xFF), 2).intValue()) * 0.1d) + "V";
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.activity_bat_query);
        if (!Tools.isUSBService(this)) {
            Tools.Toast(this, getString(R.string.restart_tpms));
        }
        this.mUSBService = TpmsServer.getInstance();
        this.mUSBService.addActivity(this);
        this.mContext = this;
        this.rl_top_left_id = (TextView) findViewById(R.id.rl_top_left_id);
        this.rl_top_right_id = (TextView) findViewById(R.id.rl_top_right_id);
        this.rl_low_left_id = (TextView) findViewById(R.id.rl_low_left_id);
        this.rl_low_right_id = (TextView) findViewById(R.id.rl_low_right_id);
        this.ico_car = (ImageView) findViewById(R.id.ico_car);
        if (FileUtils.BufferReaderFile().contains("Pharos") &&
                !FileUtils.BufferReaderFile().contains("#Pharos")) {
            this.ico_car.setImageResource(R.drawable.ico_car);
        }
    }

    protected void onResume() {
        super.onResume();
        this.mUSBService.registerHandler(this.mHandler);
    }

    protected void onPause() {
        super.onPause();
        this.mUSBService.unregisterHandler();
    }

    protected void onDestroy() {
        super.onDestroy();
    }
}
