package com.ikramu.travelapp.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public class ListOfModesActivity extends Activity {
    boolean hasAlternate = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_modes);

        Intent intent = getIntent();

        final String source, destination;
        source = intent.getStringExtra(MainActivity.SELECTED_SOURCE);
        destination = intent.getStringExtra(MainActivity.SELECTED_DESTINATION);

        final TextView distanceTextView = (TextView) findViewById(R.id.textview_distance);

        class MyAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {

            double distance;
            Context context;
            private DBInteraction dbInteraction;

            public MyAsyncTask(Context context) {
                this.context = context;
            }

            @Override
            protected ArrayList<String> doInBackground(Void... params) {
                return operate(source, destination);
            }

            private ArrayList<String> operate(String source, String destination) {
                ArrayList<String> modes = new ArrayList<String>();
                dbInteraction = new DBInteraction();
                BasicDBObject query = new BasicDBObject();
                BasicDBObject fields = new BasicDBObject();
                query.put("starting_source", source);
                query.put("destination_source", destination);
                fields.put("_id", 0);
                fields.put("type", 1);
                fields.put("distance", 1);
                fields.put("alternate_source", 1);

                DBCursor dbCursor = dbInteraction.getDb().getCollection("transport").find(query, fields);

                String alternateSource = null;
                while (dbCursor.hasNext()) {
                    try {
                        JSONObject returned = new JSONObject(dbCursor.next().toString());
                        distance = returned.getDouble("distance");
                        if (returned.has("alternate_source")) {
                            hasAlternate = true;
                            alternateSource = returned.getString("alternate_source");
                        }
                        JSONObject returned_type = returned.getJSONObject("type");
                        Iterator keys = returned_type.keys();
                        while (keys.hasNext()) {
                            String typeName = keys.next().toString();
                            int price = returned_type.getJSONObject(typeName).getInt("price");
                            String time = returned_type.getJSONObject(typeName).getString("traveltime");
                            modes.add(new Mode(typeName, price, time).toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (hasAlternate) {
                    hasAlternate = false;
                    ArrayList<String> alternateModes1 = operate(source, alternateSource);
                    ArrayList<String> alternateModes2 = operate(alternateSource, destination);
                    for (String modeA : alternateModes1) {
                        for (String modeB : alternateModes2) {
                            modes.add(source + " to " + alternateSource + ": " + modeA + "\n" +
                                    alternateSource + " to " + destination + ": " + modeB);
                        }
                    }
                }
                return modes;
            }

            @Override
            protected void onPostExecute(ArrayList<String> modes) {
                distanceTextView.setText(source + " | " + destination + " | " + distance + " miles");

                ListView modesListView = (ListView) findViewById(R.id.listview_modes);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, modes);
                modesListView.setAdapter(adapter);
                findViewById(R.id.progressBar_loading).setVisibility(View.GONE);
                modesListView.setVisibility(View.VISIBLE);
            }
        }

        new MyAsyncTask(this).execute();
    }
}
