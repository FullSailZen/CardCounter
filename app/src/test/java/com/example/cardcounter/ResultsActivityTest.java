package com.example.cardcounter;

import android.content.Intent;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.Collections;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(
        manifest = Config.NONE,                    // no real manifest needed for these unit tests
        application = TestApp.class,               // use our lightweight test application class
        sdk = 35                                   // simulate android 15 (sdk 35)
)
public class ResultsActivityTest {

    // the activity instance we'll use in most tests
    private ResultsActivity activity;

    // mock of the retrofit service interface
    @Mock private EbayApiService mockService;

    // mock of the retrofit call object that enqueue() returns
    @Mock private Call<EbaySearchResponse> mockCall;

    @Before
    public void setUp() {
        // initialize all @Mock fields so we can use them
        MockitoAnnotations.openMocks(this);

        // create an activity controller (lets us control lifecycle manually)
        ActivityController<ResultsActivity> controller = Robolectric.buildActivity(ResultsActivity.class);

        // get the actual activity instance from the controller
        activity = controller.get();

        // inject our mock service before any lifecycle method runs
        activity.setEbayApiService(mockService);

        // trigger onCreate() now that the mock is already injected
        controller.create();  // safe — no real network calls will happen
    }

    // helper method: captures the callback object that activity passes to call.enqueue()
    // this lets us simulate success/error/failure manually later on
    private Callback<EbaySearchResponse> captureCallback() {
        // create a captor that can grab the callback argument
        ArgumentCaptor<Callback<EbaySearchResponse>> captor = ArgumentCaptor.forClass(Callback.class);

        // verify that enqueue was called and capture whatever callback was given
        verify(mockCall).enqueue(captor.capture());

        // return the captured callback so tests can call onResponse / onFailure on it
        return captor.getValue();
    }

    // ----------------------------------------------
    // text cleaning logic tests
    // these verify cleanTextForSearch() handles different input cases correctly
    // ----------------------------------------------

    @Test
    public void cleanTextForSearch_returnsSecondLine_whenMultipleLines() {
        // typical pokemon card text has name on line 1, set info on line 2
        String input = "Charizard\nBase Set 4/102 Holo";

        // run the cleaning method
        String result = activity.cleanTextForSearch(input);

        // we want the second line (set + number + variant) for better ebay search
        assertEquals("Base Set 4/102 Holo", result);
    }

    @Test
    public void cleanTextForSearch_returnsFirstLine_whenOnlyOneLine() {
        // sometimes only one line is recognized (e.g. modern cards)
        String input = "Pikachu VMAX";

        String result = activity.cleanTextForSearch(input);

        // fallback should be the only line we have
        assertEquals("Pikachu VMAX", result);
    }

    @Test
    public void cleanTextForSearch_returnsEmpty_whenNullInput() {
        // protect against null coming from intent extra
        String result = activity.cleanTextForSearch(null);

        // should safely return empty string instead of crashing
        assertEquals("", result);
    }

    @Test
    public void cleanTextForSearch_returnsEmpty_whenBlankInput() {
        // handle whitespace-only input (common with bad ocr)
        String result = activity.cleanTextForSearch("   \t\n");

        // treat blank inputs as no useful text
        assertEquals("", result);
    }

    // ----------------------------------------------
    // oncreate ui behavior tests
    // these check what the screen shows when activity starts
    // ----------------------------------------------

    @Test
    public void onCreate_displaysNoTextMessage_whenNoText() {
        // empty intent → no recognized text extra
        Intent intent = new Intent();

        // create activity with no extras
        ResultsActivity act = Robolectric.buildActivity(ResultsActivity.class, intent)
                .create()
                .get();

        // find the card name textview
        TextView nameView = act.findViewById(R.id.cardName);

        // should show fallback message when nothing was recognized
        assertEquals("No text was recognized.", nameView.getText().toString());
    }

    @Test
    public void onCreate_displaysCardName_whenTextPresent() {
        // simulate successful text recognition with multi-line input
        Intent intent = new Intent();
        intent.putExtra("RECOGNIZED_TEXT", "Charizard\nBase Set");

        // build and create the activity
        ResultsActivity act = Robolectric.buildActivity(ResultsActivity.class, intent)
                .create()
                .get();

        TextView nameView = act.findViewById(R.id.cardName);
        String displayed = nameView.getText().toString();

        // prefix + cleaned second line should appear
        assertTrue(displayed.contains("Card Name: Base Set"));
    }

    // ----------------------------------------------
    // verify api search gets triggered correctly from oncreate
    // ----------------------------------------------

    @Test
    public void onCreate_triggersEbaySearch_whenCleanedTextIsPresent() {
        // set up mock to return our fake call when searchItems is called with expected args
        when(mockService.searchItems(
                anyString(),                                 // auth token (we don't care what it is)
                eq("Base Set"),                              // cleaned query we expect
                eq(5),                                       // limit of 5 results
                eq("category_ids:{2536},buyingOptions:{FIXED_PRICE}")  // exact filter string
        )).thenReturn(mockCall);

        Intent intent = new Intent();
        intent.putExtra("RECOGNIZED_TEXT", "Charizard\nBase Set");

        // use controller so we can inject mock before oncreate runs
        ActivityController<ResultsActivity> controller =
                Robolectric.buildActivity(ResultsActivity.class, intent);

        ResultsActivity act = controller.get();

        // inject mock service before lifecycle starts
        act.setEbayApiService(mockService);

        // now trigger oncreate — should call searchEbayForCard with cleaned text
        controller.create();

        // confirm the service was called with the right cleaned query
        verify(mockService).searchItems(
                anyString(),
                eq("Base Set"),
                eq(5),
                eq("category_ids:{2536},buyingOptions:{FIXED_PRICE}")
        );
    }

    // ----------------------------------------------
    // test happy path: api returns results successfully
    // ----------------------------------------------

    @Test
    public void onResponse_success_appendsListing() {
        // stub any search call to return our mock call object
        when(mockService.searchItems(anyString(), anyString(), eq(5), anyString()))
                .thenReturn(mockCall);

        // trigger the method we want to test
        activity.searchEbayForCard("Test Card Name");

        // capture the real callback the activity registered
        Callback<EbaySearchResponse> callback = captureCallback();

        // build a fake successful response body
        ItemSummary item = new ItemSummary();
        Price price = new Price();
        price.value = "149.99";
        price.currency = "USD";
        item.title = "Charizard Holo";
        item.price = price;

        EbaySearchResponse body = new EbaySearchResponse();
        body.itemSummaries = Collections.singletonList(item);

        Response<EbaySearchResponse> response = Response.success(body);

        // simulate the network succeeding
        callback.onResponse(mockCall, response);

        // check that the textview now contains the expected listing info
        String displayed = activity.cardRollAvgTextView.getText().toString();
        assertTrue(displayed.contains("Charizard Holo"));
        assertTrue(displayed.contains("149.99"));
    }

    // ----------------------------------------------
    // test error response from ebay (http 4xx/5xx)
    // ----------------------------------------------

    @Test
    public void onResponse_error_setsFetchError() {
        when(mockService.searchItems(anyString(), anyString(), eq(5), anyString()))
                .thenReturn(mockCall);

        activity.searchEbayForCard("Test");

        Callback<EbaySearchResponse> callback = captureCallback();

        // create a valid (non-null) error body — retrofit requires this
        ResponseBody errorBody = ResponseBody.create(
                MediaType.get("application/json"),
                "{\"error\":\"Unauthorized\"}"   // realistic json error payload
        );

        // simulate http 401 unauthorized response
        Response<EbaySearchResponse> errorResponse = Response.error(401, errorBody);

        callback.onResponse(mockCall, errorResponse);

        // activity should show user-friendly error message
        String text = activity.cardRollAvgTextView.getText().toString();
        assertTrue(text.contains("Could not fetch eBay listings"));
    }

    // ----------------------------------------------
    // test complete network failure (no response at all)
    // ----------------------------------------------

    @Test
    public void onFailure_setsNetworkError() {
        when(mockService.searchItems(anyString(), anyString(), eq(5), anyString()))
                .thenReturn(mockCall);

        activity.searchEbayForCard("Test");

        Callback<EbaySearchResponse> callback = captureCallback();

        // simulate network exception (timeout, no internet, etc)
        callback.onFailure(mockCall, new IOException("No connection"));

        // check that the failure message appears
        String text = activity.cardRollAvgTextView.getText().toString();
        assertTrue(text.contains("Failed to connect to eBay API"));
    }
}