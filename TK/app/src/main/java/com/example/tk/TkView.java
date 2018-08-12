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
    //维护一个队列进行子弹循环
    protected final int CHECK_BULLECT = 2;


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
        createPlayer1();
    }

    //随机生成坦克
    private void randomGenerationTK() {
        TkModel npc1 = new TkModel(TKDirect.DOWN, 5, tkWidth, tkHeight, 0.2f, 0, gameHeight, 5, R.color.colorPrimary, TkModel.Player.NPC);
        TkModel npc2 = new TkModel(TKDirect.DOWN, 5, tkWidth, tkHeight, 0.2f, (gameWidth - tkWidth) / 2, gameHeight, 5, R.color.colorPrimary, TkModel.Player.NPC);
        TkModel npc3 = new TkModel(TKDirect.DOWN, 5, tkWidth, tkHeight, 0.2f, gameWidth, gameHeight, 5, R.color.colorPrimary, TkModel.Player.NPC);
        npcModels.add(npc1);
        npcModels.add(npc2);
        npcModels.add(npc3);
        invalidate();
    }

    /*************************************玩家1控制************************************************/
    //创建玩家1
    private void createPlayer1() {
        mPlayerModel1 = new TkModel(TKDirect.UP, 5, tkWidth, tkHeight, 0.2f, tkWidth, 0, 5, R.color.colorPrimary, TkModel.Player.PLAYER1);
        //初始化子彈集合
        mPlayerModel1.setBullects(new ArrayList<BullectModel>());
        invalidate();
    }

    //发送子弹
    public void sendPlayer1Ball() {
        List<BullectModel> bullects = mPlayerModel1.getBullects();
        bullects.add(new BullectModel(mPlayerModel1.getDirect(), mPlayerModel1.getTkBulletX(), mPlayerModel1.getTkBulletY() - bulletSpace, mPlayerModel1.getTkBulletX(), mPlayerModel1.getTkBulletY() - bulletSpace * 2));
    }

    //改变方向
    public void changePlayer1Point(TKDirect direct) {
        changePlayerPoint(mPlayerModel1, direct);
    }

    /*************************************玩家1控制************************************************/


    /*************************************玩家2控制************************************************/
    //创建玩家2
    private void createPlayer2() {
        mPlayerModel2 = new TkModel(TKDirect.UP, 5, tkWidth, tkHeight, 0.2f, gameWidth - tkWidth * 2, 0, 5, R.color.colorPrimary, TkModel.Player.PLAYER2);
        invalidate();
    }

    //改变玩家2方向位置
    public void changePlayer2Point() {

    }

    /*************************************玩家2控制************************************************/


    //创建TK开始
    public void createTkAndStart(TkModel model) {
        gameHandler.sendEmptyMessageDelayed(CREATE_PLAYER, 50);
    }


    //改变玩家方向位置
    public void changePlayerPoint(TkModel tkModel, TKDirect direct) {
        LogUtils.i(direct.name());
        int tkBallMoveSpeed = mPlayerModel1.getTkBallMoveSpeed();
        switch (direct) {
            case UP:
                mPlayerModel1.setTkCenterY(mPlayerModel1.getTkCenterY() + tkBallMoveSpeed);
                break;
            case LEFT:
                mPlayerModel1.setTkCenterX(mPlayerModel1.getTkCenterX() - tkBallMoveSpeed);
                break;
            case RIGHT:
                mPlayerModel1.setTkCenterX(mPlayerModel1.getTkCenterX() + tkBallMoveSpeed);
                break;
            case DOWN:
                mPlayerModel1.setTkCenterY(mPlayerModel1.getTkCenterY() - tkBallMoveSpeed);
                break;
        }
        mPlayerModel1.setDirect(direct);
        invalidate();
    }

    //暂停游戏
    public void pauseGame() {
        if (mPlayerModel1 != null)
            mPlayerModel1.setPauseGame(true);
    }

    //恢复游戏
    public void resumeGame() {
        if (mPlayerModel1 != null)
            mPlayerModel1.setPauseGame(true);
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
                    createPlayer1();
                    break;
                case CHECK_BULLECT:

                    break;
            }
        }
    };
}
