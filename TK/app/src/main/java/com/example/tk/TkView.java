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

public class TkView extends View {
    //TK模型
    TkModel mModel;
    List<TkModel> npcModels;
    //布局宽高
    int gameHeight = 0;
    int gameWidth = 0;
    Paint mPaint;
    //子彈閒的空格
    int bulletSpace = 10;

    public TkView(Context context) {
        super(context);
        init();
    }

    public TkView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TkView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(1);
        npcModels = new ArrayList<>();
        TkModel modelRight = new TkModel(TkModel.TKDirect.RIGHT, 5, 160, 240, 0.2f, 0, 0, 5, R.color.colorPrimary, TkModel.Player.NPC);
        TkModel modelLeft = new TkModel(TkModel.TKDirect.LEFT, 5, 160, 240, 0.2f, 0, 0, 5, R.color.colorPrimary, TkModel.Player.NPC);
        TkModel modelBottom = new TkModel(TkModel.TKDirect.BOTTOM, 5, 160, 240, 0.2f, 0, 0, 5, R.color.colorPrimary, TkModel.Player.NPC);
        npcModels.add(modelRight);
        npcModels.add(modelLeft);
        npcModels.add(modelBottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        gameHeight = h;
        gameWidth = w;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //没有模型的话不开始游戏
        if (mModel == null) return;
        //暂停游戏
        if (mModel.isPauseGame()) return;
        canvas.clipRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), gameHeight - getPaddingBottom());
        drawTopTK(canvas);
        drawRightTK(canvas);
    }

    @SuppressLint("HandlerLeak")
    private Handler gameHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mModel.setTkCenterY(mModel.getTkCenterY() + 1);
            mModel.setTkCenterX(mModel.getTkCenterX() + 1);
            invalidate();
            gameHandler.sendEmptyMessageDelayed(0, 50);
        }
    };

    //画TK(TOP)
    private void drawTopTK(Canvas canvas) {
        mModel.setPhoneHeight(gameHeight);
        mModel.setPhoneWidth(gameWidth);
        float tkLineWidth = mModel.getTkLineWidth();
        float tkHeight = mModel.getTkHeight() - tkLineWidth * 2;
        float tkWidth = mModel.getTkWidth() - tkLineWidth * 2;
        float tkCenterX = mModel.getTkCenterX() + tkWidth / 2;
        //因为初始的时候中心点应该为 tkWidth / 2
        tkCenterX = (tkCenterX + tkWidth / 2) > gameWidth ? (gameWidth - tkWidth / 2) : tkCenterX;
        float tkCenterY = gameHeight - mModel.getTkCenterY() - tkHeight / 2;
        tkCenterY = tkCenterY < 0 ? (tkHeight / 2) : tkCenterY;
        mPaint.setColor(getColor(mModel.getTkColor()));
        mPaint.setStrokeWidth(tkLineWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        //整个大炮的占位
        Path tk = new Path();

        //炮身
        float bHeight = tkHeight - tkHeight * mModel.getTkHBScale();
        Path bPath = new Path();
        bPath.moveTo(tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2);
        bPath.lineTo(tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2 - bHeight);
        bPath.lineTo(tkWidth + tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2 - bHeight);
        bPath.lineTo(tkWidth + tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2);
        bPath.lineTo(tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2);
        tk.addPath(bPath);

        //炮头
        Path hPath = new Path();
        hPath.moveTo(tkCenterX, tkCenterY - tkHeight / 2);
        hPath.lineTo(tkCenterX, tkCenterY + tkHeight / 2 - bHeight / 2);
        tk.addPath(hPath);

        //坦克原型盖子
        tk.addCircle(tkCenterX, tkCenterY + tkHeight / 2 - bHeight / 2, tkWidth / 4, Path.Direction.CCW);

        //画左轮子
        tk.moveTo(tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2);
        tk.quadTo(tkCenterX - tkWidth / 2 + tkLineWidth * 4, tkCenterY, tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2 - bHeight);

        //画右轮子
        tk.moveTo(tkCenterX + tkWidth / 2, tkCenterY + tkHeight / 2);
        tk.quadTo(tkCenterX - tkLineWidth * 4 + tkWidth / 2, tkCenterY, tkWidth / 2 + tkCenterX, tkCenterY + tkHeight / 2 - bHeight);
        canvas.drawPath(tk, mPaint);

        //记录炮口的位置
        mModel.setTkBulletX(tkCenterX);
        mModel.setTkBulletY(tkCenterY - tkHeight / 2);
        //子弹
        drawBullet(canvas);
    }

    //画TK(Right)
    private void drawRightTK(Canvas canvas) {
        TkModel mModel = npcModels.get(0);
        mModel.setPhoneHeight(gameHeight);
        mModel.setPhoneWidth(gameWidth);
        float tkLineWidth = mModel.getTkLineWidth();
        float tkHeight = mModel.getTkHeight() - tkLineWidth * 2;
        float tkWidth = mModel.getTkWidth() - tkLineWidth * 2;
        float tkCenterX = mModel.getTkCenterX() + tkWidth / 2;
        tkCenterX = tkCenterX > gameWidth ? (gameWidth - tkWidth / 2) : tkCenterX;
        float tkCenterY = gameHeight - mModel.getTkCenterY() - tkHeight / 2;
        tkCenterY = tkCenterY < 0 ? (tkHeight / 2) : tkCenterY;
        mPaint.setColor(getColor(mModel.getTkColor()));
        mPaint.setStrokeWidth(tkLineWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        //整个大炮的占位
        Path tk = new Path();

        //炮身
        float bHeight = tkHeight - tkHeight * mModel.getTkHBScale();
        Path bPath = new Path();
        bPath.moveTo(tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2);
        bPath.lineTo(tkCenterX - tkWidth / 2 + bHeight, tkCenterY + tkHeight / 2);
        bPath.lineTo(tkCenterX - tkWidth / 2 + bHeight, tkCenterY + tkHeight / 2 - tkWidth);
        bPath.lineTo(tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2 - tkWidth);
        bPath.lineTo(tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2);
        tk.addPath(bPath);

        //炮头
        Path hPath = new Path();
        hPath.moveTo(tkCenterX - tkWidth / 2 + bHeight / 2, tkCenterY + tkHeight / 2 - tkWidth / 2);
        hPath.lineTo(tkCenterX - tkWidth / 2 + tkHeight, tkCenterY + tkHeight / 2 - tkWidth / 2);
        tk.addPath(hPath);

        //坦克原型盖子
        tk.addCircle(tkCenterX - tkWidth / 2 + bHeight / 2, tkCenterY + tkHeight / 2 - tkWidth / 2, tkWidth / 4, Path.Direction.CCW);
        //画上轮子
        tk.moveTo(tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2 - tkWidth);
        tk.quadTo(tkCenterX - tkWidth / 2 + bHeight / 2, tkCenterY + tkHeight / 2 - tkWidth + tkLineWidth * 4, bHeight + tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2 - tkWidth);
        //画下轮子
        tk.moveTo(tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2);
        tk.quadTo(tkCenterX - tkWidth / 2 + bHeight / 2, tkCenterY - tkLineWidth * 4 + tkHeight / 2, bHeight + tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2);
        canvas.drawPath(tk, mPaint);

        //记录炮口的位置
        mModel.setTkBulletX(tkCenterX);
        mModel.setTkBulletY(tkCenterY - tkHeight / 2);
    }

    private void drawLeftTK(Canvas canvas) {
        TkModel mModel = npcModels.get(0);
        mModel.setPhoneHeight(gameHeight);
        mModel.setPhoneWidth(gameWidth);
        float tkLineWidth = mModel.getTkLineWidth();
        float tkHeight = mModel.getTkHeight() - tkLineWidth * 2;
        float tkWidth = mModel.getTkWidth() - tkLineWidth * 2;
        float tkCenterX = mModel.getTkCenterX() + tkWidth / 2;
        tkCenterX = tkCenterX > gameWidth ? (gameWidth - tkWidth / 2) : tkCenterX;
        float tkCenterY = gameHeight - mModel.getTkCenterY() - tkHeight / 2;
        tkCenterY = tkCenterY < 0 ? (tkHeight / 2) : tkCenterY;
        mPaint.setColor(getColor(mModel.getTkColor()));
        mPaint.setStrokeWidth(tkLineWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        //整个大炮的占位
        Path tk = new Path();

        //炮身
        float bHeight = tkHeight - tkHeight * mModel.getTkHBScale();
        Path bPath = new Path();
        bPath.moveTo(tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2);
        bPath.lineTo(tkCenterX - tkWidth / 2 + bHeight, tkCenterY + tkHeight / 2);
        bPath.lineTo(tkCenterX - tkWidth / 2 + bHeight, tkCenterY + tkHeight / 2 - tkWidth);
        bPath.lineTo(tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2 - tkWidth);
        bPath.lineTo(tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2);
        tk.addPath(bPath);

        //炮头
        Path hPath = new Path();
        hPath.moveTo(tkCenterX - tkWidth / 2 + bHeight / 2, tkCenterY + tkHeight / 2 - tkWidth / 2);
        hPath.lineTo(tkCenterX - tkWidth / 2 + tkHeight, tkCenterY + tkHeight / 2 - tkWidth / 2);
        tk.addPath(hPath);

        //坦克原型盖子
        tk.addCircle(tkCenterX - tkWidth / 2 + bHeight / 2, tkCenterY + tkHeight / 2 - tkWidth / 2, tkWidth / 4, Path.Direction.CCW);
        //画上轮子
        tk.moveTo(tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2 - tkWidth);
        tk.quadTo(tkCenterX - tkWidth / 2 + bHeight / 2, tkCenterY + tkHeight / 2 - tkWidth + tkLineWidth * 4, bHeight + tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2 - tkWidth);
        //画下轮子
        tk.moveTo(tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2);
        tk.quadTo(tkCenterX - tkWidth / 2 + bHeight / 2, tkCenterY - tkLineWidth * 4 + tkHeight / 2, bHeight + tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2);
        canvas.drawPath(tk, mPaint);

        //记录炮口的位置
        mModel.setTkBulletX(tkCenterX);
        mModel.setTkBulletY(tkCenterY - tkHeight / 2);
    }

    //画子弹
    public void drawBullet(Canvas canvas) {
        //子弹的位置跟随坦克的管子 根据定义的方向进行绘制
        //将最大子弹数目*2是为了后面 %2 过滤出一半的数目，最终目的是为了得到1，3，5，7，9类似有间隔的数字
        //让子弹之间看起来有间距层次感 同时总子弹数目也符合 maxBulletCount 的数目
        for (int i = 1; i <= mModel.getMaxBulletCount() * 2; i++) {
            if (i % 2 != 0)
                canvas.drawLine(mModel.getTkBulletX(), mModel.getTkBulletY() - i * bulletSpace, mModel.getTkBulletX(), mModel.getTkBulletY() - bulletSpace * (i + 1), mPaint);
        }

    }

    //创建TK开始
    public void createTkAndStart(TkModel model) {
        mModel = model;
        invalidate();
        gameHandler.sendEmptyMessageDelayed(0, 100);
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

    private int getColor(int color) {
        return ContextCompat.getColor(getContext(), color);
    }
}
