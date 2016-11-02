package com.dl7.recyclerviewhelper.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;

import com.dl7.recycler.helper.RecyclerViewHelper;
import com.dl7.recyclerviewhelper.R;
import com.dl7.recyclerviewhelper.adapter.DragAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DragActivity extends Activity {

    @BindView(R.id.rv_list)
    RecyclerView mRvList;

    private DragAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);
        ButterKnife.bind(this);

        mAdapter = new DragAdapter(this);
        RecyclerViewHelper.initRecyclerViewV(this, mRvList, mAdapter);
        RecyclerViewHelper.startDragAndSwipe(mRvList, mAdapter);
        mAdapter.setDragColor(Color.RED);
        mRvList.setItemAnimator(new DefaultItemAnimator());

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("item " + i);
        }
        mAdapter.updateItems(list);
    }
}
