package com.app.aidl;

import android.os.IBinder;
import android.os.RemoteException;

import com.app.IBinderPool;


/**
 * 连接池实现
 * <p/>
 *
 */
public class BinderPool {

    private static final String TAG = "DEBUG-WCL: " + BinderPool.class.getSimpleName();

    public static final int BINDER_TASK = 0;
    public static final int BINDER_NOTEBOOK = 1;

    /**
     * Binder池实现
     */
    public static class BinderPoolImpl extends IBinderPool.Stub {

        public BinderPoolImpl() {
            super();
        }

        @Override public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BINDER_TASK:
                    binder = new TaskImpl();
                    break;
                case BINDER_NOTEBOOK:
                    binder = new NotebookImpl();
                    break;
                default:
                    break;
            }
            return binder;
        }
    }
}
