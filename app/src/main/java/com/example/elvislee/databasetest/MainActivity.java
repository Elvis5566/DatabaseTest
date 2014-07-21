package com.example.elvislee.databasetest;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private Button Add1Button;
    private Button Add2Button;
    private ListView mDataListView;
    private DBHelper DH = null;
    private LayoutInflater mInflater;

    private String[] mDBProjection = new String[]{
            "_id",
            "_TITLE",
            "_CONTENT",
    };
    CursorAdapter cursorAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInflater = LayoutInflater.from(this);
        mDataListView = (ListView) findViewById(R.id.list_data);
        openDB();

        getLoaderManager().initLoader(0, null, this);
    }
    private void add(String Title,String Content,String Kind){
        SQLiteDatabase db = DH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("_TITLE", Title);
        values.put("_CONTENT", Content);
        values.put("_KIND", Kind);
        db.insert("MySample", null, values);
        getLoaderManager().restartLoader(0, null, this);
    }
    private void openDB(){
        DH = new DBHelper(this);
    }
    private void closeDB(){
        DH.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add1) {
            add("A", "test", "1");
        } else if (v.getId() == R.id.btn_add2) {
            add("B", "test", "2");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(this, null, mDBProjection, null, null, null) {
            @Override
            public Cursor loadInBackground() {
                // You better know how to get your database.
                SQLiteDatabase DB = DH.getReadableDatabase();
                // You can use any query that returns a cursor.
                return DB.query("MySample", getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder());
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursorAdapter = new DBAdapter(this, cursor, true);
        mDataListView.setAdapter(cursorAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private class DBAdapter extends CursorAdapter {
        public DBAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View v;

            v = mInflater.inflate(R.layout.db_item, null);

            Item item = new Item();
            item.idTextView = (TextView) v.findViewById(R.id.text_id);
            item.titleTextView = (TextView) v.findViewById(R.id.text_title);
            item.contentTextView = (TextView) v.findViewById(R.id.text_content);

            item.idTextView.setText(Integer.toString(cursor.getInt(0)));
            item.titleTextView.setText(cursor.getString(1));
            item.contentTextView.setText(cursor.getString(2));

            v.setTag(item);
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            long start = System.currentTimeMillis();

            Item item = (Item) view.getTag();
            item.idTextView.setText(Integer.toString(cursor.getInt(0)));
            item.titleTextView.setText(cursor.getString(1));
            item.contentTextView.setText(cursor.getString(2));

            Log.d("Elvis", "time: " + (System.currentTimeMillis() - start));
        }

        private class Item {
            public TextView idTextView;
            public TextView titleTextView;
            public TextView contentTextView;
        }
    }
}

