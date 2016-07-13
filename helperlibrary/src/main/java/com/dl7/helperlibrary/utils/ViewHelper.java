package com.dl7.helperlibrary.utils;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dl7.helperlibrary.adapter.BaseQuickAdapter;
import com.dl7.helperlibrary.divider.DividerGridItemDecoration;
import com.dl7.helperlibrary.divider.DividerItemDecoration;
import com.dl7.helperlibrary.listener.OnRequestDataListener;


/**
 * Created by long on 2016/3/30.
 * 视图帮助类
 */
public class ViewHelper {

    private ViewHelper() {
        throw new RuntimeException("ViewHelper cannot be initialized!");
    }


    /**
     * 配置垂直列表RecyclerView
     * @param view
     */
    public static void initRecyclerViewV(Context context, RecyclerView view, boolean isDivided) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        view.setHasFixedSize(true);
        view.setLayoutManager(layoutManager);
        view.setItemAnimator(new DefaultItemAnimator());
        if (isDivided) {
            view.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
        }
    }

    public static void initRecyclerViewV(Context context, RecyclerView view, boolean isDivided, BaseQuickAdapter adapter,
                                         OnRequestDataListener listener) {
        initRecyclerViewV(context, view, isDivided);
        adapter.enableLoadMore(true);
        adapter.setRequestDataListener(listener);
        view.setAdapter(adapter);
    }

    public static void initRecyclerViewV(Context context, RecyclerView view, BaseQuickAdapter adapter,
                                         OnRequestDataListener listener) {
        initRecyclerViewV(context, view, false, adapter, listener);
    }

    /**
     * 配置水平列表RecyclerView
     * @param view
     */
    public static void initRecyclerViewH(Context context, RecyclerView view, boolean isDivided) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        view.setHasFixedSize(true);
        view.setLayoutManager(layoutManager);
        view.setItemAnimator(new DefaultItemAnimator());
        if (isDivided) {
            view.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL_LIST));
        }
    }

    /**
     * 配置网格列表RecyclerView
     * @param view
     */
    public static void initRecyclerViewG(Context context, RecyclerView view, boolean isDivided) {
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        view.setHasFixedSize(true);
        view.setLayoutManager(layoutManager);
        view.setItemAnimator(new DefaultItemAnimator());
        if (isDivided) {
            view.addItemDecoration(new DividerGridItemDecoration(context));
        }
    }
}
