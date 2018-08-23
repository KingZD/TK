package com.example.tk.listener;

public interface ReceivedMessageListener {
    void onReceiveMessage(String content);
    void onConnectionInterrupt(Exception e);
}
