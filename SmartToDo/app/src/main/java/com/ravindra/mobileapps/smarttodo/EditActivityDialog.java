package com.ravindra.mobileapps.smarttodo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.ravindra.mobileapps.database.TodoDatabaseHelper;
import com.ravindra.mobileapps.model.TodoItem;

/**
 * Created by ravindra on 1/24/2016.
 */
public class EditActivityDialog extends DialogFragment {
    EditText etToDoItem;
    long id;

    public EditActivityDialog() {

    }

    public static EditActivityDialog newInstance(String title) {
        EditActivityDialog frag = new EditActivityDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_edit_item, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = this.getArguments();
        String itemText = args.getString("todoitem");
        args.getInt("position");
        id = args.getLong("id");

        // Get field from view
        etToDoItem = (EditText) view.findViewById(R.id.etToDoItem);
        etToDoItem.setText(itemText);

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);

        // Show soft keyboard automatically and request focus to field

        etToDoItem.requestFocus();

        // Get field from view
        Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoItem todoItem = new TodoItem();
                todoItem.id = id;
                todoItem.text = etToDoItem.getText().toString();
                //items.set(position, item);
                //itemsStr.set(position, item);
                /*
                TodoActivity todoActivity = ((TodoActivity)getActivity());
                TodoDatabaseHelper.getInstance(todoActivity.getBaseContext()).updatePost(todoItem);

                todoActivity.itemsAdapter.remove(todoItem);
                todoActivity.itemsAdapter.notifyDataSetChanged();
                */
                dismiss();

            }
        });

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}