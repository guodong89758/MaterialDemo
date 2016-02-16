package com.guo.material.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.guo.material.R;

/**
 * Created by admin on 2016/2/16.
 */
public class CheeseListFragment extends Fragment {

    private RecyclerView rv_cheese;
    private CheeseAdapter mAdapter;
    private static int[] resIds = {R.drawable.cheese_1, R.drawable.cheese_2, R.drawable.cheese_3, R.drawable.cheese_4, R.drawable.cheese_5};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cheese_list, null);
        rv_cheese = (RecyclerView) rootView.findViewById(R.id.rv_cheese);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_cheese.setLayoutManager(layoutManager);

        mAdapter = new CheeseAdapter();
        rv_cheese.setAdapter(mAdapter);
    }


    static class CheeseAdapter extends RecyclerView.Adapter<CheeseListFragment.ViewHolder> {

        @Override
        public CheeseListFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cheese_list, null);
            ViewHolder holder = new ViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(CheeseListFragment.ViewHolder holder, int position) {
            holder.iv_cheese.setImageResource(resIds[position]);
        }

        @Override
        public int getItemCount() {
            return resIds.length;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_cheese;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_cheese = (ImageView) itemView.findViewById(R.id.iv_cheese);
        }
    }
}
