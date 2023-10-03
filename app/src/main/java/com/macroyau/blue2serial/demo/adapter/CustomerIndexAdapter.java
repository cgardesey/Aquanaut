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
import com.macroyau.blue2serial.demo.realm.RealmCustomer;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Belal on 6/6/2017.
 */

public class  CustomerIndexAdapter extends RecyclerView.Adapter<CustomerIndexAdapter.ViewHolder> {

    private static final String YOUR_DIALOG_TAG = "";
    CustomerIndexAdapterInterface chatIndexAdapterInterface;
    Activity mActivity;
    boolean showMenu;
    private ArrayList<RealmCustomer> realmCustomers;
    public static final SimpleDateFormat sfd_time = new SimpleDateFormat("h:mm a");

    public CustomerIndexAdapter(CustomerIndexAdapterInterface chatIndexAdapterInterface, Activity mActivity, ArrayList<RealmCustomer> realmCustomers, boolean showMenu) {
        this.chatIndexAdapterInterface = chatIndexAdapterInterface;
        this.mActivity = mActivity;
        this.realmCustomers = realmCustomers;
        this.showMenu = showMenu;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_customer_index, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        RealmCustomer realmCustomer = realmCustomers.get(position);

        holder.name.setText(StringUtils.normalizeSpace((realmCustomer.getFirst_name()  + " " + realmCustomer.getLast_name()).replace("null", "")));
        holder.district.setText(realmCustomer.getDistrict());
        holder.locality.setText(realmCustomer.getLocality());

        holder.profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatIndexAdapterInterface.onImageClick(realmCustomers, position, holder);
            }
        });

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatIndexAdapterInterface.onItemClick(realmCustomers, position, holder);
            }
        });

        if (showMenu) {
            holder.menu.setVisibility(View.VISIBLE);
        }
        else {
            holder.menu.setVisibility(View.GONE);
        }

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatIndexAdapterInterface.onMenuClick(realmCustomers, position, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return realmCustomers.size();
    }

    public interface CustomerIndexAdapterInterface {
        void onItemClick(ArrayList<RealmCustomer> realmCustomers, int position, ViewHolder holder);
        void onImageClick(ArrayList<RealmCustomer> realmCustomers, int position, ViewHolder holder);
        void onMenuClick(ArrayList<RealmCustomer> realmCustomers, int position, ViewHolder holder);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, district, locality;
        public ImageView profilepic, menu;
        public LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            profilepic = itemView.findViewById(R.id.profilepic);
            name = itemView.findViewById(R.id.name);
            district = itemView.findViewById(R.id.district);
            locality = itemView.findViewById(R.id.my_locality);
            item = itemView.findViewById(R.id.item);
            menu = itemView.findViewById(R.id.menu);
        }
    }

    public void setFilter(ArrayList<RealmCustomer> arrayList) {
        realmCustomers = new ArrayList<>();
        realmCustomers.addAll(arrayList);
        notifyDataSetChanged();
    }
}
