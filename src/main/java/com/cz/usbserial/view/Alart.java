package com.cz.usbserial.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.cz.usbserial.activity.MainActivity;
import com.cz.usbserial.activity.TpmsServer;
import com.cz.usbserial.tpms.R;

import java.util.Locale;

public class Alart {
    private static Alart mEasyTouch;
    private Context context;
    private boolean isShowIng = false;
    private ViewHolder mHolder;
    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;
    private View mainView;

    private Alart(Context context2) {
        this.context = context2;
        creatWM();
    }

    public static Alart initEasyTouch(Context context2) {
        if (mEasyTouch == null) {
            mEasyTouch = new Alart(context2);
        }
        return mEasyTouch;
    }

    private void creatWM() {
        this.mWindowManager = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        this.mLayoutParams = new WindowManager.LayoutParams();
        this.mLayoutParams.type = 2003;
        this.mLayoutParams.format = -3;
        this.mLayoutParams.flags = 32776;
    }

    public void showDialog(String str) {
        if (!this.isShowIng) {
            if (this.mainView == null) {
                this.mainView = LayoutInflater.from(this.context).inflate(R.layout.warn_app_dialog, null);
                this.mHolder = new ViewHolder(this, null);
                this.mHolder.btn_examine = this.mainView.findViewById(R.id.btn_examine);
                this.mHolder.btn_unexamine = this.mainView.findViewById(R.id.btn_unexamine);
                this.mHolder.tv_warn_data = this.mainView.findViewById(R.id.tv_warn_data);
                if (str == null || "".equals(str)) {
                    str = this.context.getString(R.string.alart_tip);
                }
                if (this.mHolder.tv_warn_data != null) {
                    this.mHolder.tv_warn_data.setText(str);
                }
                if (this.mHolder.btn_examine != null) {
                    this.mHolder.btn_examine.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(Alart.this.context, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Alart.this.context.startActivity(intent);
                            Alart.this.closeDialog();
                        }
                    });
                }
                if (this.mHolder.btn_unexamine != null) {
                    this.mHolder.btn_unexamine.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Alart.this.closeDialog();
                            TpmsServer.activityFlag = false;
                        }
                    });
                }
            }
            this.mLayoutParams.alpha = 1.0f;
            this.mLayoutParams.x = 0;
            this.mLayoutParams.y = 0;
            this.mLayoutParams.width = -1;
            this.mLayoutParams.height = -1;
            WindowManager.LayoutParams layoutParams = this.mLayoutParams;
            this.mLayoutParams.gravity = 49;
            layoutParams.gravity = 49;
            this.isShowIng = true;
            this.mWindowManager.addView(this.mainView, this.mLayoutParams);
        }
    }

    public void closeDialog() {
        if (this.isShowIng) {
            this.mWindowManager.removeView(this.mainView);
            this.isShowIng = false;
            this.mainView = null;
        }
    }

    private boolean isZh() {
        return this.context.getResources().getConfiguration().locale.equals(Locale.SIMPLIFIED_CHINESE);
    }

    private class ViewHolder {
        private Button btn_examine;
        private Button btn_unexamine;
        private TextView tv_warn_data;

        private ViewHolder() {
        }

        public ViewHolder(Alart alart, ViewHolder viewHolder) { this(); }
    }
}
