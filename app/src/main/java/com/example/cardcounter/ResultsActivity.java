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
                String originalText = cardRollAvgTextView.getText().toString();
                if (response.isSuccessful() && response.body() != null) {
                    // getting the original text from the textview
                    originalText = cardRollAvgTextView.getText().toString();
                    // starting with the existing text
                    StringBuilder resultsText = new StringBuilder(originalText);
                    resultsText.append("\n\n--- eBay Listings ---\n\n"); // adding a clear separator
                    if (response.body().getItemSummaries() == null || response.body().getItemSummaries().isEmpty()) {
                        resultsText.append("No listings found for '").append(cardName).append("'.\n");
                    } else {
                        for (ItemSummary item : response.body().getItemSummaries()) {
                            Price price = item.getPrice();
                            if (price != null) {
                                resultsText.append("- ")
                                        .append(item.getTitle())
                                        .append(" (")
                                        .append(price.getValue())
                                        .append(" ")
                                        .append(price.getCurrency())
                                        .append(")\n\n"); // Added extra newline for spacing
                            }
                        }
                    }
                    cardRollAvgTextView.setText(resultsText.toString());
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
