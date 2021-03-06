package com.example.tk.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.example.tk.entity.BullectEntity;
import com.example.tk.listener.GameDataChangeListener;
import com.example.tk.util.LogUtils;
import com.example.tk.R;
import com.example.tk.type.TKDirect;
import com.example.tk.entity.TkEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.tk.type.Player;
import com.google.gson.Gson;

public class TkView extends TkBaseView {
    private GameDataChangeListener listener;
    protected final int CREATE_NPC = 0;
    protected final int CREATE_PLAYER = 1;
    //改变子弹的位置
    protected final int CHANGE_BULLECT = 2;
    //检测子弹和坦克碰撞
    protected final int CHECK_BULLECT_TK = 3;
    //检测子弹和子弹之间的碰撞
    protected final int CHECK_BULLECT = 4;
    //检测子弹板砖之间的碰撞
    protected final int CHECK_BULLECT_BRICK = 5;
    //同步数据
    protected final int UPDATE_GAME = 6;


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

    //随机生成坦克
    private void randomGenerationTK() {
        TkEntity npc1 = new TkEntity(TKDirect.DOWN, tkWidth, tkHeight, 0.2f, 0, tkHeight / 2, 5, R.color.colorPrimary, Player.NPC);
        TkEntity npc2 = new TkEntity(TKDirect.DOWN, tkWidth, tkHeight, 0.2f, gameWidth / 2, tkHeight / 2, 5, R.color.colorPrimary, Player.NPC);
        TkEntity npc3 = new TkEntity(TKDirect.DOWN, tkWidth, tkHeight, 0.2f, gameWidth - tkWidth / 2, tkHeight / 2, 5, R.color.colorPrimary, Player.NPC);
        npcModels.add(npc1);
        npcModels.add(npc2);
        npcModels.add(npc3);
        invalidate();
    }

    /*************************************玩家1控制************************************************/
    //创建玩家1
    private void createPlayer1() {
        mPlayerModel1 = new TkEntity(TKDirect.UP, tkWidth, tkHeight, 0.2f, tkWidth, 0, 5, R.color.colorPrimary, Player.PLAYER1);
        //初始化子彈集合
        mPlayerModel1.setBullects(new ArrayList<BullectEntity>());
        invalidate();
    }

    public void updatePlayer1(TkEntity tkEntity) {
        if (tkEntity == null) return;
        mPlayerModel1 = tkEntity;
        invalidate();
    }

    //发送子弹
    public void sendPlayer1Ball() {
        if (mPlayerModel1 == null) return;
        List<BullectEntity> bullects = mPlayerModel1.getBullects();
        bullects.add(new BullectEntity(mPlayerModel1.getDirect(), mPlayerModel1.getTkBulletX(), mPlayerModel1.getTkBulletY(), mPlayerModel1.getTkBulletX(), mPlayerModel1.getTkBulletY()));
    }

    //改变方向
    public void changePlayer1Direct(TKDirect direct) {
        changePlayerDirect(mPlayerModel1, direct);
    }

    /*************************************玩家1控制************************************************/


    /*************************************玩家2控制************************************************/
    //创建玩家2
    private void createPlayer2() {
        mPlayerModel2 = new TkEntity(TKDirect.UP, tkWidth, tkHeight, 0.2f, gameWidth - tkWidth * 2, 0, 5, R.color.colorPrimary, Player.PLAYER2);
        invalidate();
    }

    //改变玩家2方向位置
    public void changePlayer2Direct(TKDirect direct) {
        changePlayerDirect(mPlayerModel2, direct);
    }

    /*************************************玩家2控制************************************************/


    //创建TK开始
    public void createTkStart() {
        gameHandler.sendEmptyMessage(CREATE_NPC);
        gameHandler.sendEmptyMessage(CREATE_PLAYER);
        gameHandler.sendEmptyMessage(CHANGE_BULLECT);
        gameHandler.sendEmptyMessage(CHECK_BULLECT);
        gameHandler.sendEmptyMessage(CHECK_BULLECT_BRICK);
        gameHandler.sendEmptyMessage(CHECK_BULLECT_TK);
        gameHandler.sendEmptyMessage(UPDATE_GAME);
    }


    //改变玩家方向位置
    public void changePlayerDirect(TkEntity tkEntity, TKDirect direct) {
        if (tkEntity == null) return;
        LogUtils.i(direct.name());
        tkEntity.setDirect(direct);
        int tkMoveSpeed = tkEntity.getTkMoveSpeed();
        switch (direct) {
            case UP:
                tkEntity.setTkCenterY(tkEntity.getTkCenterY() - tkMoveSpeed);
                break;
            case LEFT:
                tkEntity.setTkCenterX(tkEntity.getTkCenterX() - tkMoveSpeed);
                break;
            case RIGHT:
                tkEntity.setTkCenterX(tkEntity.getTkCenterX() + tkMoveSpeed);
                break;
            case DOWN:
                tkEntity.setTkCenterY(tkEntity.getTkCenterY() + tkMoveSpeed);
                break;
        }
        tkEntity.setDirect(direct);
        invalidate();
    }

    //变换子弹位置
    private void changeBullet() {
        //子弹的位置跟随坦克的炮管 根据定义的方向进行绘制
        //将最大子弹数目*2是为了后面 %2 过滤出一半的数目，最终目的是为了得到1，3，5，7，9类似有间隔的数字
        //让子弹之间看起来有间距层次感 同时总子弹数目也符合 maxBulletCount 的数目
        for (TkEntity tkEntity : npcModels) {
            changeBullet(tkEntity);
        }
        changeBullet(mPlayerModel1);
        changeBullet(mPlayerModel2);
        invalidate();

    }

    private void changeBullet(TkEntity tkEntity) {
        if (tkEntity == null) return;
        //改变子弹的位置
        List<BullectEntity> bullects = mPlayerModel1.getBullects();
        if (bullects == null) return;
        for (BullectEntity next : bullects) {
            switch (next.getDirect()) {
                case UP:
                    next.setStartY(next.getStartY() - tkEntity.getTkBallMoveSpeed());
                    next.setStopY(next.getStartY() - tkEntity.getTkBallMoveSpeed() * 2);
                    break;
                case LEFT:
                    next.setStartX(next.getStartX() - tkEntity.getTkBallMoveSpeed());
                    next.setStopX(next.getStartX() - tkEntity.getTkBallMoveSpeed() * 2);
                    break;
                case RIGHT:
                    next.setStartX(next.getStartX() + tkEntity.getTkBallMoveSpeed());
                    next.setStopX(next.getStartX() + tkEntity.getTkBallMoveSpeed() * 2);
                    break;
                case DOWN:
                    next.setStartY(next.getStartY() + tkEntity.getTkBallMoveSpeed());
                    next.setStopY(next.getStartY() + tkEntity.getTkBallMoveSpeed() * 2);
                    break;
            }
        }
    }

    //检测子弹和坦克碰撞
    private void checkBulletTkCollision() {
        Iterator<TkEntity> tks = npcModels.iterator();
        while (tks.hasNext()) {
            TkEntity nextTk = tks.next();
            List<BullectEntity> bullects = mPlayerModel1.getBullects();
            if (bullects == null) break;
            Iterator<BullectEntity> iterator = bullects.iterator();
            while (iterator.hasNext()) {
                BullectEntity next = iterator.next();
                boolean b = nextTk.getTkRect().contains(next.getBullectRect(bulletSpace, nextTk.getTkLineWidth()));
                LogUtils.i(nextTk.getTkRect());
                if (b) {//如果子弹在坦克范围内则 打中坦克，子弹移除 移除坦克
                    iterator.remove();
                    tks.remove();
                }
            }
        }
        invalidate();
    }

    //检测子弹和子弹碰撞
    private void checkBulletCollision() {

    }

    //检测子弹和砖头碰撞
    private void checkBulletBrickCollision() {

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
    private Handler gameHandler = new Handler() {
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
                    gameHandler.sendEmptyMessageDelayed(CHECK_BULLECT, 40);
                    checkBulletCollision();
                    break;
                //检查子弹坦克碰撞
                case CHECK_BULLECT_TK:
                    gameHandler.sendEmptyMessageDelayed(CHECK_BULLECT_TK, 40);
                    checkBulletTkCollision();
                    break;
                //检查子弹板砖碰撞
                case CHECK_BULLECT_BRICK:
                    gameHandler.sendEmptyMessageDelayed(CHECK_BULLECT_BRICK, 40);
                    checkBulletBrickCollision();
                    break;
                case UPDATE_GAME:
                    if (listener != null) {
                        listener.dataChange(new Gson().toJson(mPlayerModel1));
                        gameHandler.sendEmptyMessageDelayed(UPDATE_GAME, 20);
                    }
                    break;
            }
        }
    };

    public void setListener(GameDataChangeListener listener) {
        this.listener = listener;
    }
}
