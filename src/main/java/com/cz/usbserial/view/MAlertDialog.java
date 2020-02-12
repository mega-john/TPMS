package com.cz.usbserial.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cz.usbserial.tpms.R;

public class MAlertDialog {
    public static AlertDialog.Builder showDiolg(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.alart_dialog, (ViewGroup) null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.create();
        return builder;
    }

    public static ProgressDialog showProgress(Context context, String str1, String str2) {
        return ProgressDialog.show(context, str1, str2, true, false);
    }
}
