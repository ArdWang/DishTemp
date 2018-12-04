package com.dt.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.dt.utils.BroadCast;
import com.dt.utils.DataParser;
import com.dt.utils.GattAttributes;
import com.dt.utils.UUIDDataBase;

import java.util.List;
import java.util.UUID;

/**
 * 创建服务 此功能用于单台的设备连接
 *
 *
 *
 *
*/

public class BluetoothService extends Service {

    private final static String TAG = BluetoothService.class.getSimpleName();

    private Context mContext;

    private BluetoothManager bluetoothManager;

    private  BluetoothAdapter bluetoothAdapter;

    private BluetoothGatt bluetoothGatt;

    private String bluetoothDeviceAddress;

    private final IBinder iBinder = new LocalBinder();


    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }


    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothManager == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }


    /**
     * 蓝牙连接
     *
     *
     */
    public boolean connect(final String address,Context context) {
        mContext = context;
        if (bluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (bluetoothDeviceAddress != null && address.equals(bluetoothDeviceAddress)
                && bluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (bluetoothGatt.connect()) {
                //mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        bluetoothGatt = device.connectGatt(context, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        bluetoothDeviceAddress = address;
        return true;
    }


    /**
     * 断开蓝牙
     *
     */
    public void disconnect() {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGatt.disconnect();
    }


    /**
     * 关闭蓝牙
     */
    public void close() {
        if(bluetoothGatt==null){
            return;
        }else{
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
    }


    /*******************************************蓝牙数据传输区块***************************************/
    /**
     * 蓝牙数据传输
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if(status==BluetoothGatt.GATT_SUCCESS){
                //连接成功的时候
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    //连接成功
                    intentAction = BroadCast.ACTION_GATT_CONNECTED;
                    //发送广播
                    broadcastUpdate(intentAction);
                    //打印连接服务
                    Log.i(TAG, "Connected to GATT server.");
                    //立即去执行发现服务
                    gatt.discoverServices();
                }
            }//当断开连接的时候
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //断开连接
                intentAction = BroadCast.ACTION_GATT_DISCONNECTED;
                //打印断开连接
                Log.i(TAG, "Disconnected from GATT server.");
                //发送广播
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(status== BluetoothGatt.GATT_SUCCESS){
                //发送发现服务的广播
                broadcastUpdate(BroadCast.ACTION_GATT_SERVICES_DISCOVERED);
                Log.i(TAG,"discoverServiced is ok");
            }else {
                Log.w(TAG, "onServicesDiscovered received: " + status);   //发现设备的时候 发送广播
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i("读取出来的值",gatt+" "+characteristic+" "+status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //读取到里面的数据时候发送广播
                broadcastUpdate(BroadCast.ACTION_DATA_AVAILABLE, characteristic);
                Log.i(TAG,"");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(BroadCast.ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if(status == BluetoothGatt.GATT_SUCCESS){
                BluetoothGattCharacteristic characteristic = descriptor.getCharacteristic();
                broadcastUpdate(BroadCast.ACTION_DATA_AVAILABLE, characteristic);
                Log.i(TAG,"success is data");
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //成功之后接受数据广播
                broadcastUpdate(BroadCast.ACTION_DATA_RECIVER_AVAILABLE, characteristic);
                Log.i(TAG, "success is data");
            } else {
                Intent intent =new Intent(BroadCast.ACTION_GATT_CHARACTERISTIC_ERROR);
                intent.putExtra(BroadCast.EXTRA_CHARACTERISTIC_ERROR_MESSAGE, "" + status);
                mContext.sendBroadcast(intent);
            }
        }
    };


    /**
     * 更新的时候发送广播
     * @param action
     */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
     * 发送广播数据处理
     * @param action
     * @param characteristic
     */
    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic){
        final Intent intent = new Intent(action);
        if(characteristic.getUuid().equals(UUIDDataBase.UUID_PROJECT_TEMP)){
            String temp = DataParser.getTempData(characteristic);
            intent.putExtra(BroadCast.EXTRA_TEMP_DATA, temp);
        }
        sendBroadcast(intent);
    }


    /**
     * Enables or disables notification on a give characteristic.
     *  启用或禁用通知特性。
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification. False otherwise.
     */
    public void setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled){
        if (bluetoothAdapter == null ||  bluetoothGatt== null) {
            return;
        }
        if (characteristic.getDescriptor(UUID
                .fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG)) != null) {
            if (enabled) {
                BluetoothGattDescriptor descriptor = characteristic
                        .getDescriptor(UUID
                                .fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                descriptor
                        .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(descriptor);
            } else {
                BluetoothGattDescriptor descriptor = characteristic
                        .getDescriptor(UUID
                                .fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                descriptor
                        .setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(descriptor);
            }
        }
        bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }




    /**
     * 蓝牙温度计最关键的一步 有些特征只能通过这个 读取  并启用通知更新数据
     * Enables or disables indications on a give characteristic.
     * 启用或禁用一个给定特性的指示
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable indications. False otherwise.
     */
    public void setCharacteristicIndication(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        String serviceUUID = characteristic.getService().getUuid().toString();
        String characteristicUUID = characteristic.getUuid().toString();
        Log.i("==TAG==",serviceUUID+"   "+characteristicUUID);
        if (bluetoothAdapter == null) {
            return;
        }
        if (characteristic.getDescriptor(UUID
                .fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG)) != null) {
            if (enabled) {
                BluetoothGattDescriptor descriptor = characteristic
                        .getDescriptor(UUID
                                .fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                descriptor
                        .setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                bluetoothGatt.writeDescriptor(descriptor);

            } else {
                BluetoothGattDescriptor descriptor = characteristic
                        .getDescriptor(UUID
                                .fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                descriptor
                        .setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(descriptor);
            }
        }
        Log.i("TAG","发现操作++++");
        bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void exchangeGattMtu(int mtu) {
        int retry = 5;
        boolean status = false;
        while (!status && retry > 0) {
            status = bluetoothGatt.requestMtu(mtu);
            retry--;
        }
    }

    /**
     *  写入蓝牙数据
     * */
    public boolean writeCharacteristic(BluetoothGattCharacteristic charac,String message){
        if (bluetoothGatt == null) {
            Log.e(TAG, "lost connection");
            return false;
        }
        if(charac!=null&&message!=null){
            byte[]a = message.getBytes();
            charac.setValue(a);
            boolean status = bluetoothGatt.writeCharacteristic(charac);
            return status;
        }else{
            return false;
        }
    }



    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (bluetoothGatt == null) {
            return null;
        }
        return bluetoothGatt.getServices();
    }


}
