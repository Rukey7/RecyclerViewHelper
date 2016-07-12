package com.dl7.helperlibrary.adapter;

import android.support.v7.widget.RecyclerView;

/**
 * Created by long on 2016/7/12.
 * 基础核心适配器
 */
public abstract class BaseQuickAdapter<T> extends BaseAdapter<T> {


    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param holder A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    protected abstract void convert(BaseViewHolder holder, T item);


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case LOADING_VIEW:
                break;
            case HEADER_VIEW:
                break;
            case EMPTY_VIEW:
                break;
            case FOOTER_VIEW:
                break;
            default:
                convert((BaseViewHolder) holder, mData.get(holder.getLayoutPosition() - getHeaderViewsCount()));
                break;
        }
    }

}
