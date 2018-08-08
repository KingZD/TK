package com.example.tk;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

public class TkView extends View {
    //TK模型
    TkModel mModel;
    //布局宽高
    int height = 0;
    int width = 0;
    Paint mPaint;

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
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
        width = w;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //没有模型的话不开始游戏
        if (mModel == null) return;
        //暂停游戏
        if (mModel.isPauseGame()) return;
        drawTK(canvas);
    }

    //画TK
    private void drawTK(Canvas canvas) {
        float tkLineWidth = mModel.getTkLineWidth();
        float tkHeight = mModel.getTkHeight() - tkLineWidth * 2;
        float tkWidth = mModel.getTkWidth() - tkLineWidth * 2;
        float tkCenterX = mModel.getTkCenterX();
        float tkCenterY = mModel.getTkCenterY();
        mPaint.setColor(getColor(mModel.getTkColor()));
        mPaint.setStrokeWidth(tkLineWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        //整个大炮的占位
        Path tk = new Path();
        //炮身
        float bHeight = tkHeight - tkHeight * mModel.getTkHBScale();
        Path bPath = new Path();
        bPath.moveTo(tkCenterX, height - bHeight - tkCenterY);
        bPath.lineTo(tkWidth + tkCenterX, height - bHeight - tkCenterY);
        bPath.lineTo(tkWidth + tkCenterX, height - tkCenterY);
        bPath.lineTo(tkCenterX, height - tkCenterY);
        bPath.lineTo(tkCenterX, height - bHeight - tkCenterY);
        tk.addPath(bPath);

        //炮头
        float hHeight = tkHeight * mModel.getTkHBScale() + bHeight / 2;
        float xWidth = (tkWidth - tkLineWidth) / 2;
        Path hPath = new Path();
        hPath.moveTo(xWidth + tkLineWidth + tkCenterX, height - tkHeight - tkCenterY);
        hPath.lineTo(xWidth + tkLineWidth + tkCenterX, height - tkHeight + hHeight - tkCenterY);
        tk.addPath(hPath);
        //坦克原型盖子
        tk.addCircle(tkWidth / 2 + tkCenterX, height - bHeight / 2 - tkCenterY, tkWidth / 4, Path.Direction.CCW);

        //画轮子
        tk.moveTo(tkWidth + tkCenterX, height - tkCenterY);
        tk.quadTo(tkWidth + tkCenterX + tkLineWidth * 4, height - tkCenterY - bHeight / 2, tkWidth + tkCenterX, height - tkCenterY - bHeight);
        canvas.drawPath(tk, mPaint);
    }

    //画子弹
    public void drawBullet(Canvas canvas){
    }

    //创建TK开始
    public void createTkAndStart(TkModel model) {
        mModel = model;
        invalidate();
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
