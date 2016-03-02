package com.guo.material.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.guo.material.R;

/**
 * Created by admin on 2016/3/1.
 */
public class CheeseAdapter extends RecyclerView.Adapter<CheeseAdapter.ViewHolder> {
    OnItemClickListener clickListener;
    int[] resIds;

    public CheeseAdapter(int[] resIds) {
        this.resIds = resIds;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cheese_list, null);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.iv_cheese.setImageResource(resIds[position]);
        holder.iv_cheese.setTag(position);
        holder.iv_cheese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.itemClick(holder.itemView, (int) v.getTag());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return resIds.length;
    }

    public interface OnItemClickListener {
        public void itemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_cheese;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_cheese = (ImageView) itemView.findViewById(R.id.iv_cheese);
        }
    }
}
