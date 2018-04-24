package com.vmax.demo.nativeCSRecycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vmax.android.ads.api.VmaxAdView;

import java.util.ArrayList;

/**
 * Created by prathameshs on 07-04-2017.
 */

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.TheViewHolder> {

    ArrayList<BlogModel> blogList;
    VmaxAdView theAd;


    public RecycleAdapter( ArrayList<BlogModel> list,VmaxAdView vmaxAdView)
    {
        blogList=list;
        theAd=vmaxAdView;

    }

    public class TheViewHolder extends RecyclerView.ViewHolder
    {
        ImageView blogImage;
        TextView mainTitle,date,author;
        public TheViewHolder(View v,int position)
        {
            super(v);
            if(position%10!=0||position==0) {

                blogImage = (ImageView) v.findViewById(R.id.blog_img);
                mainTitle = (TextView) v.findViewById(R.id.blog_title);
                date = (TextView) v.findViewById(R.id.blog_date);
                author = (TextView) v.findViewById(R.id.blog_author);
            }


        }

    }

    @Override
    public TheViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view;

        if(viewType%10==0&&viewType!=0&&theAd!=null){

            view=theAd;
        }
        else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_view,
                    parent, false);
        }





        return new TheViewHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(TheViewHolder holder, int position) {


        if(position%10!=0||position==0)
        {
            holder.blogImage.setImageDrawable(blogList.get(position).getBlogImage());
            holder.author.setText(blogList.get(position).getAuthor());
            holder.date.setText(blogList.get(position).getDate());
            holder.mainTitle.setText(blogList.get(position).getTitle());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }
}
