package com.example.cardcounter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResultsActivity extends AppCompatActivity {
    private TextView cardSetTextView;
    TextView cardRollAvgTextView;
    private EbayApiService ebayApiService; // the retrofit service
    private static final String EBAY_API_TAG = "EbayApi";
    String authToken = "v^1.1#i^1#f^0#p^1#I^3#r^0#t^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";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Button resultsGoBackButton = findViewById(R.id.resultsGoBackButton);
        TextView cardNameTextView = findViewById(R.id.cardName);
        // using this for the results
        cardRollAvgTextView = findViewById(R.id.cardRollAvg);


        if (ebayApiService == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.sandbox.ebay.com/buy/browse/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ebayApiService = retrofit.create(EbayApiService.class);
        }

        Intent intent = getIntent();
        String recognizedText = intent.getStringExtra("RECOGNIZED_TEXT");

        if (recognizedText != null && !recognizedText.isEmpty()) {
            // cleaning the extracted text
            String cleanedQuery = cleanTextForSearch(recognizedText);
            // displaying the card name to the user
            cardNameTextView.setText("Card Name: " + cleanedQuery);
            // searching ebay using the cleaned query
            searchEbayForCard(cleanedQuery);
        } else {
            cardNameTextView.setText("No text was recognized.");
        }

        resultsGoBackButton.setOnClickListener(v -> finish());
    }

    @VisibleForTesting
    public void setEbayApiService(EbayApiService service) {
        this.ebayApiService = service;
    }

    /**
     * this method is used to clean the raw text extracted from google ML kit to create a
     * better search query for ebays browse api. typically, the name of the card is the
     * first line extracted, so we will be using that for basic test purposes. if the
     * app ever goes to production, this must be changed i would imagine.*
     * @param rawText is the full text extracted from the image
     * @return is a cleaner string meant to be used as a search query
     */
    String cleanTextForSearch(String rawText){
        if(rawText == null || rawText.isEmpty()){
            return "";
        }
        String[] lines = rawText.split("\n");
        if (lines.length > 1) { // checking to see if a second line actually exists
            return lines[1].trim(); // if it does, return it
        } else if (lines.length > 0) { // else if there's only one line, fall back to the first line
            return lines[0].trim();
        } else {
            return ""; // else return empty if there's nothing
        }
    }
    void searchEbayForCard(String cardName) {

        String categoryFilter = "category_ids:{2536},buyingOptions:{FIXED_PRICE}";
        String bearerAuth = "Bearer " + authToken;
        int limit = 5;

        Call<EbaySearchResponse> call = ebayApiService.searchItems(bearerAuth, cardName, limit, categoryFilter);

        call.enqueue(new Callback<EbaySearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<EbaySearchResponse> call, @NonNull Response<EbaySearchResponse> response) {
                // grab whatever text is currently showing in the results textview (card name or prev message)
                String originalText = cardRollAvgTextView.getText().toString();
               // check if http request worked and that we actually got ebay data back
                if (response.isSuccessful() && response.body() != null) {
                    // pull out the list of item summaries from ebays response
                    List<ItemSummary> items = response.body().getItemSummaries();
                    // start building the new text by copying what was already there
                    StringBuilder sb = new StringBuilder(originalText);
                    // add a header as a separator for clarity
                    sb.append("\n\n--- Current Market ---\n");
                    // handle cases where ebay sent back unusable data (or nothing)
                    if (items == null || items.isEmpty()) {
                        sb.append("No listings found.");
                    } else {
                        // instantiate variables to keep track of total price and how many valid prices were found
                        double sum = 0;
                        int validCount = 0;
                        // loop through each item returned to us by ebay
                        for (ItemSummary item : items) {
                            // get the price object from the listing
                            Price p = item.getPrice();
                            // if the price exists...
                            if (p != null) {
                                try {
                                    // convert the price string into a real number
                                    sum += Double.parseDouble(p.getValue());
                                    // increment the validCount due to getting that number
                                    validCount++;
                                } // if the price can't be converted, ignore it
                                catch (Exception ignored) {}
                            }
                        }
                        // only show an average if we extracted at least one good price
                        if (validCount > 0) {
                            double avg = sum / validCount;
                            // add the average to the display text with two decimal places (%.2f)
                            sb.append("Avg price: $").append(String.format("%.2f", avg))
                                    .append(" (").append(validCount).append(" listings)");
                        } else { // if no valid prices found
                            sb.append("No valid prices found.");
                        }
                    }
                    // update the textview with everything we just computed above
                    cardRollAvgTextView.setText(sb.toString());
                } else {
                    String errorBody = "Error occurred.";
                    if (response.errorBody() != null) {
                        try {
                            errorBody = response.errorBody().string();
                        } catch (IOException e) {
                            Log.e(EBAY_API_TAG, "Error reading error body", e);
                        }
                    }
                    cardRollAvgTextView.setText(originalText + "\n\n" + "Could not fetch eBay listings.");
                    Log.e(EBAY_API_TAG, "API Error: " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(@NonNull Call<EbaySearchResponse> call, @NonNull Throwable t) {
                cardRollAvgTextView.setText("Failed to connect to eBay API. Please check your internet connection.");
                Log.e(EBAY_API_TAG, "Network Failure: ", t);
            }
        });
    }
}
