package com.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.app.ui.VideoStartActivity;

/**
 * Created by maojianhui on 2018/7/11.
 */

public class VideoStartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
//        Toast.makeText(context,"received in VideoConnectReceiver",Toast.LENGTH_SHORT).show();
        Bundle bundle=intent.getExtras();
        Intent intent1=new Intent(context.getApplicationContext(), VideoStartActivity.class);

        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.getApplicationContext().startActivity(intent1);
        context.startActivity(intent1);
    }
}
