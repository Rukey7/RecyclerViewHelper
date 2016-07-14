package com.dl7.recyclerviewhelper.adapter;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;

import com.dl7.helperlibrary.adapter.BaseQuickAdapter;
import com.dl7.helperlibrary.adapter.BaseViewHolder;
import com.dl7.recyclerviewhelper.R;

import java.util.List;

/**
 * Created by long on 2016/7/14.
 */
public class DragAdapter extends BaseQuickAdapter<String> {

    public DragAdapter(Context context) {
        super(context);
    }

    public DragAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.adapter_drag;
    }

    @Override
    protected void convert(final BaseViewHolder holder, String item) {

        holder.setText(R.id.tv_title, item);
        holder.getView(R.id.iv_icon).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    startDrag(holder);
                }
                return false;
            }
        });
    }
}
