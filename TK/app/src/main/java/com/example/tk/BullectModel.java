package com.example.tk;

import android.graphics.Rect;

/**
 * Created by zed on 2018/8/12.
 */
public class BullectModel {
    private TKDirect direct;
    private float startX;
    private float startY;
    private float stopX;
    private float stopY;

    public BullectModel(TKDirect direct, float startX, float startY, float stopX, float stopY) {
        this.direct = direct;
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;
    }

    public TKDirect getDirect() {
        return direct;
    }

    public void setDirect(TKDirect direct) {
        this.direct = direct;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getStopX() {
        return stopX;
    }

    public void setStopX(float stopX) {
        this.stopX = stopX;
    }

    public float getStopY() {
        return stopY;
    }

    public void setStopY(float stopY) {
        this.stopY = stopY;
    }

    public Rect getBullectRect(int gameHeight, int gameWidth, int btHeight, int btWidth) {
        Rect rect = new Rect();
        switch (direct) {
            case UP:
            case DOWN:
                rect.set((int) (startX), (int) (gameHeight - startY), (int) (stopX + btWidth), (int) (gameHeight - stopY - btHeight));
                break;
            case RIGHT:
            case LEFT:
                rect.set((int) (gameWidth - startX), (int) (startY), (int) (gameWidth - stopX - btHeight), (int) (stopY + btWidth));
                break;
        }
        LogUtils.i(startX, startY, stopX, stopY, rect);
        return rect;
    }
}
