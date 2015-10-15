package com.ikramu.travelapp.android;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mongodb.DBCursor;

import java.util.ArrayList;
import java.util.List;


public class IndexingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indexing);

        final ListView indexingResultsListView = (ListView) findViewById(R.id.listview_indexing_results);
        new AsyncTask<Void, Void, List>() {
            private DBInteraction dbInteraction;

            @Override
            protected List doInBackground(Void... params) {
                dbInteraction = new DBInteraction();
                DBCursor cursor = dbInteraction.getDb().getCollection("type1d").find();
                List<String> results = new ArrayList<String>();
                while (cursor.hasNext()) {
                    results.add(cursor.next().toString());
                }
                return results;
            }

            @Override
            protected void onPostExecute(List results) {
                super.onPostExecute(results);
                ArrayAdapter<String> indexingResultsAdapter = new ArrayAdapter<String>(IndexingActivity.this,
                        android.R.layout.simple_list_item_1, results);
                indexingResultsListView.setAdapter(indexingResultsAdapter);
            }
        }.execute();
    }
}
