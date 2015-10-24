package org.honorato.imagegrid;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;

import com.google.gson.JsonObject;

import org.honorato.imagegrid.adapters.ImageAdapter;
import org.honorato.imagegrid.api.ApiManager;
import org.honorato.imagegrid.helpers.ImageHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Searches for images using google images API:
 * https://developers.google.com/image-search/v1/jsondevguide#request-format
 *
 * Displays results in an infinite scroll
 *
 * Search taken from http://developer.android.com/training/search/setup.html
 *
 * TODO: Add    tests
 *
 * Created by jlh on 10/7/15.
 */
public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.grid)
    GridView mGrid;

    @Bind(R.id.loading)
    View mLoading;

    /**
     * The adapter for the grid itself
     */
    ImageAdapter mAdapter;

    /**
     * List of image urls being shown
     */
    List<String> mUrls;

    /**
     * Current query
     */
    String mQuery = "fruits";

    String mNextPage;

    boolean mIsFetching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Handle search intent
        handleIntent(getIntent());

        // Setup layout components
        setupGrid();
    }

    @Override
    public void onPause() {
        mIsFetching = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    /**
     * Handle custom query input
     * @param intent The intent for the search activity
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            this.mQuery = intent.getStringExtra(SearchManager.QUERY);
            this.mNextPage = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Setup the toolbar's search widget
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Setup the grid components
     */
    protected void setupGrid() {
        mUrls = new ArrayList<>();
        mAdapter = new ImageAdapter(this, mUrls);

        mGrid.setAdapter(mAdapter);

        // Check when we're at the bottom so that
        // the list is refreshed
        mGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem + visibleItemCount) >= totalItemCount) {
                    onScrollEnd();
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
        });

        fetchImages(mQuery);
    }

    protected void onScrollEnd() {
        if (mIsFetching) {
            return;
        }
        fetchImages(mQuery, mNextPage);
    }

    protected void fetchImages(String query) {
        fetchImages(query, null);
    }

    /**
     * Fetches images from the API
     * TODO: Use the result's cursor to fetch
     * relevant images
     *
     * @param query The string to be queried
     */
    protected void fetchImages(String query, String start) {
        mIsFetching = true;
        ApiManager.ResponseHandler handler = new ApiManager.ResponseHandler() {
            @Override
            public void onSuccess(JsonObject result) {
                processResult(result);
                mIsFetching = false;
            }

            @Override
            public void onFailure(Exception e) {
                mIsFetching = false;
            }
        };

        // Tell the user that we're loading stuff
        setLoader(true);

        // Make the API call
        new ApiManager(this).fetch(query, start, handler);
    }

    /**
     * Handle a successful API result
     * @param result JsonObject with the API response
     */
    protected void processResult(JsonObject result) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Processing result");
        }
        // TODO: Handle empty result
        setLoader(false);

        // Get urls and update grid
        List<String> newUrls = ImageHelper.getUrlsFromApiResult(result);
        mUrls.addAll(newUrls);
        mAdapter.notifyDataSetChanged();

        mNextPage = ImageHelper.getNextPage(result);
    }

    /**
     * Control the loading state
     * @param isLoading True if it is loading
     */
    protected void setLoader(boolean isLoading) {
        mLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}
