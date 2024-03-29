package com.project.wheresafe.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.project.wheresafe.R;

import java.util.ArrayList;

public class UserArrayAdapter extends ArrayAdapter<User> {
    private static LayoutInflater inflater = null;
    private Activity activity;
    private ArrayList<User> users;

    public UserArrayAdapter(Activity activity, int textViewResourceId, ArrayList<User> _lUser) {
        super(activity, textViewResourceId, _lUser);
        try {
            this.activity = activity;
            this.users = _lUser;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }

    public int getCount() {
        return users.size();
    }

    public User getItem(int position) {
        return users.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.user_list_item, null);
                holder = new ViewHolder();

                holder.display_name = (TextView) vi.findViewById(R.id.user_name);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();

            }

            holder.display_name.setText(users.get(position).getName());


        } catch (Exception e) {


        }
        return vi;
    }

    public static class ViewHolder {
        public TextView display_name;

    }

}
