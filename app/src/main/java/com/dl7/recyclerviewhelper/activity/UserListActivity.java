package com.dl7.recyclerviewhelper.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.dl7.helperlibrary.listener.OnItemClickListener;
import com.dl7.helperlibrary.listener.OnItemLongClickListener;
import com.dl7.helperlibrary.utils.ViewHelper;
import com.dl7.recyclerviewhelper.R;
import com.dl7.recyclerviewhelper.adapter.UserAdapter;
import com.dl7.recyclerviewhelper.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListActivity extends AppCompatActivity {

    @BindView(R.id.rv_list)
    RecyclerView mRvList;

    private UserAdapter mAdapter;
    List<UserInfo> infoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);

        mAdapter = new UserAdapter();
        ViewHelper.initRecyclerViewV(this, mRvList, true);
        mRvList.setAdapter(mAdapter);

        for (int i = 0; i < 100; i++) {
            infoList.add(new UserInfo("Joke" + i, i));
        }
        mAdapter.updateItems(infoList);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(UserListActivity.this,
                        infoList.get(position).getName(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                Toast.makeText(UserListActivity.this,
                        infoList.get(position).getName() + " - " + infoList.get(position).getAge(),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}
