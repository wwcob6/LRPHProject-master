package com.punuo.sys.app.httplib;

import android.os.Handler;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.Response;

/**
 * Created by han.chen.
 * Date on 2019/4/23.
 **/
public class ExecutorDelivery {
    /**
     * Used for posting responses, typically to the main thread.
     */
    private final Executor mResponsePoster;

    public ExecutorDelivery(final Handler handler) {
        mResponsePoster = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
    }

    public void postCacheResponse(NetRequest request, Response response, Exception error) {
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, response, error));
    }

    public void postResponse(NetRequest request, Response response) {
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, response, null));
    }

    public void postError(NetRequest request, Exception error) {
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, null, error));
    }

    private class ResponseDeliveryRunnable implements Runnable {
        private final NetRequest mRequest;
        private final Response mResponse;
        private final Exception mIoException;
        private String mResponseString;

        public ResponseDeliveryRunnable(NetRequest request, Response response, Exception exception) {
            mRequest = request;
            mResponse = response;
            if (response != null) {
                try {
                    if (request.useMsgPack){
                        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(response.body().byteStream());
                        mResponseString = unpacker.unpackValue().toJson();
                        unpacker.close();
                    } else {
                        mResponseString = mResponse.body().string();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError error) {
                    System.gc();
                    exception = new ErrorTipException("您的手机内存不够啦，正在努力回收中...", error);
                }
            }
            mIoException = exception;
        }

        @Override
        public void run() {
            if (mRequest.isFinish()) {
                return;
            }
            if (mIoException != null) {
                mRequest.deliverError(mIoException);
                if (mResponse != null) {
                    mRequest.deliverResponse(mResponse, mResponseString);
                }
            } else if (mResponse != null) {
                mRequest.deliverResponse(mResponse, mResponseString);
            }
            mRequest.finish();
        }
    }
}
