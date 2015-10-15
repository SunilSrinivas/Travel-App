package com.ikramu.travelapp.android;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class SortingActivity extends Activity {

    public static final int LIMIT_ON_SORT_LIST = 25;
    public static final int SORT_DECREASING = -1;
    public static final int SORT_INCREASING = 1;
    ListView sortedListView;
    int selectedSortOrder, selectedMenuItemId;
    AsyncTask<Void, Void, ArrayList<String>> asyncTask;
    String method, mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorting);
        sortedListView = (ListView) findViewById(R.id.listview_sorted);
        selectedSortOrder = SORT_DECREASING;
        selectedMenuItemId = R.id.action_sort_decreasing;


        Intent intent = getIntent();
        switch (intent.getIntExtra(MainActivity.SELECTED_SORTING_METHOD, 0)) {
            case R.id.action_price_sorting:
                method = "price";
                break;
            case R.id.action_time_sorting:
                method = "traveltime";
                break;
            default:
                method = null;
        }
        switch (intent.getIntExtra(MainActivity.SELECTED_TRANSPORT_MODE, 0)) {
            case R.id.action_airways:
                mode = getString(R.string.airways);
                break;
            case R.id.action_waterways:
                mode = getString(R.string.waterways);
                break;
            case R.id.action_railways:
                mode = getString(R.string.railways);
                break;
            case R.id.action_roadways:
                mode = getString(R.string.roadways);
                break;
            default:
                mode = null;
        }

        String description = "The " + method + " sorted list of " + LIMIT_ON_SORT_LIST + " " + mode;

        ((TextView) findViewById(R.id.textview_sorting_description)).setText(description);

        asyncTask = new SortingActivityAsyncTask(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions_sorting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (selectedMenuItemId == itemId) return true;
        switch (itemId) {
            case R.id.action_sort_decreasing:
                selectedMenuItemId = itemId;
                selectedSortOrder = SORT_DECREASING;
                break;
            case R.id.action_sort_increasing:
                selectedMenuItemId = itemId;
                selectedSortOrder = SORT_INCREASING;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        asyncTask = new SortingActivityAsyncTask(this).execute();
        return true;
    }
}
