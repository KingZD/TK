package com.example.tk;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DirectButton extends AppCompatButton {
    Paint mPaint;
    float mWidth, mHeight, mStrokeWidth = 10;
    StateListDrawable stateListDrawable;
    int direct;
    Path mPath;
    int initColor = R.color.white;

    public DirectButton(Context context) {
        super(context);
        init(null);
    }

    public DirectButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DirectButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    void init(AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(ContextCompat.getColor(getContext(), initColor));
        mPaint.setStrokeWidth(mStrokeWidth);

        mPath = new Path();
        if (attrs == null) return;
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DirectButton);
        stateListDrawable = (StateListDrawable) typedArray.getDrawable(R.styleable.DirectButton_mBackground);
        direct = typedArray.getInt(R.styleable.DirectButton_direct, -1);
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (direct) {
            case 0://"UP"
                break;
            case 1://"LEFT"
                mPath.lineTo(mWidth * 2 / 3, 0);
                mPath.lineTo(mWidth, mHeight / 2);
                mPath.lineTo(mWidth * 2 / 3, mHeight);
                mPath.lineTo(0, mHeight);
                mPath.lineTo(0, 0);
                break;
            case 2://"RIGHT"
                break;
            case 3://"DOWN"
                break;
        }
        canvas.drawPath(mPath, mPaint);
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPaint.setColor(ContextCompat.getColor(getContext(), initColor));
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mPaint.setColor(ContextCompat.getColor(getContext(), initColor));
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }
}
