package com.ravindra.mobileapps.smarttodo;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ravindra.mobileapps.adapter.TodoItemAdapter;
import com.ravindra.mobileapps.database.TodoDatabaseHelper;
import com.ravindra.mobileapps.model.TodoItem;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TodoActivity extends AppCompatActivity {
    ArrayList<TodoItem> items;
    TodoItemAdapter itemsAdapter;
    List<String> itemsStr;
    ListView lvitems;
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
        //TodoDatabaseHelper todoHelper = TodoDatabaseHelper.getInstance(getBaseContext());
        //todoHelper.onCreate(db);

        //Load the existing items from the file
        readItems();

        //itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemsStr);
        itemsAdapter = new TodoItemAdapter(this,items);

        lvitems = (ListView)findViewById(R.id.lvitems);
        lvitems.setAdapter(itemsAdapter);

        //add the listeners on the list view
        setupListViewListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Add the item to the list and update the file
     */
    public void onAddItem(View view) {
        EditText etNewItem = (EditText)findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();

        if (itemText != null && itemText.length() > 0) {
            //itemsAdapter.add(itemText);
            etNewItem.setText("");
            //writeItems();

            TodoItem item = new TodoItem();
            item.text = itemText;

            itemsAdapter.add(item);

            TodoDatabaseHelper.getInstance(getBaseContext()).addPost(item);
        } else {
            Toast.makeText(this, "To do text is empty", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    This method has all the listeners that are supported on the list view.
     */
    private void setupListViewListener() {
        // Add long click listener on the list item
        lvitems.setOnItemLongClickListener(
            new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TodoItem item = items.remove(position);
                //itemsStr.remove(position);
                itemsAdapter.remove(item);
                itemsAdapter.notifyDataSetChanged();
                //writeItems();

                TodoDatabaseHelper.getInstance(getBaseContext()).deletePost(item);

                return true;
                }
            }
        );

        // On click listener on the list item
        lvitems.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TodoItem item = items.get(position);
                //launchEditItemView(item, position);
                showEditDialog(item, position);
                }
            }
        );
    }

    /*
    Navigate action to launch edit activity
     */
    private void launchEditItemView(TodoItem item, int position) {
        // first parameter is the context, second is the class of the activity to launch
        Intent i = new Intent(TodoActivity.this, EditItemActivity.class);
        // put the item and position into the bundle for access in the second activity
        i.putExtra("todoitem", item.text);
        i.putExtra("position", position);
        i.putExtra("id", item.id);

        // brings up the second activity
        startActivityForResult(i, REQUEST_CODE);
    }

    private void showEditDialog(TodoItem item, int position) {
        FragmentManager fm = getSupportFragmentManager();

        EditActivityDialog editNameDialog = EditActivityDialog.newInstance("Edit Todo");

        Bundle args = new Bundle();
        args.putString("todoitem", item.text);
        args.putInt("position", position);
        args.putLong("id", item.id);

        editNameDialog.setArguments(args);
        editNameDialog.show(fm, "activity_edit_item");
    }

    /*
    This method gets invoked on update action in Edit Activity and updates the file system.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            String item = data.getExtras().getString("todoitem");
            int position = data.getExtras().getInt("position", 0);
            long id = data.getExtras().getLong("id", 0);
            items.get(position).text = item;

            TodoItem todoItem = new TodoItem();
            todoItem.id = id;
            todoItem.text = item;
            //items.set(position, item);
            //itemsStr.set(position, item);

            TodoDatabaseHelper.getInstance(getBaseContext()).updatePost(todoItem);
            itemsAdapter.notifyDataSetChanged();
            writeItems();
        }
    }

    /*
    This method reads the previously stored data from the file.
     */
    private void readItems() {
        items = new ArrayList<TodoItem>();
        List<TodoItem> todoItemList = TodoDatabaseHelper.getInstance(getBaseContext()).getAllPosts();
        if (todoItemList != null) {
            for (TodoItem item : todoItemList) {
                items.add(item);
            }
        }

        /*
        File filedir = getFilesDir();
        File todoFile = new File(filedir, "todo.txt");

        try {
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException ioe) {
            items = new ArrayList<String>();
        }*/
    }

    /*
    This method stores the state of the app in a file.
     */
    private void writeItems() {
        /*
        In windows the below line gets the file path as:
        /data/user/0/com.ravindra.mobileapps.smarttodo/files
         */
        /*
        File filedir = getFilesDir();
        File todoFile = new File(filedir, "todo.txt");

        try {
            FileUtils.writeLines(todoFile, items);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }*/
    }
}