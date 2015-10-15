package com.ikramu.travelapp.android;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class MainActivity extends Activity {

    public static final String SELECTED_SOURCE = "com.ikramu.travelapp.android.selected_source";
    public static final String SELECTED_DESTINATION = "com.ikramu.travelapp.android.selected_destination";
    public static final String SELECTED_SORTING_METHOD = "com.ikramu.travelapp.android.selectedSortingMethod";
    public static final String SELECTED_TRANSPORT_MODE = "com.ikramu.travelapp.android.selectedTransportMode";
    Spinner sourceCountrySpinner, sourceStateSpinner, destinationStateSpinner;

    int selectedSortingMethod, selectedTransportMode;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar_loading);
        sourceCountrySpinner = (Spinner) findViewById(R.id.spinner_source_country);
        sourceStateSpinner = (Spinner) findViewById(R.id.spinner_source_state);
        destinationStateSpinner = (Spinner) findViewById(R.id.spinner_destination);

        new AsyncTask<Void, Void, List>() {
            private DBInteraction dbInteraction;

            @Override
            protected List doInBackground(Void... params) {
                dbInteraction = new DBInteraction();
                return dbInteraction.getDb().getCollection("transport").distinct("country");
            }

            @Override
            protected void onPostExecute(List sourceCountries) {
                super.onPostExecute(sourceCountries);
                ArrayAdapter<String> sourceCountryAdapter = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, sourceCountries);
                sourceCountrySpinner.setAdapter(sourceCountryAdapter);
            }
        }.execute();

        sourceCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = String.valueOf(((TextView) view).getText());
                new AsyncTask<String, Void, List>() {
                    private DBInteraction dbInteraction;

                    @Override
                    protected List doInBackground(String... params) {
                        dbInteraction = new DBInteraction();
                        BasicDBObject query = new BasicDBObject("country", Pattern.compile("^" + params[0] + "$"));
                        return dbInteraction.getDb().getCollection("transport").distinct("starting_source", query);
                    }

                    @Override
                    protected void onPostExecute(List sourceStates) {
                        super.onPostExecute(sourceStates);
                        ArrayAdapter<String> sourceStateAdapter = new ArrayAdapter<String>(MainActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, sourceStates);
                        sourceStateSpinner.setAdapter(sourceStateAdapter);
                    }
                }.execute(str);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sourceStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = String.valueOf(((TextView) view).getText());
                new AsyncTask<String, Void, List>() {
                    private DBInteraction dbInteraction;

                    @Override
                    protected List doInBackground(String... params) {
                        dbInteraction = new DBInteraction();
                        MapReduceOutput output = dbInteraction.getDb().getCollection("transport").mapReduce("function() " +
                                "{ " +
                                "if(this.starting_source == \"" + params[0] + "\")" +
                                "emit(this.destination_source , 1); " +
                                "}", "function(key, values) " +
                                "{ " +
                                "var count_population = {};" +
                                "for(var i in values) {" +
                                "if (count_population[i]) {" +
                                "count_population[i] += values[i];" +
                                "}" +
                                "else" +
                                "{" +
                                "count_population[i] = values[i];" +
                                "}" +
                                "}" +
                                "return Array.sum(values);" +
                                "  }", null, MapReduceCommand.OutputType.INLINE, null);
                        List<String> results = new ArrayList<String>();
                        for (DBObject object : output.results()) {
                            results.add(object.get("_id").toString());
                        }
                        return results;
                    }

                    @Override
                    protected void onPostExecute(List destinationStates) {
                        super.onPostExecute(destinationStates);

                        ArrayAdapter<String> destinationStateAdapter = new ArrayAdapter<String>(MainActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, destinationStates);
                        destinationStateSpinner.setAdapter(destinationStateAdapter);
                        progressBar.setVisibility(View.GONE);
                        sourceCountrySpinner.setVisibility(View.VISIBLE);
                        sourceStateSpinner.setVisibility(View.VISIBLE);
                        destinationStateSpinner.setVisibility(View.VISIBLE);
                        findViewById(R.id.textview_source).setVisibility(View.VISIBLE);
                        findViewById(R.id.textview_destination).setVisibility(View.VISIBLE);
                        findViewById(R.id.button_show).setVisibility(View.VISIBLE);
                    }
                }.execute(str);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * On Click Show button.
     *
     * @param view
     */
    public void onShowButtonClicked(View view) {
        Intent intent = new Intent(this, ListOfModesActivity.class);
        intent.putExtra(SELECTED_SOURCE, (String) sourceStateSpinner.getSelectedItem());
        intent.putExtra(SELECTED_DESTINATION, (String) destinationStateSpinner.getSelectedItem());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == R.id.action_distances_sorting || item.getItemId() == R.id.action_insert || item.getItemId() == R.id.action_indexing)
                continue;
            SubMenu subMenu = item.getSubMenu();
            subMenu.add(Menu.NONE, R.id.action_airways, Menu.NONE, getString(R.string.airways));
            subMenu.add(Menu.NONE, R.id.action_waterways, Menu.NONE, getString(R.string.waterways));
            subMenu.add(Menu.NONE, R.id.action_railways, Menu.NONE, getString(R.string.railways));
            subMenu.add(Menu.NONE, R.id.action_roadways, Menu.NONE, getString(R.string.roadways));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_price_sorting:
            case R.id.action_time_sorting:
                selectedSortingMethod = itemId;
                return true;
            case R.id.action_airways:
            case R.id.action_waterways:
            case R.id.action_railways:
            case R.id.action_roadways:
                selectedTransportMode = itemId;
                Intent intent = new Intent(this, SortingActivity.class);
                intent.putExtra(SELECTED_SORTING_METHOD, selectedSortingMethod);
                intent.putExtra(SELECTED_TRANSPORT_MODE, selectedTransportMode);
                startActivity(intent);
                return true;
            case R.id.action_distances_sorting:
                Intent distancesIntent = new Intent(this, DistancesActivity.class);
                startActivity(distancesIntent);
                return true;
            case R.id.action_insert:
                Intent insertIntent = new Intent(this, InsertActivity.class);
                startActivity(insertIntent);
                return true;
            case R.id.action_indexing:
                Intent indexingIntent = new Intent(this, IndexingActivity.class);
                startActivity(indexingIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}