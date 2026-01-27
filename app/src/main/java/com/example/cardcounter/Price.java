package com.example.cardcounter;

import com.google.gson.annotations.SerializedName;

public class Price {

    @SerializedName("value")
    private String value;

    @SerializedName("currency")
    private String currency;

    // getter for value
    public String getValue(){
        return value;
    }

    // getter for currency
    public String getCurrency(){
        return currency;
    }
}
