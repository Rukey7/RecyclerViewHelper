package com.dl7.recyclerviewhelper.adapter;

import android.content.Context;

import com.dl7.recycler.adapter.BaseQuickAdapter;
import com.dl7.recycler.adapter.BaseViewHolder;
import com.dl7.recyclerviewhelper.R;
import com.dl7.recyclerviewhelper.entity.UserInfo;

import java.util.List;

/**
 * Created by long on 2016/7/12.
 * 用户信息适配器
 */
public class UserAdapter extends BaseQuickAdapter<UserInfo> {


    public UserAdapter(Context context) {
        super(context);
    }

    public UserAdapter(Context context, List<UserInfo> data) {
        super(context, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, UserInfo item) {
        holder.setText(R.id.tv_user_name, item.getName())
                .setText(R.id.tv_user_age, "Age: " + item.getAge())
                .setImageResource(R.id.iv_icon, R.mipmap.ic_face_funny);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.adapter_user;
    }
}
