package com.ikramu.travelapp.android;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


public class InsertActivity extends Activity {

    CheckBox airwaysCheckBox, waterwaysCheckBox, railwaysCheckBox, roadwaysCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        airwaysCheckBox = (CheckBox) findViewById(R.id.checkbox_airways);
        airwaysCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                findViewById(R.id.edittext_airways_price).setEnabled(isChecked);
                findViewById(R.id.edittext_airways_price).setFocusableInTouchMode(isChecked);
                findViewById(R.id.edittext_airways_time).setEnabled(isChecked);
                findViewById(R.id.edittext_airways_time).setFocusableInTouchMode(isChecked);
            }
        });
        waterwaysCheckBox = (CheckBox) findViewById(R.id.checkbox_waterways);
        waterwaysCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                findViewById(R.id.edittext_waterways_price).setEnabled(isChecked);
                findViewById(R.id.edittext_waterways_price).setFocusableInTouchMode(isChecked);
                findViewById(R.id.edittext_waterways_time).setEnabled(isChecked);
                findViewById(R.id.edittext_waterways_time).setFocusableInTouchMode(isChecked);
            }
        });
        railwaysCheckBox = (CheckBox) findViewById(R.id.checkbox_railways);
        railwaysCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                findViewById(R.id.edittext_railways_price).setEnabled(isChecked);
                findViewById(R.id.edittext_railways_price).setFocusableInTouchMode(isChecked);
                findViewById(R.id.edittext_railways_time).setEnabled(isChecked);
                findViewById(R.id.edittext_railways_time).setFocusableInTouchMode(isChecked);
            }
        });
        roadwaysCheckBox = (CheckBox) findViewById(R.id.checkbox_roadways);
        roadwaysCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                findViewById(R.id.edittext_roadways_price).setEnabled(isChecked);
                findViewById(R.id.edittext_roadways_price).setFocusableInTouchMode(isChecked);
                findViewById(R.id.edittext_roadways_time).setEnabled(isChecked);
                findViewById(R.id.edittext_roadways_time).setFocusableInTouchMode(isChecked);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions_insert, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public void onSubmit(MenuItem item) {
        String country = ((EditText) findViewById(R.id.edittext_source_country)).getText().toString();
        String starting_source = ((EditText) findViewById(R.id.edittext_source_state)).getText().toString();
        String destination_source = ((EditText) findViewById(R.id.edittext_destination_state)).getText().toString();
        double distance = Double.parseDouble(((EditText) findViewById(R.id.edittext_distance)).getText().toString());

        double airwaysPrice, airwaysTravelTime, waterwaysPrice, waterwaysTravelTime,
                railwaysPrice, railwaysTravelTime, roadwaysPrice, roadwaysTravelTime;
        airwaysPrice = airwaysTravelTime = waterwaysPrice = waterwaysTravelTime =
                railwaysPrice = railwaysTravelTime = roadwaysPrice = roadwaysTravelTime = -1;
        if (airwaysCheckBox.isChecked()) {
            airwaysPrice = Double.parseDouble(((EditText) findViewById(R.id.edittext_airways_price)).getText().toString());
            airwaysTravelTime = Double.parseDouble(((EditText) findViewById(R.id.edittext_airways_time)).getText().toString());
        }
        if (waterwaysCheckBox.isChecked()) {
            waterwaysPrice = Double.parseDouble(((EditText) findViewById(R.id.edittext_waterways_price)).getText().toString());
            waterwaysTravelTime = Double.parseDouble(((EditText) findViewById(R.id.edittext_waterways_time)).getText().toString());
        }
        if (railwaysCheckBox.isChecked()) {
            railwaysPrice = Double.parseDouble(((EditText) findViewById(R.id.edittext_railways_price)).getText().toString());
            railwaysTravelTime = Double.parseDouble(((EditText) findViewById(R.id.edittext_railways_time)).getText().toString());
        }
        if (roadwaysCheckBox.isChecked()) {
            roadwaysPrice = Double.parseDouble(((EditText) findViewById(R.id.edittext_roadways_price)).getText().toString());
            roadwaysTravelTime = Double.parseDouble(((EditText) findViewById(R.id.edittext_roadways_time)).getText().toString());
        }

        BasicDBObject object = new BasicDBObject();
        object.append("country", country).append("starting_source", starting_source).append("destination_source", destination_source);
        object.append("distance", distance);

        if (airwaysCheckBox.isChecked() || waterwaysCheckBox.isChecked() || railwaysCheckBox.isChecked() || roadwaysCheckBox.isChecked()) {
            BasicDBObject typeObject = new BasicDBObject();
            if (airwaysCheckBox.isChecked()) {
                BasicDBObject airwaysObject = new BasicDBObject().append("price", airwaysPrice).append("traveltime", airwaysTravelTime);
                typeObject.append("Airways", airwaysObject);
            }
            if (waterwaysCheckBox.isChecked()) {
                BasicDBObject waterwaysObject = new BasicDBObject().append("price", waterwaysPrice).append("traveltime", waterwaysTravelTime);
                typeObject.append("Waterways", waterwaysObject);
            }
            if (railwaysCheckBox.isChecked()) {
                BasicDBObject railwaysObject = new BasicDBObject().append("price", railwaysPrice).append("traveltime", railwaysTravelTime);
                typeObject.append("Railways", railwaysObject);
            }
            if (roadwaysCheckBox.isChecked()) {
                BasicDBObject roadwaysObject = new BasicDBObject().append("price", roadwaysPrice).append("traveltime", roadwaysTravelTime);
                typeObject.append("Roadways", roadwaysObject);
            }
            object.append("type", typeObject);
        }

        new AsyncTask<DBObject, Void, Void>() {
            @Override
            protected Void doInBackground(DBObject... params) {
                new DBInteraction().insert(params[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void duh) {
                Toast msgToast = Toast.makeText(InsertActivity.this, "Successful", Toast.LENGTH_LONG);
                msgToast.setGravity(Gravity.CENTER, 0, 0);
                msgToast.show();
            }
        }.execute(object);
    }
}
