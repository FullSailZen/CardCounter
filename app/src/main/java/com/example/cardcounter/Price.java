package com.example.cardcounter;

import com.google.gson.annotations.SerializedName;

public class Price {

    @SerializedName("value")
    String value;

    @SerializedName("currency")
    String currency;

    // getter for value
    public String getValue(){
        return value;
    }

    // getter for currency
    public String getCurrency(){
        return currency;
    }
}
