package com.example.tk;

import java.util.List;
import java.util.Map;

/**
 * 定义坦克模型
 */
public class TkModel {


    public enum Player {
        //玩家一 玩家二 电脑
        PLAYER1, PLAYER2, NPC
    }

    //定义坦克行驶方向和子弹弹射方向
    private TKDirect direct = TKDirect.UP;
    //子弹移动速度
    private int tkBallMoveSpeed = 3;
    //坦克移动速度
    private int tkMoveSpeed = 10;
    //定义当前手机游戏界面的宽高，以便在别的手机进行坐标适配
    private float phoneWidth = 0;
    private float phoneHeight = 0;
    //坦克宽高
    private float tkWidth = 0;
    private float tkHeight = 0;
    //定义坦克炮杆子和身子的大小比例 0.6 = 炮头占60/炮身占40
    private float tkHBScale = 0.6f;
    //记录坦克中心点的位置
    private float tkCenterX = 0;
    private float tkCenterY = 0;
    //暂停游戏 默认不暂停
    private boolean pauseGame;
    //坦克线条宽度
    private int tkLineWidth = 0;
    //坦克颜色
    private int tkColor = -1;
    //玩家1p 2p是根据当前创建游戏人来决定，创建者为1p
    private Player player = Player.PLAYER1;
    //记录坦克跑口的关键点坐标，方便子弹的绘制
    private float tkBulletX, tkBulletY;
    //最大同時存在子彈數目
    private int maxBulletCount = 10;
    //分数
    private int score = 0;
    //记录发出去的子弹
    private List<BullectModel> bullects;

    public TkModel(TKDirect direct, float tkWidth, float tkHeight, float tkHBScale, float tkCenterX, float tkCenterY, int tkLineWidth, int tkColor, Player player) {
        this.direct = direct;
        this.tkWidth = tkWidth;
        this.tkHeight = tkHeight;
        this.tkHBScale = tkHBScale;
        this.tkCenterX = tkCenterX;
        this.tkCenterY = tkCenterY;
        this.tkLineWidth = tkLineWidth;
        this.tkColor = tkColor;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public TKDirect getDirect() {
        return direct;
    }

    public void setDirect(TKDirect direct) {
        this.direct = direct;
    }

    public int getTkLineWidth() {
        return tkLineWidth;
    }

    public void setTkLineWidth(int tkLineWidth) {
        this.tkLineWidth = tkLineWidth;
    }

    public int getTkColor() {
        return tkColor;
    }

    public void setTkColor(int tkColor) {
        this.tkColor = tkColor;
    }

    public int getTkBallMoveSpeed() {
        return tkBallMoveSpeed;
    }

    public void setTkBallMoveSpeed(int tkBallMoveSpeed) {
        this.tkBallMoveSpeed = tkBallMoveSpeed;
    }

    public float getPhoneWidth() {
        return phoneWidth;
    }

    public void setPhoneWidth(float phoneWidth) {
        this.phoneWidth = phoneWidth;
    }

    public float getPhoneHeight() {
        return phoneHeight;
    }

    public void setPhoneHeight(float phoneHeight) {
        this.phoneHeight = phoneHeight;
    }

    public float getTkWidth() {
        return tkWidth;
    }

    public void setTkWidth(float tkWidth) {
        this.tkWidth = tkWidth;
    }

    public float getTkHeight() {
        return tkHeight;
    }

    public void setTkHeight(float tkHeight) {
        this.tkHeight = tkHeight;
    }

    public float getTkHBScale() {
        return tkHBScale;
    }

    public void setTkHBScale(float tkHBScale) {
        this.tkHBScale = tkHBScale;
    }

    public float getTkCenterX() {
        return tkCenterX;
    }

    public void setTkCenterX(float tkCenterX) {
        this.tkCenterX = tkCenterX;
    }

    public float getTkCenterY() {
        return tkCenterY;
    }

    public void setTkCenterY(float tkCenterY) {
        this.tkCenterY = tkCenterY;
    }

    public boolean isPauseGame() {
        return pauseGame;
    }

    public void setPauseGame(boolean pauseGame) {
        this.pauseGame = pauseGame;
    }

    public float getTkBulletX() {
        return tkBulletX;
    }

    public void setTkBulletX(float tkBulletX) {
        this.tkBulletX = tkBulletX;
    }

    public float getTkBulletY() {
        return tkBulletY;
    }

    public void setTkBulletY(float tkBulletY) {
        this.tkBulletY = tkBulletY;
    }

    public int getMaxBulletCount() {
        return maxBulletCount;
    }

    public void setMaxBulletCount(int maxBulletCount) {
        this.maxBulletCount = maxBulletCount;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<BullectModel> getBullects() {
        return bullects;
    }

    public void setBullects(List<BullectModel> bullects) {
        this.bullects = bullects;
    }

    public int getTkMoveSpeed() {
        return tkMoveSpeed;
    }

    public void setTkMoveSpeed(int tkMoveSpeed) {
        this.tkMoveSpeed = tkMoveSpeed;
    }
}
