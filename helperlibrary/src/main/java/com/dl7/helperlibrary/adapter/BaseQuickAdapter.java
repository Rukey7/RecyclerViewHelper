package com.dl7.helperlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dl7.helperlibrary.R;
import com.dl7.helperlibrary.helper.ItemTouchHelperAdapter;
import com.dl7.helperlibrary.helper.OnStartDragListener;
import com.dl7.helperlibrary.indicator.SpinKitView;
import com.dl7.helperlibrary.indicator.SpriteFactory;
import com.dl7.helperlibrary.indicator.Style;
import com.dl7.helperlibrary.indicator.sprite.Sprite;
import com.dl7.helperlibrary.listener.OnRecyclerViewItemClickListener;
import com.dl7.helperlibrary.listener.OnRecyclerViewItemLongClickListener;
import com.dl7.helperlibrary.listener.OnRequestDataListener;

import java.util.Collections;
import java.util.List;

/**
 * Created by long on 2016/4/21.
 * 适配器基类
 */
public abstract class BaseQuickAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemTouchHelperAdapter {

    protected static final int HEADER_VIEW = 0x00000111;
    protected static final int LOADING_VIEW = 0x00000222;
    protected static final int FOOTER_VIEW = 0x00000333;
    protected static final int EMPTY_VIEW = 0x00000555;

    protected Context mContext;
    protected int mLayoutResId;
    protected LayoutInflater mLayoutInflater;
    protected List<T> mData;
    private View mParentView;
    // head and footer
    private View mHeaderView;
    private View mFooterView;
    // listener
    private OnRecyclerViewItemClickListener mItemClickListener;
    private OnRecyclerViewItemLongClickListener mItemLongClickListener;
    private OnRequestDataListener onRequestDataListener;
    private OnStartDragListener mDragStartListener;
    // load more
    private boolean mIsLoadMoreEnable;
    private boolean mIsLoadingNow;
    private View mLoadingView;
    private TextView mLoadingDesc;
    private SpinKitView mLoadingIcon;
    // empty
    private View mEmptyView;


    public BaseQuickAdapter(Context context) {
        this(context, null);
    }

    public BaseQuickAdapter(Context context, List<T> data) {
        mLayoutResId = attachLayoutRes();
        if (mLayoutResId == 0) {
            throw new IllegalAccessError("Layout resource ID must be valid!");
        }
        if (data == null) {
            mData = Collections.emptyList();
        } else {
            this.mData = data;
        }
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    /**
     * 绑定布局
     *
     * @return
     */
    protected abstract int attachLayoutRes();

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param holder A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    protected abstract void convert(BaseViewHolder holder, T item);

    @Override
    public int getItemCount() {
        int count = mData.size() + getHeaderViewsCount() + getFooterViewsCount();
        if (count == 0 && mEmptyView != null) {
            return 1;
        }
        if (mIsLoadMoreEnable) {
            count++;
        }
        return count;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView != null && position == 0) {
            return HEADER_VIEW;
        } else if ((mData.size() + getHeaderViewsCount() + getFooterViewsCount()) == 0 && mEmptyView != null) {
            return EMPTY_VIEW;
        } else if (mIsLoadMoreEnable) {
            if (position == (getItemCount() - 1)) {
                return LOADING_VIEW;
            } else if (mFooterView != null && position == (getItemCount() - 2)) {
                return FOOTER_VIEW;
            }
        } else if (mFooterView != null && position == (getItemCount() - 1)) {
            return FOOTER_VIEW;
        }
        return super.getItemViewType(position);
    }

    /**
     * Called when a view created by this adapter has been attached to a window.
     * simple to solve item will layout using all
     * {@link #_setFullSpan(RecyclerView.ViewHolder)}
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int type = holder.getItemViewType();
        if (type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW) {
            _setFullSpan(holder);
        }
    }

    /**
     * When set to true, the item will layout using all span area. That means, if orientation
     * is vertical, the view will have full width; if orientation is horizontal, the view will
     * have full height.
     * if the hold view use StaggeredGridLayoutManager they should using all span area
     *
     * @param holder True if this item should traverse all spans.
     */
    protected void _setFullSpan(RecyclerView.ViewHolder holder) {
        if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            params.setFullSpan(true);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    return (type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW) ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mParentView == null) {
            mParentView = parent;
        }
        BaseViewHolder baseViewHolder = null;
        switch (viewType) {
            case LOADING_VIEW:
                baseViewHolder = new BaseViewHolder(mLoadingView);
                break;
            case HEADER_VIEW:
                baseViewHolder = new BaseViewHolder(mHeaderView);
                break;
            case EMPTY_VIEW:
                baseViewHolder = new BaseViewHolder(mEmptyView);
                break;
            case FOOTER_VIEW:
                baseViewHolder = new BaseViewHolder(mFooterView);
                break;
            default:
                View view = mLayoutInflater.inflate(mLayoutResId, parent, false);
                baseViewHolder = new BaseViewHolder(view);
                // 设置用于单项刷新的tag标识
                baseViewHolder.itemView.setTag(R.id.view_holder_tag, baseViewHolder);
                _initItemClickListener(baseViewHolder);
                break;
        }
        return baseViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case LOADING_VIEW:
                _loadMore();
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


    /************************************* 加载更多 ****************************************/

    public void setRequestDataListener(OnRequestDataListener listener) {
        this.onRequestDataListener = listener;
    }

    public void enableLoadMore(boolean isEnable) {
        this.mIsLoadMoreEnable = isEnable;
        _initLoadingView();
    }

    public void setLoadStyle(Style style) {
        Sprite sprite = SpriteFactory.create(style);
        _initLoadingView();
        mLoadingIcon.setIndeterminateDrawable(sprite);
    }

    public void setLoadDesc(String desc) {
        _initLoadingView();
        mLoadingDesc.setText(desc);
    }

    public void setLoadColor(int color) {
        mLoadingDesc.setTextColor(color);
        mLoadingIcon.getIndeterminateDrawable().setColor(color);
    }

    public void loadComplete() {
        mIsLoadingNow = false;
    }

    public void noMoreData() {
        mIsLoadingNow = false;
        mLoadingIcon.setVisibility(View.GONE);
        mLoadingDesc.setText(R.string.no_more_data);
    }

    private void _initLoadingView() {
        if (mLoadingView == null) {
            mLoadingView = mLayoutInflater.inflate(R.layout.layout_load_more, null);
            mLoadingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            mLoadingDesc = (TextView) mLoadingView.findViewById(R.id.tv_loading_desc);
            mLoadingIcon = (SpinKitView) mLoadingView.findViewById(R.id.iv_loading_icon);
        }
    }

    private void _loadMore() {
        if (!mIsLoadingNow && onRequestDataListener != null && mLoadingIcon.getVisibility() == View.VISIBLE) {
            mIsLoadingNow = true;
            onRequestDataListener.onLoadMore();
        }
    }

    /************************************* 头尾视图 ****************************************/

    public View getHeaderView() {
        return mHeaderView;
    }

    public void addHeaderView(View headerView) {
        headerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mHeaderView = headerView;
        notifyDataSetChanged();
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void addFooterView(View footerView) {
        footerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mFooterView = footerView;
        notifyDataSetChanged();
    }

    public int getHeaderViewsCount() {
        return mHeaderView == null ? 0 : 1;
    }

    public int getFooterViewsCount() {
        return mFooterView == null ? 0 : 1;
    }

    /************************************空数据****************************************/

    public View getEmptyView() {
        return mEmptyView;
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        mEmptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public int getEmptyViewCount() {
        return mEmptyView == null ? 0 : 1;
    }

    /************************************数据操作****************************************/

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    public T getItem(int position) {
        return mData.get(position);
    }

    /**
     * Get the data of list
     *
     * @return
     */
    public List<T> getData() {
        return mData;
    }

    /**
     * 更新数据，替换原有数据
     *
     * @param items
     */
    public void updateItems(List<T> items) {
        mData = items;
        notifyDataSetChanged();
    }

    /**
     * 首部插入一条数据
     *
     * @param item 数据
     */
    public void addItem(T item) {
        mData.add(0, item);
        notifyItemInserted(0);
    }

    /**
     * 插入一条数据
     *
     * @param item     数据
     * @param position 插入位置
     */
    public void addItem(T item, int position) {
        position = Math.min(position, mData.size());
        mData.add(position, item);
        notifyItemInserted(_calcPosition(position));
    }

    /**
     * 尾部插入一条数据
     * @param item 数据
     */
    public void addLastItem(T item) {
        mData.add(mData.size(), item);
        notifyItemInserted(_calcPosition(mData.size()));
    }

    /**
     * 在列表尾添加一串数据
     *
     * @param items
     */
    public void addItems(List<T> items) {
        mData.addAll(items);
        int position = _calcPosition(mData.size());
        for (T item : items) {
            notifyItemInserted(position++);
        }
    }

    /**
     * 移除一条数据
     *
     * @param position 位置
     */
    public void removeItem(int position) {
        if (position > mData.size() - 1) {
            return;
        }
        mData.remove(position);
        notifyItemRemoved(_calcPosition(position));
    }

    /**
     * 移除一条数据
     *
     * @param item 数据
     */
    public void removeItem(T item) {
        int pos = 0;
        for (T info : mData) {
            if (item.hashCode() == info.hashCode()) {
                removeItem(pos);
                break;
            }
            pos++;
        }
    }

    /**
     * 清除所有数据
     */
    public void cleanItems() {
        mData.clear();
        notifyDataSetChanged();
    }

    /**
     * 计算位置，算上头部
     * @param position
     * @return
     */
    private int _calcPosition(int position) {
        if (mHeaderView != null) {
            position++;
        }
        return position;
    }

    /************************************点击监听****************************************/

    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been clicked and held
     *
     * @param listener The callback that will run
     */
    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }


    /**
     * init the baseViewHolder to register onRecyclerViewItemClickListener and onRecyclerViewItemLongClickListener
     *
     * @param baseViewHolder
     */
    private void _initItemClickListener(final BaseViewHolder baseViewHolder) {
        if (mItemClickListener != null) {
            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(v, baseViewHolder.getLayoutPosition());
                }
            });
        }
        if (mItemLongClickListener != null) {
            baseViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mItemLongClickListener.onItemLongClick(v, baseViewHolder.getLayoutPosition());
                }
            });
        }
    }

    /************************************拖拽滑动****************************************/

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mData, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        removeItem(position);
    }

    public void setDragStartListener(OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;
    }

    protected void startDrag(RecyclerView.ViewHolder viewHolder) {
        if (mDragStartListener != null) {
            mDragStartListener.onStartDrag(viewHolder);
        }
    }

    public void setDragColor(int dragColor) {
        BaseViewHolder.setDragColor(dragColor);
    }

    public void setFreeColor(int freeColor) {
        BaseViewHolder.setFreeColor(freeColor);
    }

    /************************************* Tag标志 ****************************************/

    /**
     * 给BaseViewHolder设置Tag
     * @param viewHolder    目标BaseViewHolder
     * @param tag   tag标志
     */
    public void setTag(BaseViewHolder viewHolder, Object tag) {
        viewHolder.itemView.setTag(tag);
    }

    /**
     * 根据tag标志获取BaseViewHolder
     * @param tag   tag标志
     * @return  目标BaseViewHolder
     */
    public BaseViewHolder getTag(Object tag) {
        View view = mParentView.findViewWithTag(tag);
        if (view == null) {
            return null;
        }
        return  (BaseViewHolder) view.getTag(R.id.view_holder_tag);
    }
}
