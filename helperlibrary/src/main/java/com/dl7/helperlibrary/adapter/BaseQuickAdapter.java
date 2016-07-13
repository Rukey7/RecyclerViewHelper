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
public abstract class BaseQuickAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected static final int HEADER_VIEW = 0x00000111;
    protected static final int LOADING_VIEW = 0x00000222;
    protected static final int FOOTER_VIEW = 0x00000333;
    protected static final int EMPTY_VIEW = 0x00000555;

    protected Context mContext;
    protected int mLayoutResId;
    protected LayoutInflater mLayoutInflater;
    protected List<T> mData;
    private View mContentView;
    private View mHeaderView;
    private View mFooterView;
    // listener
    private OnRecyclerViewItemClickListener mItemClickListener;
    private OnRecyclerViewItemLongClickListener mItemLongClickListener;
    private OnRequestDataListener onRequestDataListener;
    // load more
    private boolean mIsLoadMoreEnable;
    private boolean mIsLoadingNow;
    private View mLoadingView;
    private TextView mLoadingDesc;
    private SpinKitView mLoadingIcon;


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
        // if set headView and position =0
        if (mHeaderView != null && position == 0) {
            return HEADER_VIEW;
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
        BaseViewHolder baseViewHolder = null;
        switch (viewType) {
            case LOADING_VIEW:
                baseViewHolder = new BaseViewHolder(mLoadingView);
                break;
            case HEADER_VIEW:
                baseViewHolder = new BaseViewHolder(mHeaderView);
                break;
            case EMPTY_VIEW:
                break;
            case FOOTER_VIEW:
                baseViewHolder = new BaseViewHolder(mFooterView);
                break;
            default:
                baseViewHolder = onCreateDefViewHolder(parent, viewType);
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

    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return createBaseViewHolder(parent, mLayoutResId);
    }

    protected BaseViewHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
        if (mContentView == null) {
            return new BaseViewHolder(getItemView(layoutResId, parent));
        }
        return new BaseViewHolder(mContentView);
    }

    /**
     * @param layoutResId ID for an XML layout resource to load
     * @param parent      Optional view to be the parent of the generated hierarchy or else simply an object that
     *                    provides a set of LayoutParams values for root of the returned
     *                    hierarchy
     * @return view will be return
     */
    protected View getItemView(int layoutResId, ViewGroup parent) {
        return mLayoutInflater.inflate(layoutResId, parent, false);
    }

    /************************************* 刷新加载 ****************************************/

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
        headerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mHeaderView = headerView;
        notifyDataSetChanged();
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void addFooterView(View footerView) {
        footerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mFooterView = footerView;
        notifyDataSetChanged();
    }

    /**
     * if setHeadView will be return 1 if not will be return 0
     *
     * @return
     */
    public int getHeaderViewsCount() {
        return mHeaderView == null ? 0 : 1;
    }

    /**
     * if mFooterView will be return 1 or not will be return 0
     *
     * @return
     */
    public int getFooterViewsCount() {
        return mFooterView == null ? 0 : 1;
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
}
