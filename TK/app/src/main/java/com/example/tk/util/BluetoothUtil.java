package com.example.tk.util;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.example.tk.Constants;
import com.example.tk.entity.BluetoothDeviceInfo;
import com.example.tk.listener.BlueToothConnectCallback;
import com.example.tk.listener.ReceivedMessageListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothUtil {

    public static final String TAG = "BluetoothManagerUtil";

    ///////////////////////////////////////////////////////////////////////////
    // 单例模式
    private BluetoothUtil() {
    }

    public static synchronized BluetoothUtil getInstance() {
        return SingletonHolder.instance;
    }

    private static final class SingletonHolder {
        private static BluetoothUtil instance = new BluetoothUtil();
    }

    ///////////////////////////////////////////////////////////////////////////
    private String serverBlueToothAddress;  //连接蓝牙地址
    private BluetoothSocket socket = null; // 客户端socket
    private BluetoothAdapter mBluetoothAdapter;

    public BluetoothAdapter openBluetooth(Activity activity) {
        registerBluetoothScanReceiver(activity);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (null != mBluetoothAdapter) { //本地蓝牙存在...
            if (!mBluetoothAdapter.isEnabled()) { //判断蓝牙是否被打开...
                // 发送打开蓝牙的意图，系统会弹出一个提示对话框,打开蓝牙是需要传递intent的...
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //打开本机的蓝牙功能...使用startActivityForResult()方法...这里我们开启的这个Activity是需要它返回执行结果给主Activity的...
                activity.startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);

                Intent displayIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                // 设置蓝牙的可见性，最大值3600秒，默认120秒，0表示永远可见(作为客户端，可见性可以不设置，服务端必须要设置)
                displayIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                //这里只需要开启另一个activity，让其一直显示蓝牙...没必要把信息返回..因此调用startActivity()
                activity.startActivity(displayIntent);

                // 直接打开蓝牙
                //mBluetoothAdapter.enable();//这步才是真正打开蓝牙的部分....
                LogUtils.d(TAG, "打开蓝牙成功");
            } else {
                LogUtils.d(TAG, "蓝牙已经打开了...");
            }
        } else {
            LogUtils.d(TAG, "当前设备没有蓝牙模块");
            ToastUtils.showShort("设备不支持蓝牙");
        }
        return mBluetoothAdapter;
    }


    public void createBluetoothServer() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("GAME", UUID.fromString(Constants.SPP_UUID));
        } catch (IOException e) {
            LogUtils.d(e.getLocalizedMessage());
        }
//        mmServerSocket = tmp;
    }

    /**
     * 扫描设备 onResume()中执行.连接页面调用
     */
    public List<BluetoothDeviceInfo> scanDevice() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            LogUtils.e(TAG, "蓝牙状态异常");
            return null;
        }
        List<BluetoothDeviceInfo> bluetoothDeviceInfoList = new ArrayList<>();
        if (mBluetoothAdapter.isDiscovering()) { // 如果正在处于扫描过程...
            /** 停止扫描 */
            mBluetoothAdapter.cancelDiscovery(); // 取消扫描...
        } else {
            // 每次扫描前都先判断一下是否存在已经配对过的设备
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                BluetoothDeviceInfo bluetoothDeviceInfo;
                for (BluetoothDevice device : pairedDevices) {
                    bluetoothDeviceInfo = new BluetoothDeviceInfo(device.getName() + "", device.getAddress() + "");
                    bluetoothDeviceInfoList.add(bluetoothDeviceInfo);
                    LogUtils.d(TAG, "已经匹配过的设备:" + bluetoothDeviceInfo.toString());
                }
            } else {
                LogUtils.d(TAG, "没有已经配对过的设备");
            }
            /* 开始搜索 */
            mBluetoothAdapter.startDiscovery();
        }
        return bluetoothDeviceInfoList;
    }

    /**
     * 通过Mac地址去尝试连接一个设备.连接页面调用
     */
    public void connectRemoteDevice(final String serverBlueToothAddress, BlueToothConnectCallback connectInterface) {
        this.serverBlueToothAddress = serverBlueToothAddress;
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(serverBlueToothAddress);
        ThreadPoolUtils.execute(new ConnectRunnable(device, connectInterface));
    }

    /**
     * 广播反注册.连接页面调用
     */
    public void unregisterReceiver(Activity activity) {
        if (receiver != null && receiver.getAbortBroadcast()) {
            activity.unregisterReceiver(receiver);
        }
    }

    /**
     * 发送消息,在通信页面使用
     */
    public void sendMessage(String message) {
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(message + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收到消息的监听事件,在通信页面注册这个事件
     */
    public void setOnReceivedMessageListener(ReceivedMessageListener listener) {
        if (listener != null) {
            // 可以开启读数据线程
            //     MainHandler.getInstance().post(new ReadRunnable(listener));
            ThreadPoolUtils.execute(new ReadRunnable(listener));
        }
    }

    /**
     * 关闭蓝牙,在app退出时调用
     */
    public void onExit() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
            // 关闭蓝牙
            mBluetoothAdapter.disable();
        }
        closeCloseable(writer, socket);
    }

    /**
     * 连接线程
     */
    class ConnectRunnable implements Runnable {
        private BluetoothDevice device; // 蓝牙设备
        private BlueToothConnectCallback connectInterface;

        public ConnectRunnable(BluetoothDevice device, BlueToothConnectCallback connectInterface) {
            this.device = device;
            this.connectInterface = connectInterface;
        }

        @Override
        public void run() {
            if (null != device) {
                try {
                    if (socket != null) {
                        closeCloseable(socket);
                    }
//                    socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(Constants.SPP_UUID));
                    // 连接
                    LogUtils.d(TAG, "正在连接 " + serverBlueToothAddress);
                    connectInterface.connecting(serverBlueToothAddress);
//                    Message.obtain(handler, MESSAGE_TYPE_SEND, "请稍候，正在连接服务器: " + serverBlueToothAddress).sendToTarget();

                    socket.connect();
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            connectInterface.connectSuccess(serverBlueToothAddress);
                            LogUtils.d(TAG, "连接 " + serverBlueToothAddress + " 成功 ");
                        }
                    });
                    // 如果实现了连接，那么服务端和客户端就共享一个RFFCOMM信道...
//                    Message.obtain(handler, MESSAGE_TYPE_SEND, "已经连接上服务端！可以发送信息").sendToTarget();
                    // 如果连接成功了...这步就会执行...更新UI界面...否则走catch（IOException e）
//                    Message.obtain(handler, MESSAGE_ID_REFRESH_UI).sendToTarget();

                    // 屏蔽点击事件
//                    listViewMessage.setOnItemClickListener(null);
                } catch (final IOException e) {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            connectInterface.connectFailure(e);
                            LogUtils.d(TAG, "连接" + serverBlueToothAddress + "失败 " + e.getMessage());
                        }
                    });
                }
            }
        }
    }

    private BufferedWriter writer = null;

    class ReadRunnable implements Runnable {
        private ReceivedMessageListener listener;

        public ReadRunnable(ReceivedMessageListener listener) {
            this.listener = listener;
        }

        public void run() {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String content;
                while (!TextUtils.isEmpty(content = reader.readLine())) {
                    final String finalContent = content;
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onReceiveMessage(finalContent);
                        }
                    });
//                    Message.obtain(handler, MESSAGE_TYPE_RECEIVED, content).sendToTarget();
                }
            } catch (final IOException e) {
                MainHandler.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d(TAG, "连接中断 " + e.getMessage());
                        listener.onConnectionInterrupt(e);
                    }
                });
                // 连接断开
//                Message.obtain(handler, MESSAGE_ID_DISCONNECT).sendToTarget();
            }
            closeCloseable(reader);
        }
    }

    private BroadcastReceiver registerBluetoothScanReceiver(Activity activity) {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(receiver, filter);
        return receiver;
    }

    public void setOnFoundUnBondDeviceListener(OnFoundUnBondDeviceListener onFoundUnBondDeviceListener) {
        this.onFoundUnBondDeviceListener = onFoundUnBondDeviceListener;
    }

    private OnFoundUnBondDeviceListener onFoundUnBondDeviceListener;

    public interface OnFoundUnBondDeviceListener {
        void foundUnBondDevice(BluetoothDevice unBondDevice);
    }

    private void closeCloseable(Closeable... closeable) {
        if (null != closeable && closeable.length > 0) {
            for (int i = 0; i < closeable.length; i++) {
                if (closeable[i] != null) {
                    try {
                        closeable[i].close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        closeable[i] = null;
                    }
                }
            }
        }
    }

    /**
     * 下面是注册receiver监听，注册广播...说一下为什么要注册广播...
     * 因为蓝牙的通信，需要进行设备的搜索，搜索到设备后我们才能够实现连接..如果没有搜索，那还谈什么连接...
     * 因此我们需要搜索，搜索的过程中系统会自动发出三个广播...这三个广播为：
     * ACTION_DISCOVERY_START:开始搜索...
     * ACTION_DISCOVERY_FINISH:搜索结束...
     * ACTION_FOUND:正在搜索...一共三个过程...因为我们需要对这三个响应过程进行接收，然后实现一些功能，因此
     * 我们需要对广播进行注册...知道广播的人应该都知道，想要对广播进行接收，必须进行注册，否则是接收不到的...
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {//正在搜索过程...
                // 通过EXTRA_DEVICE附加域来得到一个BluetoothDevice设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 如果这个设备是不曾配对过的，添加到list列表
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    LogUtils.d(TAG, "发现没有配对过的设备:" + parseDevice2BluetoothDeviceInfo(device));
                    if (null != onFoundUnBondDeviceListener) {
                        onFoundUnBondDeviceListener.foundUnBondDevice(device);
                    }
                } else if (device.getBondState() != BluetoothDevice.BOND_BONDING) {
                    LogUtils.d(TAG, "正在配对的设备:" + parseDevice2BluetoothDeviceInfo(device));
                } else if (device.getBondState() != BluetoothDevice.BOND_NONE) {
                    LogUtils.d(TAG, "取消/未配对的设备:" + parseDevice2BluetoothDeviceInfo(device));
                } else {//搜索结束后的过程...
                    //if (mBluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
                    LogUtils.d(TAG, "没有发现设备");
                }
            }
        }
    };

    private String parseDevice2BluetoothDeviceInfo(BluetoothDevice device) {
        if (device == null) {
            return "device == null";
        }
        return new BluetoothDeviceInfo(device.getName(), device.getAddress()).toString();
    }
}
