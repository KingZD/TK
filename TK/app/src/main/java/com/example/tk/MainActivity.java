package com.example.tk;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    @BindView(R.id.tkView)
    TkView tkView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
    }

    public void start(View view) {
        TkModel model = new TkModel(TkModel.TKDirect.TOP, 5, 160, 240, 0.4f, 100, 100, 5, R.color.colorAccent, TkModel.Player.ONE);
        tkView.createTkAndStart(model);
    }
}
