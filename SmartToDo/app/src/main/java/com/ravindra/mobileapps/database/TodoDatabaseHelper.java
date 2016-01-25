package com.ravindra.mobileapps.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ravindra.mobileapps.model.TodoItem;
import com.ravindra.mobileapps.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ravindra on 1/12/2016.
 */
public class TodoDatabaseHelper extends SQLiteOpenHelper {
    private static TodoDatabaseHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "todoDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_TODO = "todo";
    private static final String TABLE_USERS = "users";

    private String TAG = "TodoDatabaseHelper";

    // Post Table Columns
    private static final String KEY_TODO_ID = "id";
    private static final String KEY_TODO_USER_ID_FK = "userId";
    private static final String KEY_TODO_TEXT = "text";
    private static final String KEY_TODO_COMP_DATE = "complete_by_date";
    private static final String KEY_TODO_PRIORITY = "priority";

    // User Table Columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_PROFILE_PICTURE_URL = "profilePictureUrl";

    public static synchronized TodoDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TodoDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private TodoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_TODO +
                "(" +
                KEY_TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                 KEY_TODO_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS + "," + // Define a foreign key
                KEY_TODO_TEXT + " TEXT" +
                KEY_TODO_COMP_DATE + " DATE" +
                KEY_TODO_PRIORITY + " INTEGER" +
                ")";

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY," +
                KEY_USER_NAME + " TEXT," +
                KEY_USER_PROFILE_PICTURE_URL + " TEXT" +
                ")";

        db.execSQL(CREATE_POSTS_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }

    // Insert a post into the database
    public void addPost(TodoItem todoItem) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            // The user might already exist in the database (i.e. the same user created multiple posts).
            //long userId = addorUpdateUser(post.user);

            ContentValues values = new ContentValues();
            //values.put(KEY_POST_USER_ID_FK, userId);
            values.put(KEY_TODO_TEXT, todoItem.text);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            long id = db.insertOrThrow(TABLE_TODO, null, values);
            db.setTransactionSuccessful();

            todoItem.id=id;
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    // Insert a post into the database
    public void updatePost(TodoItem todoItem) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            // The user might already exist in the database (i.e. the same user created multiple posts).
            //long userId = addorUpdateUser(post.user);

            ContentValues values = new ContentValues();
            //values.put(KEY_POST_USER_ID_FK, userId);
            values.put(KEY_TODO_TEXT, todoItem.text);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            int rows = db.update(TABLE_TODO, values, KEY_TODO_ID +"= ?", new String[]{String.valueOf(todoItem.id)});

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to update todo item to database");
        } finally {
            db.endTransaction();
        }
    }

    // Delete a post into the database
    public void deletePost(TodoItem todoItem) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            // The user might already exist in the database (i.e. the same user created multiple posts).
            //long userId = addorUpdateUser(post.user);

            ContentValues values = new ContentValues();
            values.put(KEY_TODO_ID, todoItem.id);
            values.put(KEY_TODO_TEXT, todoItem.text);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.delete(TABLE_TODO, KEY_TODO_ID + "=" + todoItem.id, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    // Insert or update a user in the database
    // Since SQLite doesn't support "upsert" we need to fall back on an attempt to UPDATE (in case the
    // user already exists) optionally followed by an INSERT (in case the user does not already exist).
    // Unfortunately, there is a bug with the insertOnConflict method
    // (https://code.google.com/p/android/issues/detail?id=13045) so we need to fall back to the more
    // verbose option of querying for the user's primary key if we did an update.
    public long addorUpdateUser(User user) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_NAME, user.userName);
            //values.put(KEY_USER_PROFILE_PICTURE_URL, user.profilePictureUrl);

            // First try to update the user in case the user already exists in the database
            // This assumes userNames are unique
            int rows = db.update(TABLE_USERS, values, KEY_USER_NAME + "= ?", new String[]{user.userName});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_USER_ID, TABLE_USERS, KEY_USER_NAME);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(user.userName)});
                try {
                    if (cursor.moveToFirst()) {
                        userId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // user with this userName did not already exist, so insert new user
                userId = db.insertOrThrow(TABLE_USERS, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
        return userId;
    }

    public List<TodoItem> getAllPosts() {
        // Create and/or open the database for writing
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<TodoItem> todoList = new ArrayList<TodoItem>();
        try {
            Cursor mCursor = db.query(TABLE_TODO, new String[]{KEY_TODO_ID, KEY_TODO_TEXT}, null, null, null, null, null);

            if (mCursor.moveToFirst()) {
                do {
                    TodoItem item = new TodoItem();
                    item.id=mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_TODO_ID));
                    item.text=mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_TODO_TEXT));

                    todoList.add(item);
                } while (mCursor.moveToNext());
            }
            if (mCursor != null && !mCursor.isClosed()) {
                mCursor.close();
            }

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        }
        return todoList;
    }

}