package com.example.cardcounter;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface EbayApiService {

    @Headers({
            "X-EBAY-C-MARKETPLACE-ID: EBAY_US",  // ← required for US site
            "Accept: application/json"
    })
    @GET("item_summary/search")
    Call<EbaySearchResponse> searchItems(
            @Header("Authorization") String authorization,  // "Bearer <token>"
            @Query("q") String query,
            @Query("limit") int limit,
            @Query("filter") String filter
    );
}