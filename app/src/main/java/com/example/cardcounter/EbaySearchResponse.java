package com.example.cardcounter;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EbaySearchResponse {
    @SerializedName("itemSummaries")
    private List<ItemSummary> itemSummaries;

    // getter for list of items
    public List<ItemSummary> getItemSummaries(){
        return itemSummaries;
    }
}
