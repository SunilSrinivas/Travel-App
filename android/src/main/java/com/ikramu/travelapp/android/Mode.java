package com.ikramu.travelapp.android;

public class Mode {
    private String type;
    private int price;
    private String time;

    public Mode(String type, int price, String time) {
        this.type = type;
        this.price = price;
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return type + " > " + "$" + price + " | " + time + " hours";
    }
}
