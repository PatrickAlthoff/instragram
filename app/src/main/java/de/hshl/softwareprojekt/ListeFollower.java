package de.hshl.softwareprojekt;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListeFollower extends AppCompatActivity {
    private static final String TAG = "ListeFollowerActivity" ;
    private ListView mListView;
    DatabaseHelperFollow mDatabaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        mListView = (ListView) findViewById(R.id.listView);
        mDatabaseHelper = new DatabaseHelperFollow(this);

        populateListView();
        Log.d(TAG, "ListView Followers erstellt");
    }
    private void populateListView() {
        Cursor data = mDatabaseHelper.getFollowerData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            listData.add(data.getString(2));
        }
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);
    }
}
