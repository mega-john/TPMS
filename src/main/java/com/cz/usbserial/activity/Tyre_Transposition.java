package com.cz.usbserial.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cz.usbserial.tpms.R;
import com.cz.usbserial.util.FileUtils;
import com.cz.usbserial.util.Tools;
import com.cz.usbserial.view.UnbindDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Tyre_Transposition extends Activity {
    private static final int SHOW_ANOTHER_ACTIVITY = 8888;
    TextView backup_tire_id;
    UnbindDialog canceDialogTile = null;
    Timer mTimerSearchID;
    TimerTask mTimerTaskSearchID;
    int ret = 0;
    int ret1 = 0;
    int ret2 = 0;
    int ret3 = 0;
    int ret4 = 0;
    int ret5 = 0;
    TextView rl_low_left_id;
    TextView rl_low_right_id;
    TextView rl_top_left_id;
    TextView rl_top_right_id;
    int[] trye_id;
    private Context context;
    private TpmsServer mUSBService;
    private byte var1 = -1;
    private byte var2 = -1;
    private ImageView ico_car;
    private List<Integer> list = new ArrayList();

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (TpmsServer.DEBUG && msg.arg1 > 0) {
                Log.e("Trans", "cz111  " + Tools.bytesToHexString((byte[]) msg.getData().get("data")));
            }
            if (msg.arg1 == 7) {
                byte[] buff = (byte[]) msg.getData().get("data");
                if (buff[3] == 48 && buff[4] == Tyre_Transposition.this.var1 && buff[5] == Tyre_Transposition.this.var2) {
                    Tyre_Transposition.this.stopTimerSearchID();
                    Tyre_Transposition.this.startTimerSearchID();
                    Tyre_Transposition.this.setTimeNsSearchID(1);
                    Tyre_Transposition.this.mHandler.removeMessages(8888);
                    Tyre_Transposition.this.closeProgress(Tyre_Transposition.this.context.getString(R.string.msg_switch_success));
                }
            } else if (msg.arg1 == 9) {
                byte[] buff2 = (byte[]) msg.getData().get("data");
                switch (buff2[3]) {
                    case 1:
                        String str = Tools.byteToHexString(buff2[4]) + Tools.byteToHexString(buff2[5]) + Tools.byteToHexString(buff2[6]) + Tools.byteToHexString(buff2[7]);
                        if (Tyre_Transposition.this.rl_top_left_id != null && !"".equals(str)) {
                            Tyre_Transposition.this.rl_top_left_id.setText(str);
                            Tyre_Transposition.this.ret1 = 1;
                            TpmsServer.setLeft1_ID(str);
                            return;
                        }
                        return;
                    case 2:
                        String str1 = Tools.byteToHexString(buff2[4]) + Tools.byteToHexString(buff2[5]) + Tools.byteToHexString(buff2[6]) + Tools.byteToHexString(buff2[7]);
                        if (Tyre_Transposition.this.rl_top_right_id != null && !"".equals(str1)) {
                            Tyre_Transposition.this.rl_top_right_id.setText(str1);
                            Tyre_Transposition.this.ret2 = 2;
                            TpmsServer.setRIGHT1_ID(str1);
                            return;
                        }
                        return;
                    case 3:
                        String str2 = Tools.byteToHexString(buff2[4]) + Tools.byteToHexString(buff2[5]) + Tools.byteToHexString(buff2[6]) + Tools.byteToHexString(buff2[7]);
                        if (Tyre_Transposition.this.rl_low_left_id != null && !"".equals(str2)) {
                            Tyre_Transposition.this.rl_low_left_id.setText(str2);
                            Tyre_Transposition.this.ret3 = 3;
                            TpmsServer.setLeft2_ID(str2);
                            return;
                        }
                        return;
                    case 4:
                        String str3 = Tools.byteToHexString(buff2[4]) + Tools.byteToHexString(buff2[5]) + Tools.byteToHexString(buff2[6]) + Tools.byteToHexString(buff2[7]);
                        if (Tyre_Transposition.this.rl_low_right_id != null && !"".equals(str3)) {
                            Tyre_Transposition.this.rl_low_right_id.setText(str3);
                            Tyre_Transposition.this.ret4 = 4;
                            TpmsServer.setRIGHT2_ID(str3);
                            return;
                        }
                        return;
                    case 5:
                        String str4 = Tools.byteToHexString(buff2[4]) + Tools.byteToHexString(buff2[5]) + Tools.byteToHexString(buff2[6]) + Tools.byteToHexString(buff2[7]);
                        if (Tyre_Transposition.this.backup_tire_id != null && !"".equals(str4)) {
                            Tyre_Transposition.this.backup_tire_id.setText(str4);
                            Tyre_Transposition.this.ret5 = 5;
                            TpmsServer.setSPARE_ID(str4);
                            return;
                        }
                        return;
                    default:
                        return;
                }
            } else if (msg.what == 8888) {
                Tyre_Transposition.this.stopTimerSearchID();
                Tyre_Transposition.this.startTimerSearchID();
                Tyre_Transposition.this.setTimeNsSearchID(1);
                Tyre_Transposition.this.closeProgress(Tyre_Transposition.this.context.getString(R.string.msg_switch_success));
            }
        }
    };

    public Tyre_Transposition() {
        int[] iArr = new int[6];
        iArr[1] = R.string.l_f_tire_id;
        iArr[2] = R.string.r_f_tire_id;
        iArr[3] = R.string.l_r_tire_id;
        iArr[4] = R.string.r_r_tire_id;
        iArr[5] = R.string.r_spare_tire_id;
        this.trye_id = iArr;
        this.mTimerSearchID = null;
        this.mTimerTaskSearchID = null;
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.activity_tyre);
        if (!Tools.isUSBService(this)) {
            Tools.Toast(this, getString(R.string.restart_tpms));
        }
        this.mUSBService = TpmsServer.getInstance();
        this.mUSBService.addActivity(this);
        this.context = this;
        this.ico_car = (ImageView) findViewById(R.id.ico_car);
        if (FileUtils.BufferReaderFile().contains("Pharos") && !FileUtils.BufferReaderFile().contains("#Pharos")) {
            this.ico_car.setImageResource(R.drawable.ico_car);
        }
        this.rl_top_left_id = (TextView) findViewById(R.id.rl_top_left_id);
        this.rl_top_right_id = (TextView) findViewById(R.id.rl_top_right_id);
        this.rl_low_left_id = (TextView) findViewById(R.id.rl_low_left_id);
        this.rl_low_right_id = (TextView) findViewById(R.id.rl_low_right_id);
        this.backup_tire_id = (TextView) findViewById(R.id.backup_tire_id);
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
        if (TpmsServer.getBackUpTyreStaus().booleanValue()) {
            if (findViewById(R.id.backup_tire_transposition) != null) {
                findViewById(R.id.backup_tire_transposition).setVisibility(View.VISIBLE);
            }
        } else if (findViewById(R.id.backup_tire_transposition) != null) {
            findViewById(R.id.backup_tire_transposition).setVisibility(View.GONE);
        }
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
        if (!"".equals(TpmsServer.getSPARE_ID()) && this.backup_tire_id != null) {
            this.backup_tire_id.setText(TpmsServer.getSPARE_ID());
        }
    }


    protected void onPause() {
        super.onPause();
        this.mUSBService.unregisterHandler();
        this.mHandler.removeMessages(8888);
        stopTimerSearchID();
    }


    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_top_left_transposition /*2131361846*/:
                addList(1, R.id.rl_top_left_transposition);
                is_Transposition();
                return;
            case R.id.rl_low_left_transposition /*2131361847*/:
                addList(3, R.id.rl_low_left_transposition);
                is_Transposition();
                return;
            case R.id.backup_tire_transposition /*2131361848*/:
                addList(5, R.id.backup_tire_transposition);
                is_Transposition();
                return;
            case R.id.rl_top_right_transposition /*2131361849*/:
                addList(2, R.id.rl_top_right_transposition);
                is_Transposition();
                return;
            case R.id.rl_low_right_transposition /*2131361850*/:
                addList(4, R.id.rl_low_right_transposition);
                is_Transposition();
                return;
            default:
                return;
        }
    }

    private void addList(int var, int id) {
        if (this.list.size() < 2 && !this.list.contains(Integer.valueOf(var))) {
            this.list.add(Integer.valueOf(var));
            findViewById(id).getBackground().setLevel(1);
        }
    }


    public void creatList() {
        this.list.clear();
        findViewById(R.id.rl_top_left_transposition).getBackground().setLevel(0);
        findViewById(R.id.rl_top_right_transposition).getBackground().setLevel(0);
        findViewById(R.id.rl_low_left_transposition).getBackground().setLevel(0);
        findViewById(R.id.rl_low_right_transposition).getBackground().setLevel(0);
        findViewById(R.id.backup_tire_transposition).getBackground().setLevel(0);
    }


    public void getMessage() {
        if (this.list.size() == 2) {
            int a = this.list.get(0).intValue();
            switch (a + this.list.get(1).intValue()) {
                case 3:
                    sendMessage((byte) 0, (byte) 1);
                    return;
                case 4:
                    sendMessage((byte) 0, (byte) 16);
                    return;
                case 5:
                    if (a == 1 || a == 4) {
                        sendMessage((byte) 0, (byte) 17);
                        return;
                    } else {
                        sendMessage((byte) 1, (byte) 16);
                        return;
                    }
                case 6:
                    if (a == 1 || a == 5) {
                        sendMessage((byte) 0, (byte) 5);
                        return;
                    } else {
                        sendMessage((byte) 1, (byte) 17);
                        return;
                    }
                case 7:
                    if (a == 2 || a == 5) {
                        sendMessage((byte) 1, (byte) 5);
                        return;
                    } else {
                        sendMessage((byte) 16, (byte) 17);
                        return;
                    }
                case 8:
                    sendMessage((byte) 16, (byte) 5);
                    return;
                case 9:
                    sendMessage((byte) 17, (byte) 5);
                    return;
                default:
                    return;
            }
        }
    }

    public void is_Transposition() {
        if (this.list.size() == 2) {
            View view = LayoutInflater.from(this.context).inflate(R.layout.is_transposition_dialog, null);
            final UnbindDialog dialog = new UnbindDialog(this.context, view);
            dialog.show();
            TextView tyre_switch_id = (TextView) view.findViewById(R.id.tyre_switch_id);
            if (this.list.get(0).intValue() >= 6 || this.list.get(1).intValue() >= 6) {
                if (tyre_switch_id != null) {
                    tyre_switch_id.setText(getString(R.string.tyre_switch));
                }
            } else if (tyre_switch_id != null) {
                tyre_switch_id.setText(getString(this.trye_id[this.list.get(0).intValue()]) + " " + getString(R.string.to) + " " + getString(this.trye_id[this.list.get(1).intValue()]) + " " + getString(R.string.tyre_switch) + " ");
            }
            byte[] bArr = new byte[6];
            bArr[0] = 85;
            bArr[1] = -86;
            bArr[2] = 6;
            bArr[3] = 3;
            TpmsServer.writeData(Tools.sum(bArr));
            view.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Tyre_Transposition.this.getMessage();
                    dialog.dismiss();
                }
            });
            view.findViewById(R.id.refuse).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Tyre_Transposition.this.creatList();
                    dialog.dismiss();
                    byte[] bArr = new byte[6];
                    bArr[0] = 85;
                    bArr[1] = -86;
                    bArr[2] = 6;
                    bArr[3] = 6;
                    TpmsServer.writeData(Tools.sum(bArr));
                }
            });
        }
    }

    public void sendMessage(byte b1, byte b2) {
        int a = this.list.get(0).intValue();
        int b = this.list.get(1).intValue();
        this.var1 = b1;
        this.var2 = b2;
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(8888), 1200);
        View view = LayoutInflater.from(this.context).inflate(R.layout.exchange_dialog, null);
        this.canceDialogTile = new UnbindDialog(this.context, view);
        this.canceDialogTile.show();
        byte[] bArr = new byte[7];
        bArr[0] = 85;
        bArr[1] = -86;
        bArr[2] = 7;
        bArr[3] = 3;
        bArr[4] = b1;
        bArr[5] = b2;
        TpmsServer.writeData(Tools.sum(bArr));
        TextView tyre_switching_id = (TextView) view.findViewById(R.id.tyre_switching_id);
        if (a >= 6 || b >= 6) {
            if (tyre_switching_id != null) {
                tyre_switching_id.setText(getString(R.string.msg_switching));
            }
        } else if (tyre_switching_id != null) {
            try {
                tyre_switching_id.setText(getString(this.trye_id[a]) + " " + getString(R.string.to) + " " + getString(this.trye_id[b]) + " " + getString(R.string.msg_switching) + " ");
            } catch (Exception e) {
            }
        }
        view.findViewById(R.id.cance_domain).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                byte[] bArr = new byte[6];
                bArr[0] = 85;
                bArr[1] = -86;
                bArr[2] = 6;
                bArr[3] = 6;
                TpmsServer.writeData(Tools.sum(bArr));
                Tyre_Transposition.this.mHandler.removeMessages(8888);
                Tyre_Transposition.this.canceDialogTile.cancel();
                Tyre_Transposition.this.creatList();
            }
        });
    }


    public void closeProgress(final String str) {
        byte[] bArr = new byte[6];
        bArr[0] = 85;
        bArr[1] = -86;
        bArr[2] = 6;
        bArr[3] = 6;
        TpmsServer.writeData(Tools.sum(bArr));
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (Tyre_Transposition.this.canceDialogTile != null) {
                    Tyre_Transposition.this.canceDialogTile.dismiss();
                    Tools.Toast(Tyre_Transposition.this.context, str);
                    Tyre_Transposition.this.creatList();
                }
            }
        }, 2500);
    }


    public void startTimerSearchID() {
        if (this.mTimerSearchID == null) {
            this.mTimerSearchID = new Timer();
        }
        if (this.mTimerTaskSearchID == null) {
            this.mTimerTaskSearchID = new TimerTask() {
                public void run() {
                    if (Tyre_Transposition.this.mUSBService != null) {
                        if (Tyre_Transposition.this.ret == 2) {
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
                        if (Tyre_Transposition.this.ret > 10) {
                            try {
                                byte[] bArr2 = new byte[6];
                                bArr2[0] = 85;
                                bArr2[1] = -86;
                                bArr2[2] = 6;
                                bArr2[3] = 25;
                                TpmsServer.writeData(Tools.sum(bArr2));
                            } catch (Exception e2) {
                            }
                            Tyre_Transposition.this.ret = 0;
                        }
                        Tyre_Transposition.this.ret++;
                    }
                }
            };
        }
    }

    /* access modifiers changed from: package-private */
    public void setTimeNsSearchID(long dalayms) {
        if (this.mTimerSearchID != null && this.mTimerTaskSearchID != null) {
            this.mTimerSearchID.schedule(this.mTimerTaskSearchID, dalayms * 1000, 1000 * dalayms);
        }
    }


    public void stopTimerSearchID() {
        this.ret1 = 0;
        this.ret2 = 0;
        this.ret3 = 0;
        this.ret4 = 0;
        this.ret5 = 0;
        if (this.mTimerSearchID != null) {
            this.mTimerSearchID.cancel();
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
