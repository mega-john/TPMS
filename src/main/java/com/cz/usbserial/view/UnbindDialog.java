package com.cz.usbserial.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.cz.usbserial.tpms.R;

public class UnbindDialog extends Dialog {
    LayoutInflater inflater;

    public UnbindDialog(Context context, View view) {
        super(context, R.style.DialogStyle);
        this.inflater = LayoutInflater.from(context);
        setContentView(view);
        setCancelable(false);
    }
}
