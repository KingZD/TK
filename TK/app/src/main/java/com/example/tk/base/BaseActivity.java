package com.example.tk.base;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.tk.R;
import com.example.tk.dialog.LoadingDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

//基类
public abstract class BaseActivity extends AppCompatActivity {
    Unbinder bind;
    private LoadingDialog mLoadDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        bind = ButterKnife.bind(this);
        initView(savedInstanceState);
    }

    protected abstract int getLayoutId();

    protected abstract void initView(@Nullable Bundle savedInstanceState);

    protected void showLoading() {
        showLoading("", null);
    }

    protected void showLoading(String content, DialogInterface.OnCancelListener cancelListener) {
        if (mLoadDialog == null) {
            mLoadDialog = new LoadingDialog(this, R.style.dialog, content);
        }
        mLoadDialog.show();
        mLoadDialog.setCanceledOnTouchOutside(false);
        if (cancelListener != null)
            mLoadDialog.setOnCancelListener(cancelListener);
    }

    protected void dismissLoading() {
        if (mLoadDialog != null && mLoadDialog.isShowing())
            mLoadDialog.dismiss();
    }

    private void destroyLoading() {
        dismissLoading();
        mLoadDialog = null;
    }

    @Override
    protected void onDestroy() {
        destroyLoading();
        bind.unbind();
        bind = null;
        super.onDestroy();
    }
}
