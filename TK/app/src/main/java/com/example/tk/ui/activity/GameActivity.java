package com.example.tk.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.example.tk.BluetoothService;
import com.example.tk.R;
import com.example.tk.base.BaseActivity;
import com.example.tk.entity.TkEntity;
import com.example.tk.listener.GameDataChangeListener;
import com.example.tk.type.GameType;
import com.example.tk.type.Player;
import com.example.tk.type.TKDirect;
import com.example.tk.ui.widget.DirectButton;
import com.example.tk.ui.widget.TkView;
import com.example.tk.util.BluetoothUtil;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.OnClick;

public class GameActivity extends BaseActivity implements GameDataChangeListener {
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
    Player player;
    GameType gameType;
    public static GameActivity instance;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_game;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        instance = this;
        tkView.setListener(this);
        btDirect.setListener(new DirectButton.OnDirectListener() {
            @Override
            public void direct(TKDirect direct) {
                tkView.changePlayer1Direct(direct);
            }
        });
        gameType = (GameType) getIntent().getSerializableExtra(GameType.FLAG);
        player = (Player) getIntent().getSerializableExtra(Player.FLAG);
    }

    public void changeGameData(TkEntity mTkEntity) {
        if (gameType == null || player == null) return;
        if (gameType.equals(GameType.CREATE) && player.equals(Player.PLAYER1)) {

        } else if (gameType.equals(GameType.CREATE) && player.equals(Player.PLAYER2)) {

        } else if (gameType.equals(GameType.JOIN) && player.equals(Player.PLAYER1)) {
            //如果是玩家2的手机，则直接同步玩家一的数据
            tkView.updatePlayer1(mTkEntity);
        } else if (gameType.equals(GameType.JOIN) && player.equals(Player.PLAYER2)) {

        }
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

    public static Intent getPlayer1Intent(Activity activity) {
        Intent mIntent = new Intent(activity, GameActivity.class);
        mIntent.putExtra(Player.FLAG, Player.PLAYER1);
        mIntent.putExtra(GameType.FLAG, GameType.CREATE);
        return mIntent;
    }

    public static Intent getPlayer2Intent(Activity activity) {
        Intent mIntent = new Intent(activity, GameActivity.class);
        mIntent.putExtra(Player.FLAG, Player.PLAYER2);
        mIntent.putExtra(GameType.FLAG, GameType.JOIN);
        return mIntent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void dataChange(String msg) {
        if (player == null || player != null && player.equals(Player.PLAYER2)) return;
        BluetoothUtil.getInstance().sendMessage(msg);
    }
}
