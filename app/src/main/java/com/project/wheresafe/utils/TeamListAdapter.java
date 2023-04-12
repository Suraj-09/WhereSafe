package com.project.wheresafe.utils;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.wheresafe.R;

import java.util.ArrayList;

public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.MyViewHolder> {
    private ArrayList<User> userList;

    public TeamListAdapter(ArrayList<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_teammate, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User item = userList.get(position);
        holder.name.setText(item.getName());
//        holder.descriptionTextView.setText(item.getDescription());
        // set other views as per your requirement
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
//        public TextView descriptionTextView;
        // add other views as per your requirement

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.userName);
//            descriptionTextView = view.findViewById(R.id.description_text_view);
            // initialize other views as per your requirement
        }
    }
}


