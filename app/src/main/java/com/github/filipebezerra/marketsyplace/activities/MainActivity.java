package com.github.filipebezerra.marketsyplace.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.github.filipebezerra.marketsyplace.R;
import com.github.filipebezerra.marketsyplace.model.ActiveListings;
import com.github.filipebezerra.marketsyplace.playservices.GooglePlayServicesHelper;
import com.github.filipebezerra.marketsyplace.views.adapters.ListingAdapter;

import static android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL;

public class MainActivity extends AppCompatActivity {
    private static final String STATE_ACTIVE_LISTINGS = "STATE_ACTIVE_LISTINGS";

    //TODO: use ButterKnife to inject views
    private RecyclerView mActiveListingsList;
    private View mProgressBar;
    private TextView mErrorView;
    private ListingAdapter mActiveListingsAdapter;
    private GooglePlayServicesHelper mPlayServicesHelper;

    private void setupList() {
        //TODO: set decorator and animations
        mActiveListingsList.setLayoutManager(new StaggeredGridLayoutManager(1, VERTICAL));
        mActiveListingsList.setAdapter(mActiveListingsAdapter = new ListingAdapter(this));
    }

    private void loadViews() {
        //TODO: use ButterKnife to inject views
        mActiveListingsList = (RecyclerView) findViewById(R.id.active_listings_list);
        mProgressBar = findViewById(R.id.progress_bar);
        mErrorView = (TextView) findViewById(R.id.error_view);

        setupList();
    }

    private void showLoading() {
        //TODO: use ButterKnife.Apply
        mActiveListingsList.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);
    }

    //TODO: public only prior using otto to get notified when data is loaded in the adapter
    public void showError(final String error) {
        //TODO: use ButterKnife.Apply
        mActiveListingsList.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mErrorView.setText(error);
        mErrorView.setVisibility(View.VISIBLE);
    }

    //TODO: public only prior using otto to get notified when data is loaded in the adapter
    public void showList() {
        //TODO: use ButterKnife.Apply
        mActiveListingsList.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_main);
        loadViews();

        mPlayServicesHelper = new GooglePlayServicesHelper(this,
                mActiveListingsAdapter);

        showLoading();

        if (inState != null && inState.containsKey(STATE_ACTIVE_LISTINGS)) {
            mActiveListingsAdapter.success((ActiveListings) inState.getParcelable(
                    STATE_ACTIVE_LISTINGS), null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPlayServicesHelper.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlayServicesHelper.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPlayServicesHelper.handleActivityResult(requestCode, resultCode, data);

        if (requestCode == ListingAdapter.REQUEST_CODE_PLUS_ONE_BUTTON) {
            mActiveListingsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final ActiveListings currentActiveListings = mActiveListingsAdapter.getActiveListings();
        if (currentActiveListings != null) {
            outState.putParcelable(STATE_ACTIVE_LISTINGS, currentActiveListings);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
