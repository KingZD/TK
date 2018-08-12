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
    @BindView(R.id.btDirect)
    DirectButton btDirect;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        btDirect.setListener(new DirectButton.OnDirectListener() {
            @Override
            public void direct(TKDirect direct) {
                tkView.changePlayer1Point(direct);
            }
        });
    }

    public void start(View view) {
        TkModel model = new TkModel(TKDirect.RIGHT, 5, 160, 240, 0.2f, 0, 0, 5, R.color.colorAccent, TkModel.Player.PLAYER1);
        tkView.createTkAndStart(model);
    }
}
