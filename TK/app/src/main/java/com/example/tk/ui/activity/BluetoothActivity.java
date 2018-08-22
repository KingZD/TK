package com.example.tk.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.tk.BluetoothService;
import com.example.tk.Constants;
import com.example.tk.R;
import com.example.tk.base.BaseActivity;
import com.example.tk.type.GameType;
import com.example.tk.util.LogUtils;
import com.example.tk.util.ToastUtils;

public class BluetoothActivity extends BaseActivity implements ServiceConnection {
    BluetoothService.BluetoothBinder binder;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bluetooth;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        if (mBluetoothAdapter == null)
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // 设备不支持蓝牙功能
            LogUtils.w("设备不支持蓝牙");
            ToastUtils.showShort("设备不支持蓝牙");
            finish();
            return;
        }
        //绑定service
        bindService(new Intent(this, BluetoothService.class), this, Context.BIND_AUTO_CREATE);
        //未启用蓝牙则开启
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
            // 不做提示，强行打开
            // mBluetoothAdapter.enable();
        } else {
            //蓝牙开启
            startActivity(new Intent(this, GameActivity.class));
        }
    }

    //创建蓝牙客户端
    private void createBluetoothClient(){
//        mBluetoothAdapter.listenUsingRfcommWithServiceRecord()
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            LogUtils.i("蓝牙打开成功");
            //开启成功，等待service连接成功
            if (binder == null) {
                onActivityResult(requestCode, resultCode, data);
                return;
            }
            startActivity(new Intent(this, GameActivity.class));
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder = (BluetoothService.BluetoothBinder) service;
        LogUtils.i("onServiceConnected");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    protected void onDestroy() {
        unbindService(this);
        super.onDestroy();
    }

    public static Intent createGameIntent(Activity activity) {
        Intent mIntent = new Intent(activity, BluetoothActivity.class);
        mIntent.putExtra(GameType.CREATE.name(), GameType.CREATE.name());
        return mIntent;
    }

    public static Intent joinGameIntent(Activity activity) {
        Intent mIntent = new Intent(activity, BluetoothActivity.class);
        mIntent.putExtra(GameType.JOIN.name(), GameType.JOIN.name());
        return mIntent;
    }
}
