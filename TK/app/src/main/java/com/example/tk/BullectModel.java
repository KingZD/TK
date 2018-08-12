package com.example.tk;

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
}
