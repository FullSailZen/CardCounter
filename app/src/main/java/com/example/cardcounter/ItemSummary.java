package com.example.cardcounter;

import com.google.gson.annotations.SerializedName;

public class ItemSummary {
    @SerializedName("title")
    private String title;

    @SerializedName("price")
    private Price price;

    @SerializedName("itemWebUrl")
    private String itemWebUrl;

    // getter for title
    public String getTitle(){
        return title;
    }

    // getter for price
    public Price getPrice(){
        return price;
    }

    // getter for itemWebUrl
    public String getItemWebUrl(){
        return itemWebUrl;
    }
}
