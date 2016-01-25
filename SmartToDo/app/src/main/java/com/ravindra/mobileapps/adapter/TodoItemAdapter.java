package com.ravindra.mobileapps.adapter;

/**
 * Created by ravindra on 1/24/2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ravindra.mobileapps.model.TodoItem;
import com.ravindra.mobileapps.smarttodo.R;

import java.util.ArrayList;

public class TodoItemAdapter extends ArrayAdapter<TodoItem> {

    public TodoItemAdapter(Context context, ArrayList<TodoItem> users) {
        super(context, 0, users);
    }

    private static class ViewHolder {
        TextView itemName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TodoItem todoItem = getItem(position);

        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.todo_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemName = (TextView)convertView.findViewById(R.id.tvTodoItem);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvTodoItem);

        // Populate the data into the template view using the data object
        tvName.setText(todoItem.text);

        // Return the completed view to render on screen
        return convertView;
    }
}