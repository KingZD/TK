package com.example.tk.listener;

public interface BlueToothConnectCallback {
    void connecting(String serverBlueToothAddress);
    void connectSuccess(String serverBlueToothAddress);
    void connectFailure(Exception e);
}
