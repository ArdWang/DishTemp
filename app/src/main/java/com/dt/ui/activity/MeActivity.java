package com.dt.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.dt.R;
import com.dt.base.ui.activity.BaseActivity;
import com.dt.data.RecycleViewItemData;
import com.dt.ext.CommonExt;
import com.dt.ui.adapter.MeAdapter;
import com.dt.ui.bean.DavidingItem;
import com.dt.ui.bean.MeConBean;
import com.dt.ui.bean.MeImageBean;

import java.util.ArrayList;
import java.util.List;

public class MeActivity extends BaseActivity {

    private List<RecycleViewItemData> dataList = new ArrayList<>();

    private String[] content = {"Version","Clear Cach","About Me"};

    private Integer[] images = {R.drawable.update,R.drawable.clear,R.drawable.about};

    private RecyclerView mMeRv;

    private MeAdapter meAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        initView();
        initData();
    }

    private void initView() {
        mMeRv = findViewById(R.id.mMeRv);
        mMeRv.setLayoutManager(new LinearLayoutManager(this));
        meAdapter = new MeAdapter(this);
        mMeRv.setAdapter(meAdapter);

        meAdapter.setOnItemClickListener(new MeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(position==2){
                    CommonExt.toast(position+"aaa");
                }else if(position==3){
                    CommonExt.toast(position+"aaa");
                }else if(position==4){
                    CommonExt.toast(position+"aaa");
                }
            }
        });
    }

    private void initData() {
        MeImageBean im = new MeImageBean();
        if(im!=null){
            im.setImg(R.drawable.temp);
            RecycleViewItemData<MeImageBean> data = new RecycleViewItemData<>();
            data.setT(im);
            data.setDataType("ME_IMAGE");
            dataList.add(data);
        }

        DavidingItem dav = new DavidingItem();
        if(dav!=null){
            RecycleViewItemData<DavidingItem> data = new RecycleViewItemData<>();
            dav.setText("GO1");
            data.setT(dav);
            data.setDataType("ME_DAVID");
            dataList.add(data);
        }


        if(true){
            for(int i=0;i<content.length;i++){
                MeConBean me = new MeConBean();
                RecycleViewItemData<MeConBean> data = new RecycleViewItemData<>();
                me.setImg(images[i]);
                me.setCon(content[i]);
                data.setT(me);
                data.setDataType("ME_CON");
                dataList.add(data);
            }
        }

        DavidingItem dav1 = new DavidingItem();
        if(dav1!=null){
            RecycleViewItemData<DavidingItem> data = new RecycleViewItemData<>();
            dav.setText("GO1");
            data.setT(dav1);
            data.setDataType("ME_DAVID");
            dataList.add(data);
        }

        if(dataList.size()>0){
            meAdapter.setData(dataList);
        }
    }



}
