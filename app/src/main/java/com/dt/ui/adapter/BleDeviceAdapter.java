package com.dt.ui.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dt.R;


import java.util.ArrayList;

public class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceAdapter.ViewHolder> {

    private ArrayList<BluetoothDevice> mLeDevices;

    private Context mContext;

    //ItemClick事件
    private OnItemClickListener<BluetoothDevice> mItemClickListener;

    /**
     * contains()是判断是否有相同的字符串
     */
    public void addDevice(BluetoothDevice device) {
        if(!mLeDevices.contains(device)) {
            if(device.getName()!=null){
                if(device.getName().contains("TMW")||device.getName().contains("RT")||device.getName().contains("OTA")
                        ||device.getName().contains("Blue")){
                    mLeDevices.add(device);
                    notifyDataSetChanged();
                }
            }
        }
    }


    public BleDeviceAdapter(Context mContext) {
        this.mContext = mContext;
        mLeDevices = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_device_item,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        try{
            BluetoothDevice device = mLeDevices.get(position);
            if(device!=null){
                if(!device.getName().isEmpty()){
                    holder.mDeviceNameTv.setText(device.getName());
                }

                if(!device.getAddress().isEmpty()){
                    String array[] = device.getAddress().split(":");
                    String address = "S/N:"+array[0]+array[1]+array[2]+array[3]+array[4]+array[5];
                    holder.mDeviceMacTv.setText(address);
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null)
                        mItemClickListener.onItemClick(mLeDevices.get(position), position);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clear() {
        if(mLeDevices.size()>0) {
            mLeDevices.clear();
        }
    }


    @Override
    public int getItemCount() {
        return mLeDevices.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView mDeviceNameTv;
        TextView mDeviceMacTv;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mDeviceNameTv = itemView.findViewById(R.id.mDeviceNameTv);
            mDeviceMacTv = itemView.findViewById(R.id.mDeviceMacTv);
        }
    }

    /**
        ItemClick事件声明
     */
    public interface OnItemClickListener<BluetoothDevice> {
        void onItemClick(BluetoothDevice item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener<BluetoothDevice> listener) {
        this.mItemClickListener = listener;
    }
}
