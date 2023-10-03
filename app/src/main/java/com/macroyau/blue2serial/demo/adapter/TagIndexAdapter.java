package com.macroyau.blue2serial.demo.adapter;

/**
 * Created by Nana on 11/10/2017.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.macroyau.blue2serial.demo.R;
import com.macroyau.blue2serial.demo.realm.RealmTag;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Belal on 6/6/2017.
 */

public class TagIndexAdapter extends RecyclerView.Adapter<TagIndexAdapter.ViewHolder> {

    TagIndexAdapterInterface tagIndexAdapterInterface;
    Activity mActivity;
    private ArrayList<RealmTag> realmTags;

    public TagIndexAdapter(TagIndexAdapterInterface tagIndexAdapterInterface, Activity mActivity, ArrayList<RealmTag> realmTags) {
        this.tagIndexAdapterInterface = tagIndexAdapterInterface;
        this.mActivity = mActivity;
        this.realmTags = realmTags;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_tag_index, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        RealmTag realmTag = realmTags.get(position);

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagIndexAdapterInterface.onItemClick(realmTags, position, holder);
            }
        });

        holder.tag.setText(realmTag.getTag_uid());


        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagIndexAdapterInterface.onMenuClick(realmTags, position, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return realmTags.size();
    }

    public interface TagIndexAdapterInterface {
        void onItemClick(ArrayList<RealmTag> realmTags, int position, ViewHolder holder);
        void onMenuClick(ArrayList<RealmTag> realmTags, int position, ViewHolder holder);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tag;
        public ImageView menu;
        public LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            tag = itemView.findViewById(R.id.tag);
            item = itemView.findViewById(R.id.item);
            menu = itemView.findViewById(R.id.menu);
        }
    }
}
