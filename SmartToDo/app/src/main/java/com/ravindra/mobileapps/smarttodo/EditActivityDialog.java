package com.ravindra.mobileapps.smarttodo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ravindra.mobileapps.database.TodoDatabaseHelper;
import com.ravindra.mobileapps.model.TodoItem;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ravindra on 1/24/2016.
 */
public class EditActivityDialog extends DialogFragment {
    EditText etToDoItem;
    Spinner sCategory;
    Spinner sPriority;
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
        String priority = args.getString("priority");
        String category = args.getString("category");
        id = args.getLong("id");

        // Get field from view
        etToDoItem = (EditText) view.findViewById(R.id.etToDoItem);
        etToDoItem.setText(itemText);


        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);

        // Show soft keyboard automatically and request focus to field

        etToDoItem.requestFocus();

        Spinner itemPrioritiesSpinner = (Spinner) view.findViewById(R.id.sPriority);
        String[] itemPriorities = new String[] { "High", "Medium", "Low" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, itemPriorities);
        itemPrioritiesSpinner.setAdapter(adapter);
        sPriority = (Spinner) view.findViewById(R.id.sPriority);
        // Get field from view
        if (priority != null) {
            sPriority.setSelection(adapter.getPosition(priority));
        }

        itemPrioritiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        String[] categories = new String[] { "Work", "Personal", "Social" };

        ArrayAdapter<String> catAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, categories);
        Spinner itemCatSpinner = (Spinner) view.findViewById(R.id.sCategory);
        itemCatSpinner.setAdapter(catAdapter);
        sCategory = (Spinner) view.findViewById(R.id.sPriority);
        // Get field from view
        if (category != null) {
            sCategory.setSelection(adapter.getPosition(category));
        }

        itemCatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                //sCategory.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        // Get field from view
        Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoItem todoItem = new TodoItem();
                todoItem.id = id;
                todoItem.text = etToDoItem.getText().toString();
                int position = sPriority.getSelectedItemPosition();
                todoItem.priority = sPriority.getSelectedItem().toString();
                todoItem.category = sCategory.getSelectedItem().toString();

                //items.set(position, item);
                //itemsStr.set(position, item);

                //TodoActivity todoActivity = ((TodoActivity)getActivity());
                TodoDatabaseHelper.getInstance(v.getContext()).updatePost(todoItem);

                //todoActivity.itemsAdapter.remove(todoItem);
                //todoActivity.itemsAdapter.notifyDataSetChanged();

                dismiss();

            }
        });

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}