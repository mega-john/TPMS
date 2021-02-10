package com.cz.usbserial.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cz.usbserial.tpms.R;
import com.cz.usbserial.util.Tools;
import com.cz.usbserial.util.UnitTools;
import com.cz.usbserial.view.UnbindDialog;

import static com.cz.usbserial.activity.TpmsServer.HP_PROGRESS;
import static com.cz.usbserial.activity.TpmsServer.HT_PROGRESS;
import static com.cz.usbserial.activity.TpmsServer.LP_PROGRESS;

public class Menuset extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public static final int UPDATE_UNIT_VALUE = 0;
    public static final int UPDATE_TEMP_VALUE = 1;
    public static final int UPDATE_PS_MAX_VALUE = 2;
    public static final int UPDATE_PS_MIN_VALUE = 3;
    static final String EXIT_APP_ACTION = "com.cz.action.exit_app";
    Context mContext = this;
    int ps_max_progress = 0;
    int ps_min_progress = 0;
    private SeekBar ps_max_seekbar;
    private TextView ps_max_value;
    private SeekBar ps_min_seekbar;
    private TextView ps_min_value;
    private TextView tp_max_value;
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_UNIT_VALUE:
                    UnitTools.returnT(TpmsServer.getWarnHighTemperature_Progress(), Menuset.this.tp_max_value);
                    UnitTools.returnHP(TpmsServer.getWarnHighPressure_Progress(), Menuset.this.ps_max_value);
                    UnitTools.returnDP(TpmsServer.getWarnLowPressure_Progress(), Menuset.this.ps_min_value);
                    return;
                case UPDATE_TEMP_VALUE:
                    UnitTools.returnT(((Integer) msg.obj).intValue(), Menuset.this.tp_max_value);
                    return;
                case UPDATE_PS_MAX_VALUE:
                    UnitTools.returnHP(((Integer) msg.obj).intValue(), Menuset.this.ps_max_value);
                    return;
                case UPDATE_PS_MIN_VALUE:
                    UnitTools.returnDP(((Integer) msg.obj).intValue(), Menuset.this.ps_min_value);
                    return;
                default:
                    return;
            }
        }
    };
    //    private ToggleButton BackUpButton;
    private View Electricity_query;
    private View Emitter_Matching;
    private View Exit_app;
    private View SerialTest;
    private ToggleButton MuteButton;
    private SeekBar.OnSeekBarChangeListener PsMaxBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        public void onStopTrackingTouch(SeekBar seekBar) {
            TpmsServer.setWarnHighPressure_Progress(Menuset.this.ps_max_progress);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress > Menuset.this.ps_min_progress) {
                Menuset.this.ps_max_progress = progress;
                Message message = Menuset.this.mHandler.obtainMessage();
                message.what = 2;
                message.obj = Integer.valueOf(progress);
                Menuset.this.mHandler.sendMessage(message);
                return;
            }
            Tools.Toast(Menuset.this.mContext, Menuset.this.getString(R.string.low_pressure));
            Menuset.this.ps_max_progress = Menuset.this.ps_min_progress;
            Menuset.this.ps_max_seekbar.setProgress(Menuset.this.ps_min_progress);
        }
    };
    private SeekBar.OnSeekBarChangeListener PsMinBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        public void onStopTrackingTouch(SeekBar seekBar) {
            TpmsServer.setWarnLowPressure_Progress(Menuset.this.ps_min_progress);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress < Menuset.this.ps_max_progress) {
                Menuset.this.ps_min_progress = progress;
                Message message = Menuset.this.mHandler.obtainMessage();
                message.what = 3;
                message.obj = Integer.valueOf(progress);
                Menuset.this.mHandler.sendMessage(message);
                return;
            }
            Tools.Toast(Menuset.this.mContext, Menuset.this.getString(R.string.high_pressure));
            Menuset.this.ps_min_progress = Menuset.this.ps_max_progress;
            Menuset.this.ps_min_seekbar.setProgress(Menuset.this.ps_max_progress);
        }
    };
    private SeekBar.OnSeekBarChangeListener TpBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        int new_progress = 0;

        public void onStopTrackingTouch(SeekBar seekBar) {
            TpmsServer.setWarnHighTemperature_Progress(this.new_progress);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.new_progress = progress;
            Message message = Menuset.this.mHandler.obtainMessage();
            message.what = 1;
            message.obj = Integer.valueOf(progress);
            Menuset.this.mHandler.sendMessage(message);
        }
    };
    private View Transposition;
//    private RadioGroup kpa_group;
//    private RadioButton rb1;
//    private RadioButton rb2;
//    private RadioButton rb3;
    private View resdef_line;
//    private RadioButton temp1;
//    private RadioButton temp2;
//    private RadioGroup tmp_group;
    private SeekBar tp_max_seekbar;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.layout_menu);
//        initRadioBtn();
        initView();
        defView();
        this.mHandler.sendEmptyMessage(0);
    }

    protected void onResume() {
        super.onResume();
    }

//    public void initRadioBtn() {
//        this.rb1 = (RadioButton) findViewById(R.id.rb_kpa);
//        this.rb2 = (RadioButton) findViewById(R.id.rb_psi);
//        this.rb3 = (RadioButton) findViewById(R.id.rb_bar);
//        this.temp1 = (RadioButton) findViewById(R.id.temp0);
//        this.temp2 = (RadioButton) findViewById(R.id.temp1);
//    }

    public void initView() {
        this.Emitter_Matching = findViewById(R.id.emitter_matching);
        this.Transposition = findViewById(R.id.transposition);
        this.Electricity_query = findViewById(R.id.electricity_query);
        this.Emitter_Matching.setOnClickListener(this);
        this.Transposition.setOnClickListener(this);
        this.Electricity_query.setOnClickListener(this);
        this.Exit_app = findViewById(R.id.exit_app);
        this.Exit_app.setOnClickListener(this);
        this.SerialTest = findViewById(R.id.serial_test);
        this.SerialTest.setOnClickListener(this);
        this.resdef_line = findViewById(R.id.resdef_line);
        this.resdef_line.setOnClickListener(this);
//        this.kpa_group = (RadioGroup) findViewById(R.id.kpa_unit);
//        this.kpa_group.setOnCheckedChangeListener(this);
//        this.tmp_group = (RadioGroup) findViewById(R.id.temp_group);
//        this.tmp_group.setOnCheckedChangeListener(this);
//        this.BackUpButton = (ToggleButton) findViewById(R.id.backup_btn);
//        this.BackUpButton.setOnCheckedChangeListener(this);
        this.MuteButton = (ToggleButton) findViewById(R.id.voice_btn);
        this.MuteButton.setOnCheckedChangeListener(this);
        this.ps_max_value = (TextView) findViewById(R.id.presure_max);
        this.ps_min_value = (TextView) findViewById(R.id.presure_min);
        this.tp_max_value = (TextView) findViewById(R.id.temp_max);
        this.ps_max_seekbar = (SeekBar) findViewById(R.id.presure_max_seekbar);
        this.ps_min_seekbar = (SeekBar) findViewById(R.id.presure_min_seekbar);
        this.tp_max_seekbar = (SeekBar) findViewById(R.id.temp_max_seekbar);
        this.ps_max_seekbar.setMax(250);
        this.ps_max_seekbar.setOnSeekBarChangeListener(this.PsMaxBarChangeListener);
        this.ps_min_seekbar.setMax(250);
        this.ps_min_seekbar.setOnSeekBarChangeListener(this.PsMinBarChangeListener);
        this.tp_max_seekbar.setMax(116);
        this.tp_max_seekbar.setOnSeekBarChangeListener(this.TpBarChangeListener);
    }

    public void defView() {
//        if (TpmsServer.getPressure_UNIT() == 0) {
//            if (this.rb1 != null) {
//                this.rb1.setChecked(true);
//            }
//        } else if (TpmsServer.getPressure_UNIT() == 1) {
//            if (this.rb2 != null) {
//                this.rb2.setChecked(true);
//            }
//        } else if (TpmsServer.getPressure_UNIT() == P_UNIT && this.rb3 != null) {
//            this.rb3.setChecked(true);
//        }
//        if (TpmsServer.getTemperature_UNIT() == T_UNIT) {
//            if (this.temp1 != null) {
//                this.temp1.setChecked(true);
//            }
//        } else if (TpmsServer.getTemperature_UNIT() == 1 && this.temp2 != null) {
//            this.temp2.setChecked(true);
//        }
//        if (TpmsServer.getBackUpTyreStatus().booleanValue()) {
//            if (this.BackUpButton != null) {
//                this.BackUpButton.setChecked(true);
//            }
//        } else if (this.BackUpButton != null) {
//            this.BackUpButton.setChecked(false);
//        }
        if (TpmsServer.getMuteStatus().booleanValue()) {
            if (this.MuteButton != null) {
                this.MuteButton.setChecked(false);
            }
        } else if (this.MuteButton != null) {
            this.MuteButton.setChecked(true);
        }
        if (this.ps_max_seekbar != null) {
            this.ps_max_seekbar.setProgress(TpmsServer.getWarnHighPressure_Progress());
        }
        if (this.ps_min_seekbar != null) {
            this.ps_min_seekbar.setProgress(TpmsServer.getWarnLowPressure_Progress());
        }
        if (this.tp_max_seekbar != null) {
            this.tp_max_seekbar.setProgress(TpmsServer.getWarnHighTemperature_Progress());
        }
    }

    public void defInitView() {
//        TpmsServer.setPressure_UNIT(P_UNIT);
//        TpmsServer.setTemperature_UNIT(T_UNIT);
        TpmsServer.setBackUpTyreStatus(false);
        TpmsServer.setMuteStatus(true);
        TpmsServer.setWarnHighPressure_Progress(HP_PROGRESS);
        TpmsServer.setWarnLowPressure_Progress(LP_PROGRESS);
        TpmsServer.setWarnHighTemperature_Progress(HT_PROGRESS);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emitter_matching /*2131361866*/:
                Intent intent = new Intent(this.mContext, Emitter_Matching.class);
                if (intent != null) {
                    this.mContext.startActivity(intent);
                    return;
                }
                return;
            case R.id.transposition /*2131361867*/:
                Intent intent1 = new Intent(this.mContext, Tyre_Transposition.class);
                if (intent1 != null) {
                    this.mContext.startActivity(intent1);
                    return;
                }
                return;
            case R.id.electricity_query /*2131361868*/:
                Intent intent2 = new Intent(this.mContext, Electricity_Query.class);
                if (intent2 != null) {
                    this.mContext.startActivity(intent2);
                    return;
                }
                return;
            case R.id.resdef_line /*2131361888*/:
                View view1 = LayoutInflater.from(this.mContext).inflate(R.layout.setdefault_dialog, null);
                final UnbindDialog dialog1 = new UnbindDialog(this.mContext, view1);
                dialog1.show();
                view1.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Menuset.this.defInitView();
                        Menuset.this.defView();
                        dialog1.dismiss();
                    }
                });
                view1.findViewById(R.id.refuse).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog1.dismiss();
                    }
                });
                return;
            case R.id.exit_app /*2131361889*/:
//                View view = LayoutInflater.from(this.mContext).inflate(R.layout.exit_app_dialog, null);
//                final UnbindDialog dialog = new UnbindDialog(this.mContext, view);
//                dialog.show();
//                view.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        Intent intent = new Intent();
//                        intent.setAction(Menuset.EXIT_APP_ACTION);
//                        Menuset.this.sendBroadcast(intent);
//                        Menuset.this.finish();
//                    }
//                });
//                view.findViewById(R.id.refuse).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
                Intent intent_exit = new Intent();
                intent_exit.setAction(Menuset.EXIT_APP_ACTION);
                Menuset.this.sendBroadcast(intent_exit);
                Menuset.this.finish();

                return;
            case R.id.serial_test:
                Intent si = new Intent(this.mContext, com.cz.usbserial.activity.SerialTest.class);
                if (si != null) {
                    this.mContext.startActivity(si);
                    return;
                }
                return;
            default:
                return;
        }
    }

//    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        switch (checkedId) {
//            case R.id.rb_kpa /*2131361870*/:
//                TpmsServer.setPressure_UNIT(0);
//                break;
//            case R.id.rb_psi /*2131361871*/:
//                TpmsServer.setPressure_UNIT(1);
//                break;
//            case R.id.rb_bar /*2131361872*/:
//                TpmsServer.setPressure_UNIT(P_UNIT);
//                break;
//            case R.id.temp0 /*2131361874*/:
//                TpmsServer.setTemperature_UNIT(T_UNIT);
//                break;
//            case R.id.temp1 /*2131361875*/:
//                TpmsServer.setTemperature_UNIT(1);
//                break;
//        }
//        this.mHandler.sendEmptyMessage(0);
//    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        if (R.id.backup_btn == buttonView.getId()) {
//            if (isChecked) {
//                TpmsServer.setBackUpTyreStatus(true);
//            } else {
//                TpmsServer.setBackUpTyreStatus(false);
//            }
//        } else
        if (R.id.voice_btn == buttonView.getId()) {
            if (isChecked) {
                TpmsServer.setMuteStatus(false);
            } else {
                TpmsServer.setMuteStatus(true);
            }
        }
    }
}
