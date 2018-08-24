package com.example.tk.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.example.tk.R;
import com.example.tk.util.DateUtil;
import com.example.tk.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 刻度尺
 */
public class RuleView extends View {
    private final static String TAG = "RuleView";
    private OnValueChangeListener mOnValueChangeListener;
    //画底线的画笔
    private Paint paint;
    //尺子控件总宽度
    private float viewWidth;
    //尺子控件总宽度
    private float viewHeight;
    //中间的标识图片
    private Bitmap cursorMap;
    //标签的位置
    private float cursorLocation;
    //未设置标识图片时默认绘制一条线作为标尺的线的颜色
    private int cursorColor = Color.GREEN;
    //刻度尺下面时间文字颜色 和底部色块区域
    private int timeColor = ContextCompat.getColor(getContext(), R.color.main_blue);
    //大刻度线宽，默认为3
    private int cursorWidth = 3;
    //小刻度线宽，默认为2
    private int scaleWidth = 2;
    //设置屏幕宽度内最多显示的大刻度数，默认为3个
    private int showItemSize = 4;
    //标尺开始位置
    private float currLocation = 0;
    //刻度表的最大值，默认为200
    private int maxValue = 5 * showItemSize;
    //一个刻度表示的值的大小
    private int oneItemValue = 1;
    //设置刻度线间宽度,大小由 showItemSize确定
    private int scaleDistance;
    //刻度高度，默认值为40
    private float scaleHeight = 20;
    //刻度的颜色刻度色，默认为灰色
    private int lineColor = Color.GRAY;
    //刻度文字的颜色，默认为灰色
    private int scaleTextColor = Color.GRAY;
    //刻度文字的大小,默认为14sp
    private int scaleTextSize = 14;
    //手势解析器
    private GestureDetector mGestureDetector;
    //处理惯性滚动
    private Scroller mScroller;
    //scroller 滚动的最大值
    private int maxX;
    //scroller 滚动的最小值
    private int minX;
    //刻度尺上时间文本的显示高度
    private int dateHeight;
    //刻度尺上时间文本的间距
    private int dateSplitHeight = 10;
    //不同时间段的间距
    private int dateSplitWidth = 500;
    private int mLastX, mMove;
    //有多少段时间刻度
    private int loopCount = 0;
    //当前指针所在位置
    private int currCursorIndex = 0;
    //默认指示器宽高
    int defWidthCursor = 40;
    int defHeightCursor = 80;
    //时间格式
    String fmt = "yyyy-MM-dd HH:mm:ss";
    //游标显示的日期
    private String mCursorDate;
    //时间段起始日期
    private String mDate;
    //时间段
    private long time;
    //所有绘制图案距离顶部的距离
    private float marginTopHeight = 10;
    //所有绘制图案距离底部的距离
    private float marginButtonHeight = 0;
    //底部蓝色区域和时间之间的间隔
    private float buttonBlueSplit = 0;
    private float mCurrentY = 0, mCurrentX = 0;

    //时间段数据
//    private List<RuleBean> dates;
    private List<RuleBean> dates = new ArrayList<RuleBean>() {{
        add(new RuleBean("2018-05-08 14:13:19", 4 * 60 * 60 * 1000));
        add(new RuleBean("2018-06-08 19:43:44", 2 * 60 * 60 * 1000));
        add(new RuleBean("2018-07-04 08:33:59", 6 * 60 * 60 * 1000));
        add(new RuleBean("2018-07-04 08:33:59", 6 * 60 * 60 * 1000));
        add(new RuleBean("2018-07-04 08:33:59", 6 * 60 * 60 * 1000));
        add(new RuleBean("2018-07-04 08:33:59", 6 * 60 * 60 * 1000));
        add(new RuleBean("2018-07-04 08:33:59", 6 * 60 * 60 * 1000));
        add(new RuleBean("2018-07-04 08:33:59", 6 * 60 * 60 * 1000));
        add(new RuleBean("2018-07-04 08:33:59", 6 * 60 * 60 * 1000));
        add(new RuleBean("2018-07-04 08:33:59", 6 * 60 * 60 * 1000));
        add(new RuleBean("2018-07-04 08:33:59", 6 * 60 * 60 * 1000));
        add(new RuleBean("2018-07-04 08:33:59", 6 * 60 * 60 * 1000));
        add(new RuleBean("2018-07-04 08:33:59", 6 * 60 * 60 * 1000));
        add(new RuleBean("2018-07-04 08:33:59", 6 * 60 * 60 * 1000));
        add(new RuleBean("2018-07-04 08:33:59", 6 * 60 * 60 * 1000));
        add(new RuleBean("2018-07-04 08:33:59", 6 * 60 * 60 * 1000));
    }};
    //
    private int RULE_SCROLL = 0;
    private int RULE_IDLE = 1;
    private long touchTime = 0;
    //记录刻度尺下部时间大小
    private Rect boundsY, boundsD, boundsALL;

    public RuleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RuleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LoopScaleView);
        scaleTextColor = ta.getColor(R.styleable.LoopScaleView_scaleTextColor, scaleTextColor);
        cursorColor = ta.getColor(R.styleable.LoopScaleView_cursorColor, ContextCompat.getColor(getContext(), android.R.color.holo_green_dark));
        int cursorMapId = ta.getResourceId(R.styleable.LoopScaleView_cursorMap, -1);
        if (cursorMapId != -1) {
            cursorMap = BitmapFactory.decodeResource(getResources(), cursorMapId);
        }
        ta.recycle();
        scaleTextSize = sp2px(scaleTextSize);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mScroller = new Scroller(context);
        mGestureDetector = new GestureDetector(context, gestureListener);
        //计算文字所占提交的耗时操作初始化的时候计算一次
        dateHeight = getTextRect("0").height();
        //刻度尺上年月日宽度
        boundsY = getTextRect("0000-00-00");
        //刻度尺上时分秒宽度
        boundsD = getTextRect("00:00:00");
        //刻度尺上年月日时分秒宽度
        boundsALL = getTextRect("0000-00-00 00:00:00");
        mCursorDate = DateUtil.getCurrentDate();
    }

    /**
     * 得到需要绘制文本的范围
     *
     * @param word
     * @return
     */
    Rect getTextRect(String word) {
        Rect bounds = new Rect();
        paint.setColor(scaleTextColor);
        paint.setTextSize(scaleTextSize);
        paint.setStrokeWidth(scaleWidth);
        paint.getTextBounds(word, 0, word.length(), bounds);
        return bounds;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        //一个小刻度的宽度（十进制，每5个小刻度为一个大刻度）
        scaleDistance = getMeasuredWidth() / (showItemSize * 5);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        //一个小刻度的宽度（十进制，每5个小刻度为一个大刻度）
        scaleDistance = 54;
        //尺子长度总的个数*一个的宽度
        viewWidth = getItemsCount() * loopCount * scaleDistance + (loopCount - 1) * dateSplitWidth;
        //往右滑动的最大距离限制
        maxX = 0;
        //往左滑动的最大距离限制
        minX = -getItemsCount() * loopCount * scaleDistance - (loopCount - 1) * dateSplitWidth;
        loopCount = dates == null ? 0 : dates.size();
        canvas.clipRect(getPaddingStart(), getPaddingTop(), getWidth() - getPaddingRight(), viewHeight - getPaddingBottom());
        cursorLocation = showItemSize / 2 * 5 * scaleDistance; //屏幕显示Item 数的中间位置
//        drawCursorTime(canvas);
        for (int k = 0; k < loopCount; k++) {
            String mDate = dates.get(k).getDate();
            long time = dates.get(k).getTime();
            for (int i = 0; i <= maxValue; i++) {
                int index = k * maxValue + i;
                //起始坐标x
                float location = cursorLocation + currLocation + index * scaleDistance;
                //时间段之间的间距
                if (k > 0)
                    location += k * dateSplitWidth;
                drawScale(canvas, mDate, time, i, location);
            }
            drawLine(canvas);
            drawCursor(canvas);
//            drawBlueRect(canvas, k);
        }
    }

    /**
     * 绘制主线
     *
     * @param canvas 绘制的画布
     */
    private void drawLine(Canvas canvas) {
        paint.setStrokeWidth(3);
        paint.setColor(lineColor);
        canvas.drawLine(getPaddingStart(), mCurrentY, getMeasuredWidth() - getPaddingEnd(), mCurrentY, paint);
    }

    /**
     * 绘制指示标签
     *
     * @param canvas 绘制控件的画布
     */
    private void drawCursor(Canvas canvas) {
        if (cursorMap == null) { //绘制一条红色的竖线线
            paint.setStrokeWidth(cursorWidth);
            paint.setColor(lineColor);
            //三角形指示器
//            Path path = new Path();
//            path.moveTo(cursorLocation + defWidthCursor / 2, viewHeight - getPaddingTop() - (dateHeight + dateSplitHeight) * 2 - getPaddingBottom() - defHeightCursor - marginButtonHeight);
//            path.lineTo(cursorLocation - defWidthCursor / 2, viewHeight - getPaddingTop() - (dateHeight + dateSplitHeight) * 2 - getPaddingBottom() - defHeightCursor - marginButtonHeight);
//            path.lineTo(cursorLocation, viewHeight - (dateHeight + dateSplitHeight) * 2 - getPaddingBottom() - marginButtonHeight);
//            path.close();
//            canvas.drawPath(path, paint);
            mCurrentY += defHeightCursor;
            canvas.drawLine(cursorLocation, mCurrentY - defHeightCursor, cursorLocation, mCurrentY, paint);
        } else { //绘制标识图片
            canvas.drawBitmap(cursorMap, cursorLocation - cursorMap.getWidth() / 2, viewHeight - cursorMap.getHeight() - (dateHeight + dateSplitHeight) * 2 - getPaddingBottom() - marginButtonHeight, paint);
        }
    }

    /**
     * 绘制标签上面的时间
     *
     * @param canvas
     */
    private void drawCursorTime(Canvas canvas) {
        if (TextUtils.isEmpty(mCursorDate)) return;
        paint.setColor(cursorColor);
        paint.setTextSize(scaleTextSize);
        if (cursorMap == null) { //绘制一条红色的竖线线
            float startX = viewHeight - getPaddingTop() - (dateHeight + dateSplitHeight) * 2 - getPaddingBottom() - defHeightCursor;
            canvas.drawText(mCursorDate, cursorLocation - boundsALL.width() / 2, startX - boundsALL.height() / 2 - marginButtonHeight, paint);
        } else {
            canvas.drawText(mCursorDate, cursorLocation - boundsALL.width() / 2, viewHeight - cursorMap.getHeight() - dateHeight * 2 - getPaddingBottom() - 3 * dateSplitHeight - marginButtonHeight, paint);
        }
    }

    /**
     * 绘制刻度线
     *
     * @param canvas 画布
     * @param index  刻度值
     */

    private void drawScale(Canvas canvas, String mDate, long time, int index, float location) {
        paint.setTextSize(scaleTextSize);
        paint.setStrokeWidth(0);
        paint.setColor(lineColor);
        if (index % 5 == 0) {
            //每个格子代表多少毫秒
            long second = time / (maxValue);
            String date = DateUtil.stringAddTime(mDate, index * second, fmt);
            String[] dStr = date.split(" ");
            String drawStrY = dStr[0];
            String drawStrD = dStr[1];
            mCurrentY = getPaddingTop() + boundsY.height() + marginTopHeight;
            canvas.drawText(drawStrY, location - boundsY.width() / 2, mCurrentY, paint);
            mCurrentY += boundsY.height() + dateSplitHeight;
            canvas.drawText(drawStrD, location - boundsD.width() / 2, mCurrentY, paint);
            mCurrentY += scaleHeight * 2;
            canvas.drawLine(location, mCurrentY - scaleHeight * 2, location, mCurrentY, paint);
        } else {
            canvas.drawLine(location, mCurrentY - scaleHeight, location, mCurrentY, paint);
        }
    }

    //绘制蓝色区域
    private void drawBlueRect(Canvas canvas, int k) {
        float startX = cursorLocation + k * (maxValue * scaleDistance + dateSplitWidth) + currLocation;
        float endX = startX + maxValue * scaleDistance;
        paint.setStrokeWidth(marginButtonHeight - buttonBlueSplit);
        paint.setColor(timeColor);
        Path path = new Path();
        path.moveTo(startX, viewHeight - marginButtonHeight + buttonBlueSplit);
        path.lineTo(endX, viewHeight - marginButtonHeight + buttonBlueSplit);
        path.lineTo(endX, viewHeight);
        path.lineTo(startX, viewHeight);
        path.lineTo(startX, viewHeight - marginButtonHeight + buttonBlueSplit);
        canvas.drawPath(path, paint);
    }

    // 拦截屏幕滑动事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (dates == null) return super.onTouchEvent(event);
        if (dates.size() <= 0) return super.onTouchEvent(event);
        touchTime = System.currentTimeMillis();
        int xPosition = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);
                mLastX = xPosition;
                mMove = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                //这里不需要累加
                //mMove += (mLastX - xPosition);
                mMove = (xPosition - mLastX);
                /**如果放在
                 * {@link gestureListener#onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)}
                 * 方法里面调用会导致 手指触摸屏幕时间稍长或者滑动缓慢的情况下 不会触发该方法
                 */
                scrollView(mMove);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //控制滑动超过距离后返回
                if (currLocation <= -(scaleDistance * maxValue * loopCount + (loopCount - 1) * dateSplitWidth)
                        || currLocation > 0) {
                    float speed = mScroller.getCurrVelocity();
                    mScroller.fling((int) currLocation, 0, (int) speed, 0, minX, maxX, 0, 0);
                    setNextMessage(RULE_SCROLL);
                } else {
                    if (mScroller.isFinished())
                        animationHandler.sendEmptyMessageDelayed(RULE_IDLE, 1000);
                }
                break;
            default:
                break;
        }
        mLastX = xPosition;
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    /**
     * 滑动手势处理
     */
    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }


        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            /**
             * 将逻辑提取到
             * {@link Rule2#onTouchEvent(MotionEvent event)}
             * MotionEvent.ACTION_MOVE 事件下，避免手势不响应
             */
            // scrollView(distanceX);
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!mScroller.computeScrollOffset()) {
                touchTime = System.currentTimeMillis();
                mScroller.fling((int) currLocation, 0, (int) (velocityX / 1.5), 0, minX, maxX, 0, 0);
                setNextMessage(RULE_SCROLL);
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }
    };

    /**
     * 滑动View
     *
     * @param distance 滑动的距离
     */
    private void scrollView(float distance) {
        currLocation += distance;
        //设置新的位置
        setCurrLocation(currLocation);
    }

    /**
     * 获取一共有多少个刻度
     *
     * @return 总刻度数
     */
    public int getItemsCount() {
        return maxValue / oneItemValue;
    }

    /**
     * 设置标识的颜色
     *
     * @param color 颜色id
     */
    public void setCursorColor(int color) {
        this.cursorColor = color;
        postInvalidate();
    }

    /**
     * 设置标识的宽度
     *
     * @param width 宽度
     */
    public void setCursorWidth(int width) {
        this.cursorWidth = width;
        postInvalidate();
    }

    /**
     * 设置游标的bitmap位图
     *
     * @param cursorMap 位图
     */
    public void setCursorMap(Bitmap cursorMap) {
        this.cursorMap = cursorMap;
        postInvalidate();
    }

    /**
     * 设置刻度线的宽度
     *
     * @param scaleWidth 刻度线的宽度
     */
    public void setScaleWidth(int scaleWidth) {
        this.scaleWidth = scaleWidth;
        postInvalidate();
    }

    /**
     * 设置屏幕宽度内大Item的数量
     *
     * @param showItemSize 屏幕宽度内显示的大 item数量
     */
    public void setShowItemSize(int showItemSize) {
        this.showItemSize = showItemSize;
        postInvalidate();
    }

    /**
     * 设置当前游标所在的值
     *
     * @param currLocation 当前游标所在的值
     */
    private void setCurrLocation(float currLocation) {
        this.currLocation = currLocation;
        boolean validateTimes = validateTimes();
        //不在时间段则移除回调
        if (!validateTimes)
            removeMesage();
        postInvalidate();
    }

    //验证当前指针是否在有效的时间段里面
    private boolean validateTimes() {
        boolean validate = false;
        long second;
        long px;
        //尺子总长度
        long ruleWidth = scaleDistance * maxValue * loopCount + (loopCount - 1) * dateSplitWidth;
        //开始区分分组时间刻度
        for (int k = 0; k < loopCount; k++) {
            float mCl = Math.abs(currLocation);
            float end = maxValue * (k + 1) * scaleDistance + k * dateSplitWidth;
            float start = maxValue * k * scaleDistance + k * dateSplitWidth;
            //判断 currLocation 在哪个时间段
            if (mCl >= start && mCl <= end) {
                second = dates.get(k).getTime() / (maxValue * scaleDistance);
                mDate = dates.get(k).getDate();
                px = (long) (mCl - start);
                currCursorIndex = k;
                mCursorDate = DateUtil.stringAddTime(mDate, px * second, fmt);
                validate = true;
            }
        }
        return validate;
    }

    /**
     * 设置刻度线的高度
     *
     * @param scaleHeight 刻度线的高度
     */
    public void setScaleHeight(float scaleHeight) {
        this.scaleHeight = scaleHeight;
        postInvalidate();
    }

    /**
     * 设置底部线条的颜色
     *
     * @param lineColor 底部线条的颜色值
     */
    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    /**
     * 设置刻度表上文字的颜色
     *
     * @param scaleTextColor 文字颜色id
     */
    public void setScaleTextColor(int scaleTextColor) {
        this.scaleTextColor = scaleTextColor;
        postInvalidate();
    }

    /**
     * 设置刻度标上的文字的大小
     *
     * @param scaleTextSize 文字大小
     */
    public void setScaleTextSize(int scaleTextSize) {
        this.scaleTextSize = scaleTextSize;
        postInvalidate();
    }

    /**
     * 设置刻度的最大值
     *
     * @param maxValue 刻度的最大值
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        postInvalidate();
    }

    /**
     * 设置 一刻度所代表的的值的大小
     *
     * @param oneItemValue 一个刻度代表的值的大小
     */
    public void setOneItemValue(int oneItemValue) {
        this.oneItemValue = oneItemValue;
        postInvalidate();
    }

    /**
     * 设置当前刻度的位置
     *
     * @param currValue 当前刻度位置，小于0时取0 大于最大值时取最大值
     */
    public void setCurrentValue(int currValue) {
        if (currLocation < 0) {
            currLocation = 0;
        } else if (currLocation > maxValue) {
            currLocation = maxValue;
        }
        this.currLocation = currValue;
        postInvalidate();
    }


    private void setNextMessage(int message) {
        animationHandler.removeMessages(RULE_IDLE);
        animationHandler.removeMessages(message);
        animationHandler.sendEmptyMessage(message);
    }

    private void removeMesage() {
        animationHandler.removeMessages(RULE_IDLE);
        animationHandler.removeMessages(RULE_SCROLL);
    }

    // 动画处理
    @SuppressLint("HandlerLeak")
    private Handler animationHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == RULE_SCROLL) {
                mScroller.computeScrollOffset();
                int currX = mScroller.getCurrX();
                float delta = currX - currLocation;
                if (delta != 0) {
                    scrollView(delta);
                }
                // 滚动还没有完成
                if (!mScroller.isFinished()) {
                    animationHandler.sendEmptyMessage(msg.what);
                } else {
                    animationHandler.sendEmptyMessageDelayed(RULE_IDLE, 1000);
                }
            } else if (msg.what == RULE_IDLE) {
                if (System.currentTimeMillis() - touchTime >= 1000) {
                    LogUtils.iTag(TAG, "Touch !!!! delayed 1s", mCursorDate);
                    if (mOnValueChangeListener != null) {
                        boolean validateTimes = validateTimes();
                        LogUtils.iTag(TAG, "validateTimes ", validateTimes);
                        if (validateTimes) {
                            mOnValueChangeListener.OnRuleValueChange(dates.get(currCursorIndex), mCursorDate, currCursorIndex);
                        } else {
                            mOnValueChangeListener.OnRuleInvalidTime();
                        }
                    }
                }
            }
        }
    };

    /**
     * 根据播放器播放进度进行滚动
     *
     * @param ss = 秒 * 倍速
     */
    public void scrollByTime(float ss) {
        if (dates == null) return;
        //1px对应多少毫秒
//        float seconds = dates.get(currCursorIndex).getTime() / (maxValue * scaleDistance);
//        float px = ss * 1000 / seconds;
//        currLocation -= px;
//        mCursorDate = DateUtil.stringAddTime(mDate, (long) (ss * 1000), fmt);
//        LogUtils.d(TAG, currLocation, ss, seconds, px, (long) (ss * 1000), mCursorDate);
//        postInvalidate();

        //1秒对应多少px
//        float second = (maxValue * scaleDistance * 1000) / dates.get(currCursorIndex).getTime();
//        currLocation -= ss * second;
//        LogUtils.d(TAG, currLocation, (maxValue * scaleDistance) * 1000, dates.get(currCursorIndex).getTime(), second, ss);
//        postInvalidate();
    }

    public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener) {
        mOnValueChangeListener = onValueChangeListener;
    }

    public interface OnValueChangeListener {
        //有效的时间段
        void OnRuleValueChange(RuleBean ruleBean, String date, int currCursorIndex);

        //当前未在有效的时间段类
        void OnRuleInvalidTime();
    }

    public void setDates(List<RuleBean> dates) {
        currLocation = 0;
        currCursorIndex = 0;
        this.dates = dates;
        if (dates != null && dates.get(0) != null)
            mDate = dates.get(0).getDate();
        if (TextUtils.isEmpty(mDate))
            mDate = DateUtil.getCurrentDate();
        mCursorDate = mDate;
        invalidate();
    }

    //获取当前时间段的起始时间
    public RuleBean getCurrStartTime() {
        if (dates == null) return null;
        if (dates.size() <= 0) return null;
        return dates.get(currCursorIndex);
    }

    public String getCursorDate() {
        return mCursorDate;
    }

    public int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
