package com.cz.usbserial.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cz.usbserial.tpms.R;
import com.cz.usbserial.util.FileUtils;
import com.cz.usbserial.util.Tools;
import com.cz.usbserial.view.UnbindDialog;

import java.util.Timer;
import java.util.TimerTask;

public class Emitter_Matching extends Activity {
    public static final int SHOW_ANOTHER_ACTIVITY = 8888;
    public static final int SHOW_WAIT_TEXTVIEW = 10000;
    private static final int MATCHING_WAIT_TIME = 120;

    private static UnbindDialog cancelDialogTile = null;
    private static LayoutInflater Inflate = null;
    private static View view = null;
    //    public TextView backup_tire_id;
    public ProgressDialog dialog;
    int Time_count = MATCHING_WAIT_TIME;
    Timer mTimer = null;
    Timer mTimerSearchID = null;
    TimerTask mTimerTask = null;
    TimerTask mTimerTaskSearchID = null;
    private boolean Par_flag = false;
    private boolean flag = false;
    private Context mContext;
    private TpmsServer mUSBService;
    private byte position = -1;
    private int ret = 0;
    private int ret1 = 0;
    private int ret2 = 0;
    private int ret3 = 0;
    private int ret4 = 0;
    private int ret5 = 0;
    private TextView rl_low_left_id;
    private TextView rl_low_right_id;
    private TextView rl_top_left_id;
    private TextView rl_top_right_id;
    private TextView wait_time = null;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            byte[] buff;
            byte[] buff2;
            super.handleMessage(msg);
            if (TpmsServer.DEBUG && msg.arg1 > 0) {
                Log.e("Matching", "cz" + Tools.bytesToHexString((byte[]) msg.getData().get("data")));
            }
            if (msg.arg1 == 6) {
                if (Emitter_Matching.this.flag) {
                    buff2 = Tools.requestData((byte[]) msg.getData().get("data"), 6);
                } else {
                    buff2 = (byte[]) msg.getData().get("data");
                }
                Log.d("REQUEST", new StringBuilder(String.valueOf(Emitter_Matching.this.flag)).toString());
                switch (buff2[4]) {
                    case 0:
                        Emitter_Matching.this.isLearnState(buff2[3]);
                        return;
                    case 1:
                        Emitter_Matching.this.isLearnState(buff2[3]);
                        return;
                    case 5:
                        Emitter_Matching.this.isLearnState(buff2[3]);
                        return;
                    case 16:
                        Emitter_Matching.this.isLearnState(buff2[3]);
                        return;
                    case 17:
                        Emitter_Matching.this.isLearnState(buff2[3]);
                        return;
                    default:
                        return;
                }
            } else if (msg.arg1 == 9) {
                if (Emitter_Matching.this.flag) {
                    buff = Tools.requestData((byte[]) msg.getData().get("data"), 9);
                } else {
                    buff = (byte[]) msg.getData().get("data");
                }
                if (TpmsServer.DEBUG) {
                    Log.e("cz  id", "-- " + Tools.bytesToHexString(buff));
                }
                String str = Tools.byteToHexString(buff[4]) +
                        Tools.byteToHexString(buff[5]) +
                        Tools.byteToHexString(buff[6]) +
                        Tools.byteToHexString(buff[7]);
                switch (buff[3]) {
                    case 1:
//                        String str = Tools.byteToHexString(buff[4]) +
//                                Tools.byteToHexString(buff[5]) +
//                                Tools.byteToHexString(buff[6]) +
//                                Tools.byteToHexString(buff[7]);
                        if (TpmsServer.DEBUG) {
                            Log.e("cz  id", "1 -- " + str + "  " + TpmsServer.getLeft1_ID());
                        }
                        if (Emitter_Matching.this.rl_top_left_id != null && !"".equals(str)) {
                            Emitter_Matching.this.rl_top_left_id.setText(str);
                            Emitter_Matching.this.ret1 = 1;
                            TpmsServer.setLeft1_ID(str);
                            return;
                        }
                        return;
                    case 2:
//                        String str1 = Tools.byteToHexString(buff[4]) +
//                                Tools.byteToHexString(buff[5]) +
//                                Tools.byteToHexString(buff[6]) +
//                                Tools.byteToHexString(buff[7]);
                        if (Emitter_Matching.this.rl_top_right_id != null && !"".equals(str)) {
                            Emitter_Matching.this.rl_top_right_id.setText(str);
                            Emitter_Matching.this.ret2 = 2;
                            TpmsServer.setRIGHT1_ID(str);
                            return;
                        }
                        return;
                    case 3:
//                        String str2 = Tools.byteToHexString(buff[4]) +
//                                Tools.byteToHexString(buff[5]) +
//                                Tools.byteToHexString(buff[6]) +
//                                Tools.byteToHexString(buff[7]);
                        if (Emitter_Matching.this.rl_low_left_id != null && !"".equals(str)) {
                            Emitter_Matching.this.rl_low_left_id.setText(str);
                            Emitter_Matching.this.ret3 = 3;
                            TpmsServer.setLeft2_ID(str);
                            return;
                        }
                        return;
                    case 4:
//                        String str3 = Tools.byteToHexString(buff[4]) +
//                                Tools.byteToHexString(buff[5]) +
//                                Tools.byteToHexString(buff[6]) +
//                                Tools.byteToHexString(buff[7]);
                        if (Emitter_Matching.this.rl_low_right_id != null && !"".equals(str)) {
                            Emitter_Matching.this.rl_low_right_id.setText(str);
                            Emitter_Matching.this.ret4 = 4;
                            TpmsServer.setRIGHT2_ID(str);
                            return;
                        }
                        return;
                    case 5:
//                        String str4 = Tools.byteToHexString(buff[4]) +
//                                Tools.byteToHexString(buff[5]) +
//                                Tools.byteToHexString(buff[6]) +
//                                Tools.byteToHexString(buff[7]);
//                        if (Emitter_Matching.this.backup_tire_id != null && !"".equals(str)) {
//                            Emitter_Matching.this.backup_tire_id.setText(str);
//                            Emitter_Matching.this.ret5 = 5;
//                            TpmsServer.setSPARE_ID(str);
//                            return;
//                        }
                        return;
                    default:
                        return;
                }
            } else if (msg.what == SHOW_WAIT_TEXTVIEW) {
                if (Emitter_Matching.this.wait_time != null) {
                    Emitter_Matching.this.wait_time.setText(Emitter_Matching.this.Time_count + " S");
                }
                if (Emitter_Matching.this.Time_count < 1) {
                    Emitter_Matching.this.closeProgress(Emitter_Matching.this.mContext.getString(R.string.matching_fail));
                }
            } else if (msg.what == SHOW_ANOTHER_ACTIVITY) {
                Emitter_Matching.this.resetBackgroundColor();
                Emitter_Matching.this.closeProgress(Emitter_Matching.this.mContext.getString(R.string.matching_fail));
            } else if (Emitter_Matching.this.position != 255 && msg.arg1 > 6 && Emitter_Matching.this.flag) {
                byte[] buff3 = (byte[]) msg.getData().get("data");
                byte[] bus = {85, -86, 6, 24, Emitter_Matching.this.position};
                boolean matchingSuccess = true;
                for (int i = 0; i < 5; i++) {
                    if (buff3[i] != bus[i]) {
                        matchingSuccess = false;
                    }
                }
                if (matchingSuccess) {
                    Emitter_Matching.this.stopTimerSearchID();
                    Emitter_Matching.this.startTimerSearchID();
                    Emitter_Matching.this.setTimeNsSearchID(1);
                    Emitter_Matching.this.resetBackgroundColor();
                    Emitter_Matching.this.mHandler.removeMessages(Emitter_Matching.SHOW_ANOTHER_ACTIVITY);
                    Emitter_Matching.this.closeProgress(Emitter_Matching.this.mContext.getString(R.string.msg_study_success));
                    Emitter_Matching.this.flag = false;
                    Log.v("Agint", "two");
                }
            }
        }
    };
    private ImageView ico_car;
    private TextView match_id = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.activity_study);
        if (!Tools.isUSBService(this)) {
            Tools.Toast(this, getString(R.string.restart_tpms));
        }
        this.mUSBService = TpmsServer.getInstance();
        this.mUSBService.addActivity(this);
        this.mContext = this;
        Inflate = LayoutInflater.from(this.mContext);
        view = Inflate.inflate(R.layout.matching_dialog, null);
        cancelDialogTile = new UnbindDialog(this.mContext, view);
        this.rl_top_left_id = (TextView) findViewById(R.id.rl_top_left_id);
        this.rl_top_right_id = (TextView) findViewById(R.id.rl_top_right_id);
        this.rl_low_left_id = (TextView) findViewById(R.id.rl_low_left_id);
        this.rl_low_right_id = (TextView) findViewById(R.id.rl_low_right_id);
//        this.backup_tire_id = (TextView) findViewById(R.id.backup_tire_id);
        this.ico_car = (ImageView) findViewById(R.id.ico_car);
        if (FileUtils.BufferReaderFile().contains("Pharos") && !FileUtils.BufferReaderFile().contains("#Pharos")) {
            this.ico_car.setImageResource(R.drawable.ico_car);
        }
        stopTimerSearchID();
        startTimerSearchID();
        setTimeNsSearchID(1);
    }

    protected void onResume() {
        super.onResume();
        this.mUSBService.registerHandler(this.mHandler);
        stopTimerSearchID();
        startTimerSearchID();
        setTimeNsSearchID(1);
//        if (TpmsServer.getBackUpTyreStatus().booleanValue()) {
//            if (findViewById(R.id.backup_tire_matching) != null) {
//                findViewById(R.id.backup_tire_matching).setVisibility(View.VISIBLE);
//            }
//        } else if (findViewById(R.id.backup_tire_matching) != null) {
//            findViewById(R.id.backup_tire_matching).setVisibility(View.GONE);
//        }
        if (!"".equals(TpmsServer.getLeft1_ID()) && this.rl_top_left_id != null) {
            this.rl_top_left_id.setText(TpmsServer.getLeft1_ID());
        }
        if (!"".equals(TpmsServer.getLeft2_ID()) && this.rl_low_left_id != null) {
            this.rl_low_left_id.setText(TpmsServer.getLeft2_ID());
        }
        if (!"".equals(TpmsServer.getRIGHT1_ID()) && this.rl_top_right_id != null) {
            this.rl_top_right_id.setText(TpmsServer.getRIGHT1_ID());
        }
        if (!"".equals(TpmsServer.getRIGHT2_ID()) && this.rl_low_right_id != null) {
            this.rl_low_right_id.setText(TpmsServer.getRIGHT2_ID());
        }
//        if (!"".equals(TpmsServer.getSPARE_ID()) && this.backup_tire_id != null) {
//            this.backup_tire_id.setText(TpmsServer.getSPARE_ID());
//        }
    }

    protected void onPause() {
        super.onPause();
        this.mUSBService.unregisterHandler();
        this.mHandler.removeMessages(SHOW_ANOTHER_ACTIVITY);
        stopTimer();
        stopTimerSearchID();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void resetBackgroundColor() {
        findViewById(R.id.rl_top_left_matching).getBackground().setLevel(0);
        findViewById(R.id.rl_top_right_matching).getBackground().setLevel(0);
        findViewById(R.id.rl_low_left_matching).getBackground().setLevel(0);
        findViewById(R.id.rl_low_right_matching).getBackground().setLevel(0);
//        findViewById(R.id.backup_tire_matching).getBackground().setLevel(0);
    }

    public void onClick(View v) {
        resetBackgroundColor();
        switch (v.getId()) {
            case R.id.rl_top_left_matching /*2131361841*/:
                this.position = 0;
                v.getBackground().setLevel(1);
                break;
            case R.id.rl_low_left_matching /*2131361842*/:
                this.position = 16;
                v.getBackground().setLevel(1);
                break;
//            case R.id.backup_tire_matching /*2131361843*/:
//                this.position = 5;
//                v.getBackground().setLevel(1);
//                break;
            case R.id.rl_top_right_matching /*2131361844*/:
                this.position = 1;
                v.getBackground().setLevel(1);
                break;
            case R.id.rl_low_right_matching /*2131361845*/:
                this.position = 17;
                v.getBackground().setLevel(1);
                break;
        }
        this.Par_flag = true;
        sendMatch(this.position);
    }

    private void sendMatch(byte key) {
        if (-1 != key) {
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(SHOW_ANOTHER_ACTIVITY), 120000);
            this.match_id = (TextView) view.findViewById(R.id.match_id);
            if (0 == key) {
                this.match_id.setText(getString(R.string.l_f_tire) + getString(R.string.matchinging));
            } else if (1 == key) {
                this.match_id.setText(getString(R.string.r_f_tire) + getString(R.string.matchinging));
            } else if (16 == key) {
                this.match_id.setText(getString(R.string.l_r_tire) + getString(R.string.matchinging));
            } else if (17 == key) {
                this.match_id.setText(getString(R.string.r_r_tire) + getString(R.string.matchinging));
            } else if (5 == key) {
                this.match_id.setText(getString(R.string.r_spare_tire) + getString(R.string.matchinging));
            }
            if (this.wait_time == null) {
                this.wait_time = (TextView) view.findViewById(R.id.wait_time);
            }
            view.findViewById(R.id.cance_domain).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        byte[] bArr = new byte[6];
                        bArr[0] = 85;
                        bArr[1] = -86;
                        bArr[2] = 6;
                        bArr[3] = 6;
                        TpmsServer.writeData(Tools.sum(bArr));
                    } catch (Exception e) {
                    }
                    Emitter_Matching.this.mHandler.removeMessages(Emitter_Matching.SHOW_ANOTHER_ACTIVITY);
                    Emitter_Matching.cancelDialogTile.cancel();
                    Emitter_Matching.this.wait_time = null;
                    Emitter_Matching.this.resetBackgroundColor();
                }
            });
            this.wait_time.setText("120 S");
            cancelDialogTile.show();
            try {
                byte[] bArr = new byte[6];
                bArr[0] = 85;
                bArr[1] = -86;
                bArr[2] = 6;
                bArr[3] = 1;
                bArr[4] = key;
                TpmsServer.writeData(Tools.sum(bArr));
            } catch (Exception e) {
            }
            this.Time_count = MATCHING_WAIT_TIME;
            stopTimer();
            startTimer();
            setTimeMs(1);
            return;
        }
        Log.i("Agint", "cz key = 0xff ");
    }

    private void isLearnState(byte b1) {
        if (b1 == 16) {
            Log.v("Agint", "one");
            this.flag = true;
            Log.d("REQUEST", new StringBuilder(String.valueOf(this.flag)).toString());
        } else if (b1 == 24) {
            stopTimerSearchID();
            startTimerSearchID();
            setTimeNsSearchID(1);
            resetBackgroundColor();
            this.mHandler.removeMessages(SHOW_ANOTHER_ACTIVITY);
            closeProgress(this.mContext.getString(R.string.msg_study_success));
            this.flag = false;
            Log.v("Agint", "two");
        }
    }

    private void closeProgress(final String str) {
        try {
            byte[] bArr = new byte[6];
            bArr[0] = 85;
            bArr[1] = -86;
            bArr[2] = 6;
            bArr[3] = 6;
            TpmsServer.writeData(Tools.sum(bArr));
        } catch (Exception e) {
        }
        this.mHandler.removeMessages(SHOW_ANOTHER_ACTIVITY);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (Emitter_Matching.this.dialog != null) {
                    Emitter_Matching.this.dialog.dismiss();
                    Tools.Toast(Emitter_Matching.this.mContext, str);
                    Emitter_Matching.this.position = (byte) -1;
                    Emitter_Matching.this.Par_flag = false;
                } else if (Emitter_Matching.cancelDialogTile != null) {
                    Emitter_Matching.cancelDialogTile.dismiss();
                    Tools.Toast(Emitter_Matching.this.mContext, str);
                    Emitter_Matching.this.position = (byte) -1;
                    Emitter_Matching.this.wait_time = null;
                    Emitter_Matching.this.Par_flag = false;
                }
            }
        }, 2500);
    }

    private void startTimer() {
        if (this.mTimer == null) {
            this.mTimer = new Timer();
        }
        if (this.mTimerTask == null) {
            this.mTimerTask = new TimerTask() {
                public void run() {
                    if (Emitter_Matching.cancelDialogTile != null) {
                        Emitter_Matching emitter_Matching = Emitter_Matching.this;
                        emitter_Matching.Time_count--;
                        Message message = new Message();
                        message.what = SHOW_WAIT_TEXTVIEW;
                        message.obj = Integer.valueOf(Emitter_Matching.this.Time_count);
                        Emitter_Matching.this.mHandler.sendMessage(message);
                        if (Emitter_Matching.this.Time_count < 1) {
                            Emitter_Matching.this.stopTimer();
                        }
                        try {
                            byte[] bArr = new byte[6];
                            bArr[0] = 85;
                            bArr[1] = -86;
                            bArr[2] = 6;
                            bArr[3] = 25;
                            TpmsServer.writeData(Tools.sum(bArr));
                        } catch (Exception e) {
                        }
                    } else {
                        Emitter_Matching.this.stopTimer();
                    }
                }
            };
        }
    }

    private void setTimeMs(long dalayms) {
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

    private void startTimerSearchID() {
        if (this.mTimerSearchID == null) {
            this.mTimerSearchID = new Timer();
        }
        if (this.mTimerTaskSearchID == null) {
            this.mTimerTaskSearchID = new TimerTask() {
                public void run() {
                    if (Emitter_Matching.this.mUSBService != null) {
                        if (Emitter_Matching.this.ret == 2) {
                            try {
                                byte[] bArr = new byte[6];
                                bArr[0] = 85;
                                bArr[1] = -86;
                                bArr[2] = 6;
                                bArr[3] = 7;
                                TpmsServer.writeData(Tools.sum(bArr));
                            } catch (Exception e) {
                            }
                        }
                        if (Emitter_Matching.this.ret > 10) {
                            try {
                                byte[] bArr2 = new byte[6];
                                bArr2[0] = 85;
                                bArr2[1] = -86;
                                bArr2[2] = 6;
                                bArr2[3] = 25;
                                TpmsServer.writeData(Tools.sum(bArr2));
                            } catch (Exception e2) {
                            }
                            Emitter_Matching.this.ret = 0;
                        }
                        Emitter_Matching emitter_Matching = Emitter_Matching.this;
                        emitter_Matching.ret = emitter_Matching.ret + 1;
                    }
                }
            };
        }
    }

    private void setTimeNsSearchID(long dalayms) {
        if (this.mTimerSearchID != null && this.mTimerTaskSearchID != null) {
            this.mTimerSearchID.schedule(this.mTimerTaskSearchID, dalayms * 1000, 1000 * dalayms);
        }
    }

    private void stopTimerSearchID() {
        this.ret1 = 0;
        this.ret2 = 0;
        this.ret3 = 0;
        this.ret4 = 0;
        this.ret5 = 0;
        if (this.mTimerSearchID != null) {
            this.mTimerSearchID.cancel();
            this.mTimerSearchID.purge();
            this.mTimerSearchID = null;
        }
        if (this.mTimerTaskSearchID != null) {
            this.mTimerTaskSearchID.cancel();
            this.mTimerTaskSearchID = null;
        }
        byte[] bArr = new byte[6];
        bArr[0] = 85;
        bArr[1] = -86;
        bArr[2] = 6;
        bArr[3] = 6;
        TpmsServer.writeData(Tools.sum(bArr));
    }
}
