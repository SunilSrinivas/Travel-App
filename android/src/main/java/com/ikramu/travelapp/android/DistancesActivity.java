package com.ikramu.travelapp.android;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class DistancesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distances);

        final TextView statsDescription = (TextView) findViewById(R.id.textview_distance_stats_description);
        new AsyncTask<Void, Void, String>() {

            private DBInteraction dbInteraction;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                statsDescription.setVisibility(View.INVISIBLE);
            }

            @Override
            protected String doInBackground(Void... params) {
                dbInteraction = new DBInteraction();
                DBCursor cursor = dbInteraction.getDb().getCollection("distance1").find();
                DBObject object = cursor.next();
                String result = "";
                try {
                    JSONObject jsonObject = new JSONObject(object.get("value").toString());
                    result = "Total of all Distances: " + jsonObject.getLong("sum") + "\n"
                            + "Minimum Distance: " + jsonObject.getLong("min") + "\n" +
                            "Maximum Distance: " + jsonObject.getLong("max") + "\n" +
                            "Total Documents(Distances): " + jsonObject.getLong("count") + "\n"
                            + "Standard Deviation of all Distances: " + jsonObject.getDouble("diff");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                statsDescription.setText(result);
                statsDescription.setVisibility(View.VISIBLE);
            }
        }.execute();

        final ListView distancesListView = (ListView) findViewById(R.id.listview_distances);
        new AsyncTask<Void, Void, ArrayList<String>>() {

            private DBInteraction dbInteraction;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                findViewById(R.id.progressBar_loading).setVisibility(View.VISIBLE);
                distancesListView.setVisibility(View.INVISIBLE);
            }

            @Override
            protected ArrayList<String> doInBackground(Void... params) {
                dbInteraction = new DBInteraction();
                MapReduceOutput output = dbInteraction.getDb().getCollection("transport").mapReduce("function () {" +
                        "    var x = { distance : this.distance, _id : this._id , source: this.starting_source , destination : this.destination_source };" +
                        "    emit(this. distance , { min : x } )" +
                        "}", "function (key, values) {" +
                        "    var res = values[0];" +
                        "    for ( var i=1; i<values.length; i++ ) {" +
                        "        if ( values[i].min.distance < res.min.distance ) " +
                        "           res.min = values[i].min;" +
                        "    }" +
                        "    return res;" +
                        "}", null, MapReduceCommand.OutputType.INLINE, null);

                ArrayList<String> results = new ArrayList<String>();
                for (DBObject dbObject : output.results()) {
                    try {
                        JSONObject jsonObject = new JSONObject(dbObject.get("value").toString());
                        JSONObject object = jsonObject.getJSONObject("min");
                        long distance = object.getLong("distance");
                        String source = object.getString("source");
                        String destination = object.getString("destination");
                        results.add(distance + " miles | " + source + " to " + destination);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return results;
            }

            @Override
            protected void onPostExecute(ArrayList<String> results) {
                super.onPostExecute(results);
                ArrayAdapter<String> distancesAdapter = new ArrayAdapter<String>(DistancesActivity.this, android.R.layout.simple_list_item_1, results);
                distancesListView.setAdapter(distancesAdapter);
                findViewById(R.id.progressBar_loading).setVisibility(View.GONE);
                distancesListView.setVisibility(View.VISIBLE);
            }
        }.execute();
    }
}
