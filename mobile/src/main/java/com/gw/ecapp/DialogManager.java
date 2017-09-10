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
import android.widget.TextView;

/**
 * Created by iningosu on 9/10/2017.
 */

public class DialogManager {







    public static final void showGenericConfirmDialogForOneButtons(Context context,
                                                            String title, String positiveButtonName,
                                                            final DialogListener dialogListener) {

        final Dialog mdDialog;
        mdDialog = new Dialog(context);
        mdDialog.setCancelable(false);
        mdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mdDialog.setContentView(R.layout.device_pwd_dialog_layout);
        mdDialog.setCanceledOnTouchOutside(true);
        Window window = mdDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);

        TextView tvTitle = (TextView) mdDialog
                .findViewById(R.id.wifi_name_conn_text);
        final EditText pwdDevice = (EditText) mdDialog
                .findViewById(R.id.wifi_pwd);
        Button positive = (Button) mdDialog
                .findViewById(R.id.btn_connect);


        tvTitle.setText(title);
        positive.setText(positiveButtonName);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = pwdDevice.getEditableText().toString();
                mdDialog.dismiss();
                dialogListener.positiveButtonClick(pwd);
            }
        });

        if (!((Activity) context).isFinishing())
            mdDialog.show();
    }

}
