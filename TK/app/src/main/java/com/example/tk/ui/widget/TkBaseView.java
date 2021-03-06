package com.example.tk.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.example.tk.entity.BullectEntity;
import com.example.tk.R;
import com.example.tk.entity.TkEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TkBaseView extends View {
    //TK模型
    protected TkEntity mPlayerModel1;
    protected TkEntity mPlayerModel2;
    protected List<TkEntity> npcModels;
    //坦克整體大小
    protected int tkWidth = 100;
    protected int tkHeight = 100;
    //布局宽高
    protected int gameHeight = 0;
    protected int gameWidth = 0;
    private Paint mPaint;
    //子彈閒的空格
    protected int bulletSpace = 10;
    private Canvas mCanvas;
    RectF mCalculatePressBounds;
    Region mCalculatePressRegion;
    private Bitmap mHome;

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
        mCalculatePressBounds = new RectF();
        mCalculatePressRegion = new Region();
        //不允许在原图上进行操作
        mHome = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)
                .copy(Bitmap.Config.ARGB_8888, true);
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
        //主持大局的玩家都没有还玩蛇皮
        if (mPlayerModel1 == null)
            return;
        //玩家暂停
        if (mPlayerModel1.isPauseGame() || mPlayerModel2 != null && mPlayerModel2.isPauseGame())
            return;
        mCanvas = canvas;
        Iterator<TkEntity> iterator = npcModels.iterator();
        while (iterator.hasNext()) {
            drawTK(iterator.next());
        }
        drawHome();
        drawTK(mPlayerModel1);
        drawTK(mPlayerModel2);
        drawBullet();
        drawBullet(mPlayerModel1);
        drawBullet(mPlayerModel2);
    }

    //绘制坦克
    private void drawTK(TkEntity mModel) {
        drawTK(mModel, false);
    }

    //绘制坦克
    private void drawTK(TkEntity mModel, boolean clearCanvas) {
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
    private void drawTopTK(TkEntity mModel) {
        mModel.setPhoneHeight(gameHeight);
        mModel.setPhoneWidth(gameWidth);
        float tkLineWidth = mModel.getTkLineWidth();
        float tkHeight = mModel.getTkHeight() - tkLineWidth * 2;
        float tkWidth = mModel.getTkWidth() - tkLineWidth * 2;
        float tkCenterX = mModel.getTkCenterX() <= 0 ? (mModel.getTkCenterX() + tkWidth / 2) : mModel.getTkCenterX();
        //因为初始的时候中心点应该为 tkWidth / 2
        float tkCenterY = mModel.getTkCenterY() <= 0 ? (gameHeight - mModel.getTkCenterY() - tkHeight / 2) : mModel.getTkCenterY();
        tkCenterY = tkCenterY <= tkHeight / 2 ? tkHeight / 2 : tkCenterY;
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
        //记录中心坐标点
        mModel.setTkCenterX(tkCenterX);
        mModel.setTkCenterY(tkCenterY);
    }

    //画TK(Bottom)
    private void drawBottomTK(TkEntity mModel) {
        mModel.setPhoneHeight(gameHeight);
        mModel.setPhoneWidth(gameWidth);
        float tkLineWidth = mModel.getTkLineWidth();
        float tkHeight = mModel.getTkHeight() - tkLineWidth * 2;
        float tkWidth = mModel.getTkWidth() - tkLineWidth * 2;
        float tkCenterX = mModel.getTkCenterX() <= 0 ? (mModel.getTkCenterX() + tkWidth / 2) : mModel.getTkCenterX();
        //因为初始的时候中心点应该为 tkWidth / 2
        float tkCenterY = mModel.getTkCenterY() <= 0 ? (gameHeight - mModel.getTkCenterY() - tkHeight / 2) : mModel.getTkCenterY();
        tkCenterY = (tkCenterY + tkHeight / 2 >= gameHeight) ? (gameHeight - tkHeight / 2) : tkCenterY;
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
        mModel.setTkBulletY(tkCenterY + tkHeight / 2);
        //记录中心坐标点
        mModel.setTkCenterX(tkCenterX);
        mModel.setTkCenterY(tkCenterY);
    }

    //画TK(Right)
    private void drawRightTK(TkEntity mModel) {
        mModel.setPhoneHeight(gameHeight);
        mModel.setPhoneWidth(gameWidth);
        float tkLineWidth = mModel.getTkLineWidth();
        float tkHeight = mModel.getTkHeight() - tkLineWidth * 2;
        float tkWidth = mModel.getTkWidth() - tkLineWidth * 2;
        float tkCenterX = mModel.getTkCenterX() <= 0 ? (gameWidth - mModel.getTkCenterX() - tkHeight / 2) : mModel.getTkCenterX();
        tkCenterX = tkCenterX + tkWidth / 2 >= gameWidth ? (gameWidth - tkWidth / 2) : tkCenterX;
        float tkCenterY = mModel.getTkCenterY() <= 0 ? (mModel.getTkCenterY() + tkWidth / 2) : mModel.getTkCenterY();

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
        mModel.setTkBulletX(tkCenterX - tkWidth / 2 + bHeight / 2);
        mModel.setTkBulletY(tkCenterY + tkHeight / 2 - tkWidth / 2);
        //记录中心坐标点
        mModel.setTkCenterX(tkCenterX);
        mModel.setTkCenterY(tkCenterY);
    }

    private void drawLeftTK(TkEntity mModel) {
        mModel.setPhoneHeight(gameHeight);
        mModel.setPhoneWidth(gameWidth);
        float tkLineWidth = mModel.getTkLineWidth();
        float tkHeight = mModel.getTkHeight() - tkLineWidth * 2;
        float tkWidth = mModel.getTkWidth() - tkLineWidth * 2;
        float tkCenterX = mModel.getTkCenterX() <= 0 ? (gameWidth - mModel.getTkCenterX() - tkHeight / 2) : mModel.getTkCenterX();
        tkCenterX = tkCenterX <= tkWidth / 2 ? tkWidth / 2 : tkCenterX;
        float tkCenterY = mModel.getTkCenterY() <= 0 ? (mModel.getTkCenterY() + tkWidth / 2) : mModel.getTkCenterY();
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
        mModel.setTkBulletX(tkCenterX - tkWidth / 2);
        mModel.setTkBulletY(tkCenterY + tkHeight / 2 - tkWidth / 2);
        //记录中心坐标点
        mModel.setTkCenterX(tkCenterX);
        mModel.setTkCenterY(tkCenterY);
    }

    //画子弹
    public void drawBullet() {
        for (TkEntity tkEntity : npcModels) {
            drawBullet(tkEntity);
        }
    }

    //子弹的位置跟随坦克的管子 根据定义的方向进行绘制
    //将最大子弹数目*2是为了后面 %2 过滤出一半的数目，最终目的是为了得到1，3，5，7，9类似有间隔的数字
    //让子弹之间看起来有间距层次感 同时总子弹数目也符合 maxBulletCount 的数目
    private void drawBullet(TkEntity tkEntity) {
        //没有模型的话不开始游戏
        if (tkEntity == null) return;
        //暂停游戏
        if (tkEntity.isPauseGame()) return;
        //没有画布就不绘制
        if (mCanvas == null) return;
        //画玩家的子弹
        List<BullectEntity> bullects = mPlayerModel1.getBullects();
        if (bullects == null) return;
        Iterator<BullectEntity> iterator = bullects.iterator();
        while (iterator.hasNext()) {
            BullectEntity next = iterator.next();
            switch (next.getDirect()) {
                case UP:
                    mCanvas.drawLine(next.getStartX(), next.getStartY() + bulletSpace, next.getStopX(), next.getStopY(), mPaint);
                    //如果子弹底部的位置Y坐标小于0代表已经超出屏幕，移除掉
                    if (next.getStartY() <= 0)
                        iterator.remove();
                    break;
                case LEFT:
                    mCanvas.drawLine(next.getStartX() + bulletSpace, next.getStartY(), next.getStopX(), next.getStopY(), mPaint);
                    //如果子弹底部的位置Y坐标小于0代表已经超出屏幕，移除掉
                    if (next.getStartX() <= 0)
                        iterator.remove();
                    break;
                case RIGHT:
                    mCanvas.drawLine(next.getStartX(), next.getStartY(), next.getStopX() + bulletSpace, next.getStopY(), mPaint);
                    //如果子弹底部的位置Y坐标大于屏幕高度代表已经超出屏幕，移除掉
                    if (next.getStartX() >= gameWidth)
                        iterator.remove();
                    break;
                case DOWN:
                    mCanvas.drawLine(next.getStartX(), next.getStartY(), next.getStopX(), next.getStopY() + bulletSpace, mPaint);
                    //如果子弹底部的位置Y坐标大于屏幕高度代表已经超出屏幕，移除掉
                    if (next.getStartY() >= gameHeight)
                        iterator.remove();
                    break;
            }
        }

//        for (BullectEntity next : bullects) {
//            switch (next.getDirect()) {
//                case UP:
//                    mCanvas.drawLine(next.getStartX(), next.getStartY() + bulletSpace, next.getStopX(), next.getStopY(), mPaint);
//                    break;
//                case LEFT:
//                    mCanvas.drawLine(next.getStartX() + bulletSpace, next.getStartY(), next.getStopX(), next.getStopY(), mPaint);
//                    break;
//                case RIGHT:
//                    mCanvas.drawLine(next.getStartX(), next.getStartY(), next.getStopX() + bulletSpace, next.getStopY(), mPaint);
//                    break;
//                case DOWN:
//                    mCanvas.drawLine(next.getStartX(), next.getStartY(), next.getStopX(), next.getStopY() + bulletSpace, mPaint);
//                    break;
//            }
//        }
    }


    //绘制鸟巢
    private void drawHome() {
        Path path = new Path();
        path.moveTo(gameWidth * 2 / 3, gameHeight);
        path.lineTo(gameWidth * 2 / 3, gameHeight - gameWidth / 3);
        path.lineTo(gameWidth / 3, gameHeight - gameWidth / 3);
        path.lineTo(gameWidth / 3, gameHeight);
        path.close();// 使这些点构成封闭的多边形
        //从屏幕中间绘制五角星
        mCanvas.drawPath(path, mPaint);
        Rect rect = new Rect(gameWidth / 3, gameHeight - gameWidth / 3, gameWidth * 2 / 3, gameHeight);
        mCanvas.drawBitmap(mHome, null, rect, mPaint);
    }

//
//    private void drawXX(){
//        Path path = new Path();
//        float radius = (gameWidth / 3) / 2;
//        float radian = degree2Radian(36);// 36为五角星的角度
//        float radius_in = (float) (radius * Math.sin(radian / 2) / Math
//                .cos(radian)); // 中间五边形的半径
//
//
//        float startX = (gameWidth / 3);
//        float startY = (gameHeight - gameWidth / 3);
//
//        path.moveTo((float) (radius * Math.cos(radian / 2)) + startX, startY);// 此点为多边形的起点
//        path.lineTo((float) (radius * Math.cos(radian / 2) + radius_in * Math.sin(radian)) + startX,
//                (float) (radius - radius * Math.sin(radian / 2)) + startY);
//
//        path.lineTo((float) (radius * Math.cos(radian / 2) * 2) + startX,
//                (float) (radius - radius * Math.sin(radian / 2)) + startY);
//
//        path.lineTo((float) (radius * Math.cos(radian / 2) + radius_in * Math.cos(radian / 2)) + startX,
//                (float) (radius + radius_in * Math.sin(radian / 2)) + startY);
//
//        path.lineTo((float) (radius * Math.cos(radian / 2) + radius * Math.sin(radian)) + startX
//                , (float) (radius + radius * Math.cos(radian)) + startY);
//
//        path.lineTo((float) (radius * Math.cos(radian / 2)) + startX,
//                (radius + radius_in) + startY);
//
//        path.lineTo((float) (radius * Math.cos(radian / 2) - radius * Math.sin(radian)) + startX,
//                (float) (radius + radius * Math.cos(radian)) + startY);
//
//        path.lineTo((float) (radius * Math.cos(radian / 2) - radius_in * Math.cos(radian / 2)) + startX,
//                (float) (radius + radius_in * Math.sin(radian / 2)) + startY);
//
//        path.lineTo(0 + startX, (float) (radius - radius * Math.sin(radian / 2)) + startY);
//
//        path.lineTo((float) (radius * Math.cos(radian / 2) - radius_in * Math.sin(radian)) + startX,
//                (float) (radius - radius * Math.sin(radian / 2)) + startY);
//
//        path.close();// 使这些点构成封闭的多边形
//        //从屏幕中间绘制五角星
//        mCanvas.drawPath(path, mPaint);
//    }

    /**
     * 角度转弧度公式
     *
     * @param degree
     * @return
     */
    private float degree2Radian(int degree) {
        return (float) (Math.PI * degree / 180);
    }

    //判断点是否在path内

    protected boolean pointInPath(Path path, int x, int y) {
        return pointInPath(path, new Point(x, y));
    }

    protected boolean pointInPath(Path path, Point point) {
        path.computeBounds(mCalculatePressBounds, true);
        mCalculatePressRegion.setPath(path, new Region((int) mCalculatePressBounds.left, (int) mCalculatePressBounds.top, (int) mCalculatePressBounds.right, (int) mCalculatePressBounds.bottom));
        return mCalculatePressRegion.contains(point.x, point.y);
    }

    protected boolean pointInPath(float left, float top, float right, float bottom, Point point) {
        mCalculatePressRegion.set((int) left, (int) top, (int) right, (int) bottom);
        return mCalculatePressRegion.contains(point.x, point.y);
    }

    protected int getColor(int color) {
        return ContextCompat.getColor(getContext(), color);
    }
}
