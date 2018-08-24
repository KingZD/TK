package com.example.tk.listener;

public interface ReceivedMessageListener {
    void onConnectedSuccess();
    void onReceiveMessage(String content);
    void onConnectionInterrupt(Exception e);
}
