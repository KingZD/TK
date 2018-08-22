package com.example.tk.util;

import android.bluetooth.BluetoothAdapter;

public class BluetoothUtil {

    public static final String TAG = "BluetoothManagerUtil";

    ///////////////////////////////////////////////////////////////////////////
    // 单例模式
    private BluetoothUtil() {
    }

    public static synchronized BluetoothUtil getInstance() {
        return SingletonHolder.instance;
    }

    private static final class SingletonHolder {
        private static BluetoothUtil instance = new BluetoothUtil();
    }

    ///////////////////////////////////////////////////////////////////////////
    BluetoothAdapter defaultAdapter;

    public void openBluetooth() {
        defaultAdapter = BluetoothAdapter.getDefaultAdapter();
    }
}
