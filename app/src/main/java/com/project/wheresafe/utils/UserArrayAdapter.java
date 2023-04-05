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
        private Activity activity;
        private ArrayList<User> users;
        private static LayoutInflater inflater = null;

        public UserArrayAdapter (Activity activity, int textViewResourceId,ArrayList<User> _lUser) {
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

        public static class ViewHolder {
            public TextView display_name;
            public TextView display_number;

        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            final ViewHolder holder;
            try {
                if (convertView == null) {
                    vi = inflater.inflate(R.layout.user_list_item, null);
                    holder = new ViewHolder();

                    holder.display_name = (TextView) vi.findViewById(R.id.user_name);
//                    holder.display_number = (TextView) vi.findViewById(R.id.display_number);


                    vi.setTag(holder);
                } else {
                    holder = (ViewHolder) vi.getTag();

                }
//
//                vi.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });


                holder.display_name.setText(users.get(position).getName());
//                holder.display_number.setText(lProducts.get(position).number);


            } catch (Exception e) {


            }
            return vi;
        }
    
}
