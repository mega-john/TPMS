package com.cz.usbserial.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cz.usbserial.tpms.R;
import com.cz.usbserial.util.Tools;

public class SeriaTest extends Activity {
    /* access modifiers changed from: private */
    public final String TAG = MainActivity.class.getSimpleName();
    /* access modifiers changed from: private */
    public Button debug_btn;
    /* access modifiers changed from: private */
    public TextView mDumpTextView;
    private Handler mHandlerSeriaTest = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            try {
                Log.i(SeriaTest.this.TAG, "cz1  " + Tools.bytesToHexString((byte[]) msg.getData().get("data")));
                SeriaTest.this.mDumpTextView.append(" " + Tools.bytesToHexString((byte[]) msg.getData().get("data")) + " ");
            } catch (Exception e) {
            }
        }
    };
    private TextView mTitleTextView;
    private TpmsServer mTpmsServer;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.serial_console);
        this.debug_btn = (Button) findViewById(R.id.debug_btn);
        this.debug_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (TpmsServer.getDebugTest().booleanValue()) {
                    SeriaTest.this.debug_btn.setText("\u6253\u5f00\u684c\u9762\u8c03\u8bd5\u663e\u793a");
                    TpmsServer.setDebugTest(false);
                    return;
                }
                SeriaTest.this.debug_btn.setText("\u5173\u95ed\u684c\u9762\u8c03\u8bd5\u663e\u793a");
                TpmsServer.setDebugTest(true);
            }
        });
        this.mDumpTextView = (TextView) findViewById(R.id.consoleText);
        this.mTpmsServer = TpmsServer.getInstance();
        this.mTpmsServer.addActivity(this);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.mTpmsServer.registerHandler(this.mHandlerSeriaTest);
        if (TpmsServer.getDebugTest().booleanValue()) {
            this.debug_btn.setText("\u5173\u95ed\u684c\u9762\u8c03\u8bd5\u663e\u793a");
        } else {
            this.debug_btn.setText("\u6253\u5f00\u684c\u9762\u8c03\u8bd5\u663e\u793a");
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.mTpmsServer.unregisterHandler();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
    }
}
