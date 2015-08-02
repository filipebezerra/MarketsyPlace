package com.github.filipebezerra.marketsyplace.views.adapters;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.filipebezerra.marketsyplace.R;
import com.github.filipebezerra.marketsyplace.activities.MainActivity;
import com.github.filipebezerra.marketsyplace.api.Etsy;
import com.github.filipebezerra.marketsyplace.model.ActiveListings;
import com.github.filipebezerra.marketsyplace.model.Listing;
import com.github.filipebezerra.marketsyplace.playservices.GooglePlayServicesHelper.GooglePlayServicesListener;
import com.google.android.gms.plus.PlusOneButton;
import com.google.android.gms.plus.PlusShare;
import com.squareup.picasso.Picasso;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 01/08/2015
 * @since #
 */
public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingHolder>
    implements Callback<ActiveListings>, GooglePlayServicesListener {

    public static final int REQUEST_CODE_PLUS_ONE_BUTTON = 10;
    public static final int REQUEST_CODE_SHARE_BUTTON = 11;

    private final MainActivity mMainActivity;
    private final LayoutInflater inflator;
    private ActiveListings mActiveListings;
    private boolean isGooglePlayServicesAvailable;

    //TODO: use context from ViewGroup parameter in onCreteViewHolder method
    public ListingAdapter(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        inflator = LayoutInflater.from(mainActivity);
    }

    @Override
    public ListingHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = inflator.inflate(R.layout.item_listing, viewGroup, false);
        return new ListingHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListingHolder holder, int position) {
        final Listing listing = mActiveListings.results[position];

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                viewIntent.setData(Uri.parse(listing.url));
                mMainActivity.startActivity(Intent.createChooser(viewIntent, "Open with..."));
            }
        });

        holder.mListingTitle.setText(listing.title);
        holder.mListingPrice.setText(listing.price);
        holder.mListingShopName.setText(listing.Shop.shop_name);

        if (isGooglePlayServicesAvailable) {
            holder.mPlusButton.setVisibility(View.VISIBLE);
            //TODO: instead of using REQUEST_CODE, use OnPlusOneClickListener
            holder.mPlusButton.initialize(listing.url, REQUEST_CODE_PLUS_ONE_BUTTON);
            holder.mShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent plusShareIntent = new PlusShare.Builder(mMainActivity)
                            .setType("text/plain")
                            .setText(String.format(
                                    "I recommend you to check it out %s at Etsy", listing.title))
                            .setContentUrl(Uri.parse(listing.url))
                            .getIntent();

                    mMainActivity.startActivityForResult(plusShareIntent,
                            REQUEST_CODE_SHARE_BUTTON);
                }
            });
        } else {
            holder.mPlusButton.setVisibility(View.GONE);

            holder.mShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                    } else {
                        //noinspection deprecation
                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    }
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, String.format(
                            "I recommend you to check it out %s at Etsy by this link %s",
                            listing.title, listing.url));

                    mMainActivity.startActivityForResult(Intent.createChooser(shareIntent, "Share"),
                            REQUEST_CODE_SHARE_BUTTON);
                }
            });
        }

        Picasso.with(holder.mListingImage.getContext())
                .load(listing.Images[0].url_570xN)
                .into(holder.mListingImage);
    }

    @Override
    public int getItemCount() {
        if (mActiveListings == null || mActiveListings.results == null) {
            return 0;
        }

        return mActiveListings.results.length;
    }

    @Override
    public void success(ActiveListings activeListings, Response response) {
        mActiveListings = activeListings;
        notifyDataSetChanged();
        //TODO: use otto to notify
        mMainActivity.showList();
    }

    @Override
    public void failure(RetrofitError error) {
        Timber.e(error, "Failed to load data from API");
        //TODO: use otto to notify
        mMainActivity.showError("Failed to load data from API");
    }

    public ActiveListings getActiveListings() {
        return mActiveListings;
    }

    @Override
    public void onConnected() {
        if (getItemCount() == 0) {
            Etsy.getActiveListings(this);
        }

        isGooglePlayServicesAvailable = true;
        notifyDataSetChanged();
    }

    @Override
    public void onDisconnected() {
        if (getItemCount() == 0) {
            Etsy.getActiveListings(this);
        }

        isGooglePlayServicesAvailable = false;
        notifyDataSetChanged();
    }

    public class ListingHolder extends RecyclerView.ViewHolder {
        //TODO: use ButterKnife to inject views
        public ImageView mListingImage;
        public TextView mListingTitle;
        public TextView mListingShopName;
        public TextView mListingPrice;
        public PlusOneButton mPlusButton;
        public ImageButton mShareButton;

        public ListingHolder(View itemView) {
            super(itemView);
            mListingImage = (ImageView) itemView.findViewById(R.id.listing_image);
            mListingTitle = (TextView) itemView.findViewById(R.id.listing_title);
            mListingShopName = (TextView) itemView.findViewById(R.id.listing_shop_name);
            mListingPrice = (TextView) itemView.findViewById(R.id.listing_shop_price);
            mPlusButton = (PlusOneButton) itemView.findViewById(R.id.listing_plus_button);
            mShareButton = (ImageButton) itemView.findViewById(R.id.listing_share_button);
        }
    }
}
