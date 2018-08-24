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
import android.widget.TextView;

import com.example.tk.BluetoothService;
import com.example.tk.Constants;
import com.example.tk.R;
import com.example.tk.base.BaseActivity;
import com.example.tk.entity.BluetoothDeviceInfo;
import com.example.tk.entity.TkEntity;
import com.example.tk.listener.BlueToothConnectCallback;
import com.example.tk.listener.GameDataChangeListener;
import com.example.tk.listener.ReceivedMessageListener;
import com.example.tk.type.GameType;
import com.example.tk.type.TKDirect;
import com.example.tk.util.BluetoothUtil;
import com.example.tk.util.LogUtils;
import com.example.tk.util.ToastUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;

public class BluetoothActivity extends BaseActivity {
    GameType gameType;
    @BindView(R.id.tvStatus)
    TextView tvStatus;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bluetooth;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        LogUtils.d("初始化");
        tvStatus.setText("初始化");
        gameType = (GameType) getIntent().getSerializableExtra(GameType.FLAG);
        BluetoothAdapter bluetoothAdapter = BluetoothUtil.getInstance()
                .openBluetooth(this, gameType == GameType.CREATE);
        if (bluetoothAdapter == null) {
            // 设备不支持蓝牙功能
            LogUtils.w("设备不支持蓝牙");
            ToastUtils.showShort("设备不支持蓝牙");
            finish();
            return;
        }

        if (bluetoothAdapter.isEnabled()) {
            switch (gameType) {
                case JOIN:
                    findExistsGame();
                    break;
                case CREATE:
                    createBluetoothClient();
                    break;
            }
        }
    }


    //////////////////////////////////////////////////////////创建者/////////////////////////////////////////////////////////////////////////////////
    //创建蓝牙客户端
    private void createBluetoothClient() {
        LogUtils.d("createBluetoothClient");
        showLoading();
        BluetoothUtil.getInstance().createBluetoothServer(new ReceivedMessageListener() {

            @Override
            public void onConnectedSuccess() {
                dismissLoading();
                ToastUtils.showShort("玩家2连接成功");
                startActivityForResult(GameActivity.getPlayer1Intent(BluetoothActivity.this), Constants.REQUEST_CLOSE_GAME);
            }

            @Override
            public void onReceiveMessage(String content) {
                LogUtils.d(content);
            }

            @Override
            public void onConnectionInterrupt(Exception e) {
                LogUtils.e(e.getLocalizedMessage());
            }
        });
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||//
//////////////////////////////////////////////////////////加入者/////////////////////////////////////////////////////////////////////////////////

    //寻找已经创建的游戏
    private void findExistsGame() {
        LogUtils.d("findExistsGame");
        connectGame("A8:0C:63:E6:0E:D6");
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
        boolean contains = macAddress.contains("|");
        if (contains)
            macAddress = macAddress.split("\\|")[1];
        BluetoothUtil.getInstance().connectRemoteDevice(macAddress, new BlueToothConnectCallback() {
            @Override
            public void connecting(String serverBlueToothAddress) {
                dismissLoading();
                ToastUtils.showShort("连接游戏成功");
                startActivityForResult(GameActivity.getPlayer2Intent(BluetoothActivity.this), Constants.REQUEST_CLOSE_GAME);
            }

            @Override
            public void connectSuccess(String serverBlueToothAddress) {
            }

            @Override
            public void connectFailure(Exception e) {
                dismissLoading();
                ToastUtils.showShort("连接失败..");
            }
        }, new ReceivedMessageListener() {
            @Override
            public void onConnectedSuccess() {

            }

            @Override
            public void onReceiveMessage(String content) {
                LogUtils.i(content);
                if (GameActivity.instance != null)
                    GameActivity.instance.changeGameData(new Gson().fromJson(content, TkEntity.class));
            }

            @Override
            public void onConnectionInterrupt(Exception e) {

            }
        });
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void updateBluetooth() {
        List<BluetoothDeviceInfo> bluetoothDeviceInfoList = BluetoothUtil.getInstance().scanDevice();
        if (bluetoothDeviceInfoList == null) return;
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
            switch (gameType) {
                case JOIN:
                    findExistsGame();
                    break;
                case CREATE:
                    createBluetoothClient();
                    break;
            }
        } else if (requestCode == Constants.REQUEST_CLOSE_GAME) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        BluetoothUtil.getInstance().onExit();
        BluetoothUtil.getInstance().unregisterReceiver(this);
        super.onDestroy();
    }

    public static Intent createGameIntent(Activity activity) {
        Intent mIntent = new Intent(activity, BluetoothActivity.class);
        mIntent.putExtra(GameType.FLAG, GameType.CREATE);
        return mIntent;
    }

    public static Intent joinGameIntent(Activity activity) {
        Intent mIntent = new Intent(activity, BluetoothActivity.class);
        mIntent.putExtra(GameType.FLAG, GameType.JOIN);
        return mIntent;
    }
}
