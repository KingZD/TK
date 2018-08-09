package com.example.tk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class TkView extends TkBaseView {
    protected final int CREATE_NPC = 0;
    protected final int CREATE_PLAYER = 1;

    public TkView(Context context) {
        super(context);
    }

    public TkView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TkView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        randomGenerationTK();
    }

    //随机生成坦克
    private void randomGenerationTK() {
        TkModel npc1 = new TkModel(TkModel.TKDirect.BOTTOM, 5, tkWidth, tkHeight, 0.2f, 0, gameHeight, 5, R.color.colorPrimary, TkModel.Player.NPC);
        TkModel npc2 = new TkModel(TkModel.TKDirect.BOTTOM, 5, tkWidth, tkHeight, 0.2f, (gameWidth - tkWidth) / 2, gameHeight, 5, R.color.colorPrimary, TkModel.Player.NPC);
        TkModel npc3 = new TkModel(TkModel.TKDirect.BOTTOM, 5, tkWidth, tkHeight, 0.2f, gameWidth, gameHeight, 5, R.color.colorPrimary, TkModel.Player.NPC);
        npcModels.add(npc1);
        npcModels.add(npc2);
        npcModels.add(npc3);
        invalidate();
    }

    private void createPlayer() {
        TkModel player1 = new TkModel(TkModel.TKDirect.TOP, 5, tkWidth, tkHeight, 0.2f, tkWidth, 0, 5, R.color.colorPrimary, TkModel.Player.ONE);
        TkModel player2 = new TkModel(TkModel.TKDirect.TOP, 5, tkWidth, tkHeight, 0.2f, gameWidth - tkWidth * 2, 0, 5, R.color.colorPrimary, TkModel.Player.TWO);
        npcModels.add(player1);
        npcModels.add(player2);
        invalidate();
    }

    //创建TK开始
    public void createTkAndStart(TkModel model) {
        gameHandler.sendEmptyMessageDelayed(CREATE_PLAYER, 50);
    }

    //暂停游戏
    public void pauseGame() {
        if (mModel != null)
            mModel.setPauseGame(true);
    }

    //恢复游戏
    public void resumeGame() {
        if (mModel != null)
            mModel.setPauseGame(true);
    }

    //绘制线程
    @SuppressLint("HandlerLeak")
    protected Handler gameHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CREATE_NPC:
                    randomGenerationTK();
                    break;
                case CREATE_PLAYER:
                    createPlayer();
                    break;
            }
        }
    };
}
