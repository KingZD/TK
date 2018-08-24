package com.example.tk.entity;

import android.graphics.Rect;

import com.example.tk.util.LogUtils;
import com.example.tk.type.TKDirect;

/**
 * Created by zed on 2018/8/12.
 */
public class BullectEntity {
    private TKDirect direct;
    private float startX;
    private float startY;
    private float stopX;
    private float stopY;

    public BullectEntity(TKDirect direct, float startX, float startY, float stopX, float stopY) {
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

    public Rect getBullectRect(int btHeight, int btWidth) {
        Rect rect = new Rect();
        switch (direct) {
            case UP:
            case DOWN:
                rect.set((int) (startX), (int) (startY + btHeight), (int) (stopX + btWidth), (int) (stopY));
                break;
            case RIGHT:
            case LEFT:
                rect.set((int) (startX + btHeight), (int) (startY), (int) (stopX), (int) (stopY + btWidth));
                break;
        }
        LogUtils.i(startX, startY, stopX, stopY, rect);
        return rect;
    }
}
