package com.stynet.shuidianbing.opticalcharacterrecognition.adapter;

import androidx.annotation.ColorInt;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.stynet.shuidianbing.opticalcharacterrecognition.R;

import java.util.List;
/**
 * Created by shuiDianBing on 2018/5/7.
 * 图文结合的adapter,布局文件传递进来,布局必须包含R.id.icon和R.id.text
 */

public class ScanCodeSelectAdapter extends RecyclerView.Adapter {
    private List<String[]>list;

    private @LayoutRes int layoutResId;
    private @ColorInt int backgroundColor;
    public ScanCodeSelectAdapter(List<String[]>list,@LayoutRes int layoutResId, @ColorInt int backgroundColr){
        this.list = list;
        this.layoutResId = layoutResId;
        this.backgroundColor = backgroundColr;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(LayoutInflater.from(parent.getContext()).inflate(layoutResId,parent));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(list.get(position)[0]).into(((Vh)holder).icon);
        ((Vh)holder).text.setText(list.get(position)[1]);
    }

    @Override
    public int getItemCount() {
        return null== list ?0: list.size();
    }
    private class Vh extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView text;
        public Vh(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
        }
    }
}
