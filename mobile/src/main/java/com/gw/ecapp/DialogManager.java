package com.gw.ecapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by iningosu on 9/10/2017.
 */

public class DialogManager {


    public static final void showGenericConfirmDialogForOneButtons(Context context,
                                                                   String title, String positiveButtonName,
                                                                   final DialogListener dialogListener) {

        final Dialog mDialog;
        mDialog = new Dialog(context);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.device_pwd_dialog_layout);
        mDialog.setCanceledOnTouchOutside(true);
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        TextView tvTitle = (TextView) mDialog
                .findViewById(R.id.wifi_name_conn_text);
        final EditText pwdDevice = (EditText) mDialog
                .findViewById(R.id.wifi_pwd);
        Button positive = (Button) mDialog
                .findViewById(R.id.btn_connect);

        tvTitle.setText(title);
        positive.setText(positiveButtonName);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = pwdDevice.getEditableText().toString();
                mDialog.dismiss();
                dialogListener.positiveButtonClick(pwd);
            }
        });

        if (!((Activity) context).isFinishing())
            mDialog.show();
    }


    public static final void showPasswordEntryDialog(Context context, String positiveButtonName,
                                                                   final DialogListener dialogListener) {

        final Dialog mDialog;
        mDialog = new Dialog(context);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.select_pwd_entry_mode);
        mDialog.setCanceledOnTouchOutside(true);
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        final RadioButton radio_man = (RadioButton) mDialog
                .findViewById(R.id.enter_password);
        final RadioButton radio_scan = (RadioButton) mDialog
                .findViewById(R.id.scan_password);
        Button positive = (Button) mDialog
                .findViewById(R.id.btn_ok);

        positive.setText(positiveButtonName);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();

                String pwd_entry_type;
                if (radio_scan.isChecked()) {
                    pwd_entry_type = AppConstant.ENTER_PWD_SCAN;
                } else {
                    pwd_entry_type = AppConstant.ENTER_PWD_MAN;
                }

                dialogListener.positiveButtonClick(pwd_entry_type);
            }
        });
        if (!((Activity) context).isFinishing())
            mDialog.show();
    }


    public static final void showGenericConfirmDialogForTwoButtons(
            Context context,String title,
            String positiveButtonName,String negativeButtonName ,
            final TwoButtonDialogListener dialogListener) {

        final Dialog mDialog;
        mDialog = new Dialog(context);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.two_button_dialog_layout);
        mDialog.setCanceledOnTouchOutside(true);
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        TextView tvTitle = (TextView) mDialog
                .findViewById(R.id.two_btn_dialog_title);

        Button positive = (Button) mDialog
                .findViewById(R.id.btn_positive);

        Button negative = (Button) mDialog
                .findViewById(R.id.btn_negative);

        tvTitle.setText(title);
        positive.setText(positiveButtonName);


        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogListener.positiveButtonClicked();
                mDialog.dismiss();
            }
        });


        negative.setText(negativeButtonName);

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogListener.negativeButtonClicked();
                mDialog.dismiss();
            }
        });

        if (!((Activity) context).isFinishing())
            mDialog.show();
    }



}
