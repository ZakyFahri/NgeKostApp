package com.android.ngekostapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class LoadingAlert {
    Activity activity;
    AlertDialog dialog;
    LoadingAlert(Activity myActivity){
        activity = myActivity;
    }

    void StartAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialogloading, null));

        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }

    void closeAlertDialog(){
        dialog.dismiss();
    }

}
