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
    //改变子弹的位置
    protected final int CHANGE_BULLECT = 2;
    //维护一个队列进行子弹循环
    protected final int CHECK_BULLECT = 3;


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
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        gameHandler.sendEmptyMessage(CHANGE_BULLECT);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        randomGenerationTK();
        createPlayer1();
    }

    //随机生成坦克
    private void randomGenerationTK() {
        TkModel npc1 = new TkModel(TKDirect.DOWN, tkWidth, tkHeight, 0.2f, 0, gameHeight, 5, R.color.colorPrimary, TkModel.Player.NPC);
        TkModel npc2 = new TkModel(TKDirect.DOWN, tkWidth, tkHeight, 0.2f, (gameWidth - tkWidth) / 2, gameHeight, 5, R.color.colorPrimary, TkModel.Player.NPC);
        TkModel npc3 = new TkModel(TKDirect.DOWN, tkWidth, tkHeight, 0.2f, gameWidth, gameHeight, 5, R.color.colorPrimary, TkModel.Player.NPC);
        npcModels.add(npc1);
        npcModels.add(npc2);
        npcModels.add(npc3);
        invalidate();
    }

    /*************************************玩家1控制************************************************/
    //创建玩家1
    private void createPlayer1() {
        mPlayerModel1 = new TkModel(TKDirect.UP, tkWidth, tkHeight, 0.2f, tkWidth, 0, 5, R.color.colorPrimary, TkModel.Player.PLAYER1);
        //初始化子彈集合
        mPlayerModel1.setBullects(new ArrayList<BullectModel>());
        invalidate();
    }

    //发送子弹
    public void sendPlayer1Ball() {
        List<BullectModel> bullects = mPlayerModel1.getBullects();
        bullects.add(new BullectModel(mPlayerModel1.getDirect(), mPlayerModel1.getTkBulletX(), mPlayerModel1.getTkBulletY(), mPlayerModel1.getTkBulletX(), mPlayerModel1.getTkBulletY()));
    }

    //改变方向
    public void changePlayer1Direct(TKDirect direct) {
        changePlayerDirect(mPlayerModel1, direct);
    }

    /*************************************玩家1控制************************************************/


    /*************************************玩家2控制************************************************/
    //创建玩家2
    private void createPlayer2() {
        mPlayerModel2 = new TkModel(TKDirect.UP, tkWidth, tkHeight, 0.2f, gameWidth - tkWidth * 2, 0, 5, R.color.colorPrimary, TkModel.Player.PLAYER2);
        invalidate();
    }

    //改变玩家2方向位置
    public void changePlayer2Direct(TKDirect direct) {
        changePlayerDirect(mPlayerModel2, direct);
    }

    /*************************************玩家2控制************************************************/


    //创建TK开始
    public void createTkStart() {
        gameHandler.sendEmptyMessageDelayed(CREATE_PLAYER, 50);
    }


    //改变玩家方向位置
    public void changePlayerDirect(TkModel tkModel, TKDirect direct) {
        if (tkModel == null) return;
        LogUtils.i(direct.name());
        tkModel.setDirect(direct);
        int tkMoveSpeed = tkModel.getTkMoveSpeed();
        switch (direct) {
            case UP:
                tkModel.setTkCenterY(tkModel.getTkCenterY() + tkMoveSpeed);
                break;
            case LEFT:
                tkModel.setTkCenterX(tkModel.getTkCenterX() - tkMoveSpeed);
                break;
            case RIGHT:
                tkModel.setTkCenterX(tkModel.getTkCenterX() + tkMoveSpeed);
                break;
            case DOWN:
                tkModel.setTkCenterY(tkModel.getTkCenterY() - tkMoveSpeed);
                break;
        }
        tkModel.setDirect(direct);
        invalidate();
    }

    //变换子弹位置
    private void changeBullet() {
        //子弹的位置跟随坦克的管子 根据定义的方向进行绘制
        //将最大子弹数目*2是为了后面 %2 过滤出一半的数目，最终目的是为了得到1，3，5，7，9类似有间隔的数字
        //让子弹之间看起来有间距层次感 同时总子弹数目也符合 maxBulletCount 的数目
        for (TkModel tkModel : npcModels) {
            changeBullet(tkModel);
        }
        changeBullet(mPlayerModel1);
        changeBullet(mPlayerModel2);
        invalidate();

    }

    private void changeBullet(TkModel tkModel) {
        if (tkModel == null) return;
        //改变子弹的位置
        List<BullectModel> bullects = mPlayerModel1.getBullects();
        if (bullects == null) return;
        for (BullectModel next : bullects) {
            switch (next.getDirect()) {
                case UP:
                    next.setStartY(next.getStartY() - tkModel.getTkBallMoveSpeed());
                    next.setStopY(next.getStartY() - tkModel.getTkBallMoveSpeed() * 2);
                    break;
                case LEFT:
                    next.setStartX(next.getStartX() - tkModel.getTkBallMoveSpeed());
                    next.setStopX(next.getStartX() - tkModel.getTkBallMoveSpeed() * 2);
                    break;
                case RIGHT:
                    next.setStartX(next.getStartX() + tkModel.getTkBallMoveSpeed());
                    next.setStopX(next.getStartX() + tkModel.getTkBallMoveSpeed() * 2);
                    break;
                case DOWN:
                    next.setStartY(next.getStartY() + tkModel.getTkBallMoveSpeed());
                    next.setStopY(next.getStartY() + tkModel.getTkBallMoveSpeed() * 2);
                    break;
            }
        }
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
                //创建NPC
                case CREATE_NPC:
                    randomGenerationTK();
                    break;
                //创建玩家
                case CREATE_PLAYER:
                    createPlayer1();
                    break;
                //改变子弹位置
                case CHANGE_BULLECT:
                    gameHandler.sendEmptyMessageDelayed(CHANGE_BULLECT, 40);
                    changeBullet();
                    break;
                //检查子弹碰撞
                case CHECK_BULLECT:

                    break;
            }
        }
    };
}
