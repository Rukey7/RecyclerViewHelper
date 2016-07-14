package com.dl7.recyclerviewhelper.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dl7.helperlibrary.helper.RecyclerViewHelper;
import com.dl7.helperlibrary.listener.OnRecyclerViewItemClickListener;
import com.dl7.helperlibrary.listener.OnRecyclerViewItemLongClickListener;
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

        mAdapter = new UserAdapter(this);
        RecyclerViewHelper.initRecyclerViewV(this, mRvList, true, mAdapter);

        for (int i = 0; i < 10; i++) {
            infoList.add(new UserInfo("Joke" + i, i));
        }
        mAdapter.updateItems(infoList);
        mAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(UserListActivity.this,
                        infoList.get(position).getName(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter.setOnItemLongClickListener(new OnRecyclerViewItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                Toast.makeText(UserListActivity.this,
                        infoList.get(position).getName() + " - " + infoList.get(position).getAge(),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        _addHeadView();
        _addFooterView();
    }

    private void _addHeadView() {
        View view = LayoutInflater.from(this).inflate(R.layout.head_test, null);
        final TextView titleView = (TextView) view.findViewById(R.id.tv_title);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserListActivity.this, titleView.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter.addHeaderView(view);
    }

    private void _addFooterView() {
        View view = LayoutInflater.from(this).inflate(R.layout.head_test, null);
        final TextView titleView = (TextView) view.findViewById(R.id.tv_title);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserListActivity.this, titleView.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter.addFooterView(view);
    }
}
