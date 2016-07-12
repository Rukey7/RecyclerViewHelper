package com.dl7.recyclerviewhelper.adapter;

import com.dl7.helperlibrary.adapter.BaseQuickAdapter;
import com.dl7.helperlibrary.adapter.BaseViewHolder;
import com.dl7.recyclerviewhelper.R;
import com.dl7.recyclerviewhelper.entity.UserInfo;

/**
 * Created by long on 2016/7/12.
 * 用户信息适配器
 */
public class UserAdapter extends BaseQuickAdapter<UserInfo> {


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
