package com.guo.material.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.guo.material.R;

/**
 * Created by admin on 2016/3/1.
 */
public class CheeseAdapter extends RecyclerView.Adapter<CheeseAdapter.ViewHolder> {
    OnItemClickListener clickListener;
    private Context mContext;
    int[] resIds;
    String[] images = {"http://img.ivsky.com/img/tupian/pre/201512/11/weimei_hongye-001.jpg",
            "http://p1.wmpic.me/article/2016/03/14/1457926887_eipCPgZi_215x185.jpg",
            "http://p1.wmpic.me/article/2016/03/14/1457926620_pmHwibsS_215x185.jpg",
            "http://p3.wmpic.me/article/2016/03/11/1457686853_IqhOTmCK_215x185.jpg",
            "http://p2.wmpic.me/article/2016/03/07/1457340928_KAiDBPSF_215x185.jpg",
            "http://p2.wmpic.me/article/2016/03/07/1457340682_lNCyEzXY_215x185.jpg",
            "http://p3.wmpic.me/article/2016/03/01/1456815854_tXCKkUmh_215x185.jpg",
            "http://p2.wmpic.me/article/2016/03/01/1456813129_yPGtvIaA_215x185.jpg",
            "http://p2.wmpic.me/article/2016/03/01/1456801057_FHteETkS_215x185.jpg",
            "http://p3.wmpic.me/article/2016/02/20/1455949685_VMrhDlsT_215x185.jpg",
            "http://p1.wmpic.me/article/2016/02/20/1455949524_WSdvLOoD_215x185.jpg",
            "http://p3.wmpic.me/article/2016/02/18/1455758447_szfpxpmP_215x185.jpg",
            "http://p1.wmpic.me/article/2016/02/14/1455435021_hQsUGNuD_215x185.jpg",
            "http://p2.wmpic.me/article/2016/03/14/1457926624_VwqoKMEJ.jpg",
            "http://p3.wmpic.me/article/2016/03/11/1457686859_HQxYkizl.jpg"};

    public CheeseAdapter(Context context, int[] resIds) {
        this.mContext = context;
        this.resIds = resIds;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cheese_list, null);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        holder.iv_cheese.setImageResource(resIds[position]);
        Glide.with(mContext)
                .load(images[position])
                .placeholder(R.drawable.ic_def_cover)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .crossFade()
                .into(holder.iv_cheese);
        holder.itemView.setTag(R.id.iv_cheese, images[position]);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.itemClick(holder.itemView, v.getTag(R.id.iv_cheese).toString());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public interface OnItemClickListener {
        public void itemClick(View view, String url);
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
