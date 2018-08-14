package com.example.tk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TkBaseView extends View {
    //TK模型
    protected TkModel mPlayerModel1;
    protected TkModel mPlayerModel2;
    protected List<TkModel> npcModels;
    //坦克整體大小
    protected int tkWidth = 60;
    protected int tkHeight = 100;
    //布局宽高
    protected int gameHeight = 0;
    protected int gameWidth = 0;
    private Paint mPaint;
    //子彈閒的空格
    protected int bulletSpace = 10;
    private Canvas mCanvas;

    public TkBaseView(Context context) {
        super(context);
        init();
    }

    public TkBaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TkBaseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(1);
        npcModels = new ArrayList<>();
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
        mCanvas = canvas;
        for (int i = 0; i < npcModels.size(); i++) {
            drawTK(npcModels.get(i));
        }
        drawTK(mPlayerModel1);
        drawTK(mPlayerModel2);
        drawBullet();
        drawBullet(mPlayerModel1);
        drawBullet(mPlayerModel2);
    }

    //绘制坦克
    private void drawTK(TkModel mModel) {
        drawTK(mModel, false);
    }

    //绘制坦克
    private void drawTK(TkModel mModel, boolean clearCanvas) {
        //没有模型的话不开始游戏
        if (mModel == null) return;
        //暂停游戏
        if (mModel.isPauseGame()) return;
        //没有画布就不绘制
        if (mCanvas == null) return;
        //清屏
        if (clearCanvas)
            mCanvas.clipRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), gameHeight - getPaddingBottom());
        switch (mModel.getDirect()) {
            case UP:
                drawTopTK(mModel);
                break;
            case LEFT:
                drawLeftTK(mModel);
                break;
            case RIGHT:
                drawRightTK(mModel);
                break;
            case DOWN:
                drawBottomTK(mModel);
                break;
        }
    }

    //画TK(TOP)
    private void drawTopTK(TkModel mModel) {
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
        mCanvas.drawPath(tk, mPaint);

        //记录炮口的位置
        mModel.setTkBulletX(tkCenterX);
        mModel.setTkBulletY(tkCenterY - tkHeight / 2);
    }

    //画TK(Bottom)
    private void drawBottomTK(TkModel mModel) {
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
        Path bPath = new Path();
        bPath.moveTo(tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2 - tkHeight * mModel.getTkHBScale());
        bPath.lineTo(tkCenterX - tkWidth / 2, tkCenterY - tkHeight / 2);
        bPath.lineTo(tkWidth + tkCenterX - tkWidth / 2, tkCenterY - tkHeight / 2);
        bPath.lineTo(tkWidth + tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2 - tkHeight * mModel.getTkHBScale());
        bPath.lineTo(tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2 - tkHeight * mModel.getTkHBScale());
        tk.addPath(bPath);

        //炮头
        Path hPath = new Path();
        hPath.moveTo(tkCenterX, tkCenterY + tkHeight / 2);
        hPath.lineTo(tkCenterX, tkCenterY - tkHeight * mModel.getTkHBScale() / 2);
        tk.addPath(hPath);

        //坦克原型盖子
        tk.addCircle(tkCenterX, tkCenterY - tkHeight * mModel.getTkHBScale() / 2, tkWidth / 4, Path.Direction.CCW);

        //画左轮子
        tk.moveTo(tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2 - tkHeight * mModel.getTkHBScale());
        tk.quadTo(tkCenterX - tkWidth / 2 + tkLineWidth * 4, tkCenterY - tkHeight * mModel.getTkHBScale(), tkCenterX - tkWidth / 2, tkCenterY - tkHeight / 2);

        //画右轮子
        tk.moveTo(tkCenterX + tkWidth / 2, tkCenterY + tkHeight / 2 - tkHeight * mModel.getTkHBScale());
        tk.quadTo(tkCenterX - tkLineWidth * 4 + tkWidth / 2, tkCenterY - tkHeight * mModel.getTkHBScale(), tkWidth / 2 + tkCenterX, tkCenterY - tkHeight / 2);
        mCanvas.drawPath(tk, mPaint);

        //记录炮口的位置
        mModel.setTkBulletX(tkCenterX);
        mModel.setTkBulletY(tkCenterY - tkHeight / 2);
    }

    //画TK(Right)
    private void drawRightTK(TkModel mModel) {
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
        mCanvas.drawPath(tk, mPaint);

        //记录炮口的位置
        mModel.setTkBulletX(tkCenterX);
        mModel.setTkBulletY(tkCenterY - tkHeight / 2);
    }

    private void drawLeftTK(TkModel mModel) {
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
        Path bPath = new Path();
        bPath.moveTo(tkCenterX - tkWidth / 2 + tkHeight * mModel.getTkHBScale(), tkCenterY + tkHeight / 2);
        bPath.lineTo(tkCenterX - tkWidth / 2 + tkHeight, tkCenterY + tkHeight / 2);
        bPath.lineTo(tkCenterX - tkWidth / 2 + tkHeight, tkCenterY + tkHeight / 2 - tkWidth);
        bPath.lineTo(tkCenterX - tkWidth / 2 + tkHeight * mModel.getTkHBScale(), tkCenterY + tkHeight / 2 - tkWidth);
        bPath.lineTo(tkCenterX - tkWidth / 2 + tkHeight * mModel.getTkHBScale(), tkCenterY + tkHeight / 2);
        tk.addPath(bPath);

        //炮头
        Path hPath = new Path();
        hPath.moveTo(tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2 - tkWidth / 2);
        hPath.lineTo(tkCenterX - tkWidth / 2 + tkHeight / 2 + tkHeight * mModel.getTkHBScale() / 2, tkCenterY + tkHeight / 2 - tkWidth / 2);
        tk.addPath(hPath);

        //坦克原型盖子
        tk.addCircle(tkCenterX - tkWidth / 2 + tkHeight / 2 + tkHeight * mModel.getTkHBScale() / 2, tkCenterY + tkHeight / 2 - tkWidth / 2, tkWidth / 4, Path.Direction.CCW);
        //画上轮子
        tk.moveTo(tkCenterX - tkWidth / 2 + tkHeight * mModel.getTkHBScale(), tkCenterY + tkHeight / 2 - tkWidth);
        tk.quadTo(tkCenterX - tkWidth / 2 + tkHeight / 2 + tkHeight * mModel.getTkHBScale() / 2, tkCenterY + tkHeight / 2 - tkWidth + tkLineWidth * 4, tkHeight + tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2 - tkWidth);
        //画下轮子
        tk.moveTo(tkCenterX - tkWidth / 2 + tkHeight * mModel.getTkHBScale(), tkCenterY + tkHeight / 2);
        tk.quadTo(tkCenterX - tkWidth / 2 + tkHeight / 2 + tkHeight * mModel.getTkHBScale() / 2, tkCenterY - tkLineWidth * 4 + tkHeight / 2, tkHeight + tkCenterX - tkWidth / 2, tkCenterY + tkHeight / 2);
        mCanvas.drawPath(tk, mPaint);

        //记录炮口的位置
        mModel.setTkBulletX(tkCenterX);
        mModel.setTkBulletY(tkCenterY - tkHeight / 2);
    }

    //画子弹
    public void drawBullet() {
        for (TkModel tkModel : npcModels) {
            drawBullet(tkModel);
        }
        //子弹的位置跟随坦克的管子 根据定义的方向进行绘制
        //将最大子弹数目*2是为了后面 %2 过滤出一半的数目，最终目的是为了得到1，3，5，7，9类似有间隔的数字
        //让子弹之间看起来有间距层次感 同时总子弹数目也符合 maxBulletCount 的数目
//        for (int i = 1; i <= tkModel.getMaxBulletCount() * 2; i++) {
//            if (i % 2 != 0)
//                mCanvas.drawLine(tkModel.getTkBulletX(), tkModel.getTkBulletY() - i * bulletSpace, tkModel.getTkBulletX(), tkModel.getTkBulletY() - bulletSpace * (i + 1), mPaint);
//        }
    }

    private void drawBullet(TkModel tkModel) {
        if (tkModel == null) return;
        //画玩家的子弹
        List<BullectModel> bullects = mPlayerModel1.getBullects();
        if (bullects == null) return;
        for (BullectModel next : bullects) {
            mCanvas.drawLine(next.getStartX(), next.getStartY(), next.getStopX(), next.getStopY(), mPaint);
        }
    }

    //绘制鸟巢
    private void drawHome() {
        //从屏幕中间绘制五角星
    }


    protected int getColor(int color) {
        return ContextCompat.getColor(getContext(), color);
    }
}
