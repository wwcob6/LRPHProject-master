package com.punuo.sys.app.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by han.chen.
 * Date on 2019-07-23.
 **/
public class BaseHandler extends Handler {
    private WeakReference<MessageHandler> mMsgHandler;

    public BaseHandler(MessageHandler msgHandler) {
        super(Looper.getMainLooper());
        mMsgHandler = new WeakReference<MessageHandler>(msgHandler);
    }

    @Override
    public void handleMessage(Message msg) {
        MessageHandler messageHandler = mMsgHandler.get();
        if (messageHandler != null) {
            messageHandler.handleMessage(msg);
        }
    }

    public interface MessageHandler {
        void handleMessage(Message msg);
    }
}
