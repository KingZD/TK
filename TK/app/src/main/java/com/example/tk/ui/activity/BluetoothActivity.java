package com.example.tk.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import com.example.tk.entity.BluetoothDeviceInfo;
import com.example.tk.listener.BlueToothConnectCallback;
import com.example.tk.type.GameType;
import com.example.tk.util.BluetoothUtil;
import com.example.tk.util.LogUtils;
import com.example.tk.util.ToastUtils;

import java.io.IOException;
import java.util.List;

public class BluetoothActivity extends BaseActivity implements ServiceConnection {
    BluetoothService.BluetoothBinder binder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bluetooth;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        BluetoothAdapter bluetoothAdapter = BluetoothUtil.getInstance().openBluetooth(this);
        if (bluetoothAdapter == null) {
            // 设备不支持蓝牙功能
            LogUtils.w("设备不支持蓝牙");
            ToastUtils.showShort("设备不支持蓝牙");
            finish();
            return;
        }
        //绑定service
        bindService(new Intent(this, BluetoothService.class), this, Context.BIND_AUTO_CREATE);
        if (bluetoothAdapter.isEnabled()) {
            //蓝牙开启
            startActivity(new Intent(this, GameActivity.class));
        }
    }


    //////////////////////////////////////////////////////////创建者/////////////////////////////////////////////////////////////////////////////////
    //创建蓝牙客户端
    private void createBluetoothClient() {
//        mBluetoothAdapter.listenUsingRfcommWithServiceRecord()
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||//
//////////////////////////////////////////////////////////加入者/////////////////////////////////////////////////////////////////////////////////

    //寻找已经创建的游戏
    private void findExistsGame() {
        BluetoothUtil.getInstance().setOnFoundUnBondDeviceListener(new BluetoothUtil.OnFoundUnBondDeviceListener() {
            @Override
            public void foundUnBondDevice(BluetoothDevice unBondDevice) {
                LogUtils.d(unBondDevice.getName(), unBondDevice.getAddress());
            }
        });
    }

    //连接一个存在的房间
    private void connectGame(String macAddress) {
        showLoading("正在连接..");
        macAddress = macAddress.split("\\|")[1];
        BluetoothUtil.getInstance().connectRemoteDevice(macAddress, new BlueToothConnectCallback() {
            @Override
            public void connecting(String serverBlueToothAddress) {

            }

            @Override
            public void connectSuccess(String serverBlueToothAddress) {
                dismissLoading();
                ToastUtils.showShort("连接成功");
            }

            @Override
            public void connectFailure(Exception e) {
                dismissLoading();
                ToastUtils.showShort("连接失败..");
            }
        });
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    protected void onResume() {
        super.onResume();
        List<BluetoothDeviceInfo> bluetoothDeviceInfoList = BluetoothUtil.getInstance().scanDevice();
        for (BluetoothDeviceInfo bluetoothDeviceInfo : bluetoothDeviceInfoList) {
            LogUtils.d(bluetoothDeviceInfo.toString());
        }
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
        BluetoothUtil.getInstance().unregisterReceiver(this);
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
