package com.lit.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lit.R;
import com.lit.database.DatabaseUtility;
import com.lit.models.Room;

import java.util.List;

/**
 * Created by Joe on 5/1/2016.
 */
public class ChangeRoomAdapter extends BaseAdapter {
    private Context context;
    private List<Room> rooms;

    public ChangeRoomAdapter(Context context, List<Room> rooms)
    {
        this.context = context;
        this.rooms = rooms;
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public Object getItem(int position) {
        return rooms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rooms.get(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        view = inflater.inflate(R.layout.change_room_single_line, null);
        TextView roomNameText = (TextView) view.findViewById(R.id.change_room_line_text);
        roomNameText.setText(((Room)getItem(position)).getName());

        return view;
    }
}
