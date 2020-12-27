package com.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.app.ui.VideoConnect;

import static android.R.attr.start;

/**
 * Created by maojianhui on 2018/6/29.
 */

public class VideoConnectReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
//        Toast.makeText(context,"received in VideoConnectReceiver",Toast.LENGTH_SHORT).show();
        Bundle bundle=intent.getExtras();
        Intent intent1=new Intent(context.getApplicationContext(),VideoConnect.class);

        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.getApplicationContext().startActivity(intent1);
        context.startActivity(intent1);
    }


    }


