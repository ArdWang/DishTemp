package com.dt.ui.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dt.R;
import com.dt.data.RecycleViewItemData;
import com.dt.ui.bean.MeConBean;
import com.dt.ui.bean.MeImageBean;
import java.util.List;

public class MeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int EMPTY_VIEW = 0;
    private static final int IMAGE_VIEW = 1;
    private static final int CON_VIEW = 2;
    private static final int DAVID_VIEW = 3;

    private List<RecycleViewItemData> dataList;

    private Context mContext;

    //ItemClick事件
    private OnItemClickListener mItemClickListener;

    public MeAdapter(Context context){
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if(viewType==EMPTY_VIEW) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_set_empty_item, viewGroup, false);
            return new EmptyViewHolder(view);
        }else if(viewType==IMAGE_VIEW){
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_set_image_item, viewGroup, false);
            return new ImageViewHolder(view);
        }else if(viewType==DAVID_VIEW){
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_set_david_item, viewGroup, false);
            return new DavidViewHolder(view);
        }else{
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_set_con_item, viewGroup, false);
            return new ConViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        if(viewHolder instanceof ImageViewHolder){
            //强制转换类型
            MeImageBean model = (MeImageBean) dataList.get(i).getT();
            ((ImageViewHolder) viewHolder).mTopImg.setImageResource(model.getImg());
        }else if(viewHolder instanceof ConViewHolder){
            MeConBean model = (MeConBean) dataList.get(i).getT();
            ((ConViewHolder) viewHolder).mConImg.setImageResource(model.getImg());
            ((ConViewHolder) viewHolder).mConTv.setText(model.getCon());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null)
                        mItemClickListener.onItemClick(i);
                }
            });

        }
    }

    /**
     设置数据
     Presenter处理过为null的情况，所以为不会为Null
     */
    public void setData(List<RecycleViewItemData> sources) {
        dataList = sources;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(dataList.size()==0){
            return EMPTY_VIEW;
        }else if(dataList.get(position).getDataType().equals("ME_IMAGE")){
            return IMAGE_VIEW;
        }else if(dataList.get(position).getDataType().equals("ME_CON")){
            return CON_VIEW;
        }else if(dataList.get(position).getDataType().equals("ME_DAVID")){
            return DAVID_VIEW;
        }else {
            return super.getItemViewType(position);
        }
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder{
        EmptyViewHolder(View view){
            super(view);
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder{
        private ImageView mTopImg;
        ImageViewHolder(View view){
            super(view);
            mTopImg = view.findViewById(R.id.mTopImg);
        }
    }

    class ConViewHolder extends RecyclerView.ViewHolder{
        private ImageView mConImg;
        private TextView mConTv;
        ConViewHolder(View view){
            super(view);
            mConImg = view.findViewById(R.id.mConImg);
            mConTv = view.findViewById(R.id.mConTv);
        }
    }

    class DavidViewHolder extends RecyclerView.ViewHolder{
        DavidViewHolder(View view){
            super(view);
        }
    }

    /**
        ItemClick事件声明
     */
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

}
