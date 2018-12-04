package com.dt.ui.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dt.R;
import com.dt.base.ui.activity.BaseActivity;
import com.dt.ui.adapter.BleDeviceAdapter;
import com.dt.widgets.CustomDialog;
import com.dt.widgets.DividerItemDecoration;
import com.dt.widgets.HeaderBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView mDevicesRv;

    private SwipeRefreshLayout mSwipeRf;

    List<String> permissionList = new ArrayList<>();

    private BluetoothAdapter mBluetoothAdapter;

    private BleDeviceAdapter bleDeviceAdapter;

    //最长扫描时间为10秒
    private static final int SCAN_PERIOD = 10000;

    private static final int REQUEST_ENABLE_BT = 1;

    private Handler mHandler;

    private ImageView mRightIv;

    private CustomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        openPermission();
        checkBluetooth();
    }

    private void openPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.BLUETOOTH);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    private void initView(){
        mDevicesRv = findViewById(R.id.mDevicesRv);
        mSwipeRf = findViewById(R.id.mSwipeRf);
        mRightIv = findViewById(R.id.mRightIv);
        mSwipeRf.setOnRefreshListener(this);

        mRightIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MeActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initData(){
        mHandler = new Handler();
        mSwipeRf.setColorSchemeResources(R.color.colorYaHei);
        bleDeviceAdapter = new BleDeviceAdapter(this);
        //必须先设置LayoutManager
        mDevicesRv.setLayoutManager(new LinearLayoutManager(this));
        mDevicesRv.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        mDevicesRv.setAdapter(bleDeviceAdapter);

        //点击事件
        bleDeviceAdapter.setOnItemClickListener(new BleDeviceAdapter.OnItemClickListener<BluetoothDevice>() {
            @Override
            public void onItemClick(final BluetoothDevice item, int position) {
                CustomDialog.Builder builder = new CustomDialog.Builder(MainActivity.this);
                builder.setTitle("Connect");
                builder.setMessage("Device Connect");

                builder.setPositiveButton(R.string.multiple, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        //跳转到多台的界面
                       // Intent intent = new Intent(MainActivity.this,DataActivity.class);
                        //intent.putExtra("deviceName",item.getName());
                       // intent.putExtra("deviceAddre",item.getAddress());
                        //startActivity(intent);

                    }
                }).setNegativeButton(R.string.single, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(MainActivity.this,DataActivity.class);
                        intent.putExtra("deviceName",item.getName());
                        intent.putExtra("deviceMac",item.getAddress());
                        startActivity(intent);
                    }
                });

                dialog = builder.create();
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);

            }
        });

    }

    /**
     * 检查设备是否提供蓝牙
     */
    private void checkBluetooth() {
        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "没有提供蓝牙", Toast.LENGTH_SHORT).show();
            //finish();
        }

        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 启动的时候要扫描蓝牙设备
     */
    @Override
    protected void onResume() {
        super.onResume();
        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        //开始扫描
        autoRefresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    /**
     * 自动执行刷新
     */
    public void autoRefresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(bleDeviceAdapter!=null){
                    bleDeviceAdapter.clear();
                }
                scanLeDevice(true);
                mSwipeRf.setRefreshing(false);
            }
        }, 1000);
        mSwipeRf.setRefreshing(true);  //直接调用是没有用的
    }


    private void scanLeDevice(boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    /*
                     * 停止扫描后需要自动连接
                     */
                    Log.i("check times", "we end!");
                }
            }, SCAN_PERIOD);
            Log.i("check times", "we starting!");
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }else{
            //停止扫描
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }


    // 找到设备回调  处理机制
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //要指定这样子的设备才能添加进去
                    bleDeviceAdapter.addDevice(device);
                    //bleDeviceAdapter.notifyDataSetChanged();
                }
            });
        }
    };


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
                if (!mBluetoothAdapter.isEnabled()) {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }
                if(bleDeviceAdapter!=null){
                    bleDeviceAdapter.clear();
                }

                scanLeDevice(true);
                // 停止刷新
                mSwipeRf.setRefreshing(false);
            }
        }, 2000); // 5秒后发送消息，停止刷新
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Log.d("TAG", "打开蓝牙成功！");
            }

            if (resultCode == RESULT_CANCELED) {
                Log.d("TAG", "放弃打开蓝牙！");
            }

        } else {
            Log.d("TAG", "蓝牙异常！");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }
}
