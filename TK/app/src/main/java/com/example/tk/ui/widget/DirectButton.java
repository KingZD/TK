package com.example.tk.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.tk.R;
import com.example.tk.type.TKDirect;

//自定义方向键盘
public class DirectButton extends AppCompatButton {
    Paint mPaint;
    float mWidth, mHeight, mStrokeWidth = 10;
    Path mUpPath, mLeftPath, mRightPath, mDownPath;
    int initColor = R.color.white;
    @ColorInt
    int mPressColor = -1;
    @ColorInt
    int mNormalColor = -1;
    int space = 10;
    TKDirect direct = TKDirect.ALL;
    RectF mCalculatePressBounds;
    Region mCalculatePressRegion;
    private final int SEND_DOWN = 0;
    private final int SEND_PRESS = 0;
    private final int SEND_UP = 0;
    private int moveAction = MotionEvent.ACTION_DOWN;

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
        setBackgroundColor(Color.parseColor("#00ffffff"));
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(ContextCompat.getColor(getContext(), initColor));
        mPaint.setStrokeWidth(mStrokeWidth);
        mCalculatePressBounds = new RectF();
        mCalculatePressRegion = new Region();

        mUpPath = new Path();
        mLeftPath = new Path();
        mRightPath = new Path();
        mDownPath = new Path();
        if (attrs == null) return;
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DirectButton);
        mPressColor = typedArray.getColor(R.styleable.DirectButton_mPressColor, mPressColor);
        mNormalColor = typedArray.getColor(R.styleable.DirectButton_mNormalColor, mNormalColor);
        mPaint.setColor(mNormalColor);
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
        mPaint.setColor(mNormalColor);
        float centerY = mHeight / 2;
        float centerX = mWidth / 2;
        //"UP"
        mUpPath.reset();
        mUpPath.moveTo(centerX, centerY);
        mUpPath.lineTo(centerX / 2 + space, centerY / 2);
        mUpPath.lineTo(centerX / 2 + space, 0);
        mUpPath.lineTo(centerX * 3 / 2 - space, 0);
        mUpPath.lineTo(centerX * 3 / 2 - space, centerY / 2);
        //"LEFT"
        mLeftPath.reset();
        mLeftPath.moveTo(centerX, centerY);
        mLeftPath.lineTo(centerX / 2, centerY / 2 + space);
        mLeftPath.lineTo(0, centerY / 2 + space);
        mLeftPath.lineTo(0, centerY * 3 / 2 - space);
        mLeftPath.lineTo(centerX / 2, centerY * 3 / 2 - space);

        //"RIGHT"
        mRightPath.reset();
        mRightPath.moveTo(centerX, centerY);
        mRightPath.lineTo(centerX * 3 / 2, centerY / 2 + space);
        mRightPath.lineTo(centerX * 2, centerY / 2 + space);
        mRightPath.lineTo(centerX * 2, centerY * 3 / 2 - space);
        mRightPath.lineTo(centerX * 3 / 2, centerY * 3 / 2 - space);

        //"DOWN"
        mDownPath.reset();
        mDownPath.moveTo(centerX, centerY);
        mDownPath.lineTo(centerX / 2 + space, centerY * 3 / 2);
        mDownPath.lineTo(centerX / 2 + space, centerY * 2);
        mDownPath.lineTo(centerX * 3 / 2 - space, centerY * 2);
        mDownPath.lineTo(centerX * 3 / 2 - space, centerY * 3 / 2);
        canvas.drawPath(mUpPath, mPaint);
        canvas.drawPath(mLeftPath, mPaint);
        canvas.drawPath(mRightPath, mPaint);
        canvas.drawPath(mDownPath, mPaint);
        mPaint.setColor(mPressColor);
        switch (direct) {
            case UP:
                canvas.drawPath(mUpPath, mPaint);
                break;
            case LEFT:
                canvas.drawPath(mLeftPath, mPaint);
                break;
            case RIGHT:
                canvas.drawPath(mRightPath, mPaint);
                break;
            case DOWN:
                canvas.drawPath(mDownPath, mPaint);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        moveAction = event.getAction();
        switch (moveAction) {
            case MotionEvent.ACTION_DOWN:
                boolean pressBottomDirect = getPressBottomDirect(new Point((int) event.getX(), (int) event.getY()));
                //点击到有效的方向区域才执行 回调事件 表示往哪個方向
                if (pressBottomDirect) {
                    if (listener != null)
                        listener.direct(direct);
                    handler.sendEmptyMessageDelayed(SEND_PRESS, 500);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                direct = TKDirect.ALL;
                invalidate();
                handler.removeMessages(SEND_PRESS);
                break;
        }
        return super.onTouchEvent(event);
    }

    //当前按下去的区域方向
    private boolean getPressBottomDirect(Point point) {
        if (pointInPath(mUpPath, point)) {
            direct = TKDirect.UP;
            return true;
        }
        if (pointInPath(mDownPath, point)) {
            direct = TKDirect.DOWN;
            return true;
        }
        if (pointInPath(mLeftPath, point)) {
            direct = TKDirect.LEFT;
            return true;
        }
        if (pointInPath(mRightPath, point)) {
            direct = TKDirect.RIGHT;
            return true;
        }
        direct = TKDirect.ALL;
        return false;
    }


    private boolean pointInPath(Path path, Point point) {
        path.computeBounds(mCalculatePressBounds, true);
        mCalculatePressRegion.setPath(path, new Region((int) mCalculatePressBounds.left, (int) mCalculatePressBounds.top, (int) mCalculatePressBounds.right, (int) mCalculatePressBounds.bottom));
        return mCalculatePressRegion.contains(point.x, point.y);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_PRESS:
                    if (moveAction == MotionEvent.ACTION_DOWN || moveAction == MotionEvent.ACTION_MOVE) {
                        handler.sendEmptyMessageDelayed(SEND_PRESS, 20);
                        if (listener != null)
                            listener.direct(direct);
                    } else {
                        handler.removeMessages(SEND_PRESS);
                    }
                    break;
            }
        }
    };

    public interface OnDirectListener {
        void direct(TKDirect direct);
    }

    private OnDirectListener listener;

    public void setListener(OnDirectListener listener) {
        this.listener = listener;
    }
}
