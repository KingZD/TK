package com.example.tk.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.example.tk.R;
import com.example.tk.base.BaseActivity;
import com.example.tk.type.TKDirect;
import com.example.tk.ui.widget.DirectButton;
import com.example.tk.ui.widget.TkView;

import butterknife.BindView;
import butterknife.OnClick;

public class GameActivity extends BaseActivity {
    @BindView(R.id.tkView)
    TkView tkView;
    @BindView(R.id.btDirect)
    DirectButton btDirect;
    @BindView(R.id.btStart)
    Button btStart;
    @BindView(R.id.btSend)
    Button btSend;
    @BindView(R.id.btReset)
    Button btReset;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        startActivity(new Intent(this, BluetoothActivity.class));
        btDirect.setListener(new DirectButton.OnDirectListener() {
            @Override
            public void direct(TKDirect direct) {
                tkView.changePlayer1Direct(direct);
            }
        });
    }

    //开始游戏
    @OnClick(R.id.btStart)
    void start() {
        tkView.createTkStart();
    }

    //发送子弹
    @OnClick(R.id.btSend)
    void send() {
        tkView.sendPlayer1Ball();
    }

    //重开
    @OnClick(R.id.btReset)
    void reset() {

    }
}
