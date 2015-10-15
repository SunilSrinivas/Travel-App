package com.ikramu.travelapp.android;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

class SortingActivityAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {
    SortingActivity context;
    private DBInteraction dbInteraction;

    SortingActivityAsyncTask(SortingActivity context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        context.findViewById(R.id.progressBar_loading).setVisibility(View.VISIBLE);
        context.sortedListView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {

        dbInteraction = new DBInteraction();
        DB db = dbInteraction.getDb();
        DBCollection collection = db.getCollection("transport");
        DBObject ref = new BasicDBObject("type." + context.mode, new BasicDBObject("$exists", true));
        DBObject keys = new BasicDBObject();
        keys.put("_id", 0);
        keys.put("type." + context.mode + "." + context.method, 1);
        keys.put("starting_source", 1);
        keys.put("destination_source", 1);
        keys.put("distance", 1);
        DBCursor cursor = collection.find(ref, keys);
        cursor.sort(new BasicDBObject("type." + context.mode + "." + context.method, context.selectedSortOrder));
        cursor.limit(SortingActivity.LIMIT_ON_SORT_LIST);
        ArrayList<String> results = new ArrayList<String>();
        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            StringBuilder printString = new StringBuilder();
            try {
                JSONObject jsonObject = new JSONObject(object.toString());
                printString.append("From ").append(jsonObject.get("starting_source")).append(" to ");
                printString.append(jsonObject.get("destination_source")).append(" | ");
                if (context.method.equals("price")) printString.append("$");
                JSONObject typeJson = jsonObject.getJSONObject("type");
                JSONObject modeJson = typeJson.getJSONObject(typeJson.keys().next().toString());
                printString.append(modeJson.getDouble(modeJson.keys().next().toString()));
                if (context.method.equals("traveltime")) printString.append(" hours");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            results.add(printString.toString());
        }
        return results;
    }

    @Override
    protected void onPostExecute(ArrayList<String> results) {
        super.onPostExecute(results);
        ArrayAdapter<String> sortedAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, results);
        context.sortedListView.setAdapter(sortedAdapter);
        context.findViewById(R.id.progressBar_loading).setVisibility(View.GONE);
        context.sortedListView.setVisibility(View.VISIBLE);
    }
}