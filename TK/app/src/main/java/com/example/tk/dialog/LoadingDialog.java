package com.example.tk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tk.R;

/**
 * 作者：
 * 邮箱：
 * 说明:用于加载的dialog
 */
public class LoadingDialog extends Dialog {

    private ImageView imageView;
    private TextView textView;
    private AnimationDrawable animationDrawable;
    private final int IS_BUFFER = 101;

    private String mTitle;

    public LoadingDialog(Context context, int dialog) {
        super(context, dialog);
    }

    public LoadingDialog(Context context, int dialog, String title) {
        super(context, dialog);
        this.mTitle = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_load);
//        Window window = getWindow();
//        window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.width = (int) (window.getWindowManager().getDefaultDisplay().getWidth() * 0.82);//设置宽度，填充屏幕  废弃
//        params.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.82);
//        window.setAttributes(params);
        initView();
        initData();
        setCanceledOnTouchOutside(false);
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.iv_loading);
        textView = (TextView) findViewById(R.id.tv_loading);
    }

    private void initData() {
        try {
            if (mTitle != null && !mTitle.equals("")) {
                textView.setText(mTitle);
            } else {
                textView.setText(R.string.loading);
            }
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.progress_white_animlist);
            animationDrawable = (AnimationDrawable) imageView.getDrawable();
            animationDrawable.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
