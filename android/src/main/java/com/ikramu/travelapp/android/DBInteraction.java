package com.ikramu.travelapp.android;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.net.UnknownHostException;

public class DBInteraction {

    private final DB db;

    public DBInteraction() {
        MongoClient client = null;
        try {
            client = new MongoClient(
                    new MongoClientURI("mongodb://naren:sunil@ds053438.mongolab.com:53438/travel_db"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (client == null)
            db = null;
        else
            db = client.getDB("travel_db");
    }

    public DB getDb() {
        return db;
    }

    public void insert(DBObject dbObject) {
        DBCollection collection = getDb().getCollection("transport");
        collection.insert(dbObject);
    }
}
