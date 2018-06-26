package de.hshl.softwareprojekt;

import android.database.Cursor;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListeFollowing extends AppCompatActivity {
    private static final String TAG = "ListeFollowingActivity" ;
    private ListView mListView;
    DatabaseHelperFollow mDatabaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        mListView = (ListView) findViewById(R.id.listView);
        mDatabaseHelper = new DatabaseHelperFollow(this);

        populateListView();
        Log.d(TAG, "ListView Following erstellt");
    }

    private void populateListView() {
        Cursor data = mDatabaseHelper.getFollowingData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            listData.add(data.getString(1));
        }
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);
    }
}
