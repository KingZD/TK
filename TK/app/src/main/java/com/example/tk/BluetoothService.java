package com.example.tk;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.tk.util.LogUtils;

public class BluetoothService extends Service {
    private BluetoothBinder binder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.i("onBind");
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("onCreate");
        if (binder == null)
            binder = new BluetoothBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        LogUtils.i("onRebind");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.i("onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i("onDestroy");
    }


    public class BluetoothBinder extends Binder {

    }
}
