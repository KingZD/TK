package com.example.tk.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.widget.Button;

import com.example.tk.base.BaseActivity;
import com.example.tk.type.GameType;
import com.example.tk.ui.widget.DirectButton;
import com.example.tk.R;
import com.example.tk.type.TKDirect;
import com.example.tk.ui.widget.TkView;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
    }

    //担任游戏
    @OnClick(R.id.btSingle)
    void start() {
        startActivity(new Intent(this, GameActivity.class));
    }

    //创建游戏
    @OnClick(R.id.btCreate)
    void btCreate() {
        startActivity(BluetoothActivity.createGameIntent(this));
    }

    //寻找游戏
    @OnClick(R.id.btFind)
    void send() {
        startActivity(BluetoothActivity.joinGameIntent(this));
    }
}
