package com.jiang.android.pulltorefresh;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiang.android.lib.BaseRefreshListener;
import com.jiang.android.lib.PullToRefreshLayout;
import com.jiang.android.lib.State;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG = "Main2Activity";
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        manager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(new NormalAdapter());
        final PullToRefreshLayout refreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh);
        refreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                Log.i(TAG, "refresh: ");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setFinish(State.REFRESH);


                    }
                }, 2000);

            }

            @Override
            public void loadMore() {
                Log.i(TAG, "loadMore: ");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setFinish(State.LOADMORE);
                    }
                }, 2000);
            }

            @Override
            public void finishLoadMore() {

                Log.i(TAG, "finishLoadMore: ");
                refreshLayout.setLoadMore(false);
                refreshLayout.setRefresh(true);

            }

            @Override
            public void finish() {

                Log.i(TAG, "finish: ");
                refreshLayout.setLoadMore(true);
                refreshLayout.setRefresh(false);

            }
        });

        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.autoRefresh();
            }
        }, 500);


    }

    public static class NormalAdapter
            extends RecyclerView.Adapter<NormalAdapter.ViewHolder> {


        public static class ViewHolder extends RecyclerView.ViewHolder {

            public final TextView mTV;

            public ViewHolder(View view) {
                super(view);
                mTV = (TextView) view.findViewById(R.id.text);
            }


        }

        public NormalAdapter() {
            super();
        }

        @Override
        public NormalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            return new NormalAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final NormalAdapter.ViewHolder holder, int position) {
            holder.mTV.setText("第" + position + "项");

        }

        @Override
        public int getItemCount() {
            return 30;
        }
    }
}
