package com.github.filipebezerra.marketsyplace.playservices;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.Plus.PlusOptions;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 01/08/2015
 * @since #
 */
public class GooglePlayServicesHelper implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = GooglePlayServicesHelper.class.getName();

    private static final int REQUEST_CODE_RESOLUTION = 100;
    private static final int REQUEST_CODE_AVAILABILITY = -101;

    private final GooglePlayServicesListener mListener;
    private final Activity mActivity;
    private final GoogleApiClient mApiClient;

    private static final String PLUS_CLIENT_ID =
            "790912117281-lp2rfb666vji3jermi6kri342ctjuvs5.apps.googleusercontent.com";

    //TODO: use otto to notify
    public interface GooglePlayServicesListener {
        void onConnected();
        void onDisconnected();
    }

    public GooglePlayServicesHelper(@NonNull Activity activityontext,
            @NonNull GooglePlayServicesListener listener) {
        mListener = listener;
        mActivity = activityontext;
        //TODO: use dagger to injects GoogleApiClient
        mApiClient = new GoogleApiClient.Builder(activityontext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, PlusOptions.builder()
                        .setServerClientId(PLUS_CLIENT_ID)
                        .build())
                .build();
        Timber.tag(TAG);
    }

    public void connect() {
        if (isGooglePlayServicesAvailable()) {
            mApiClient.connect();
        } else {
            mListener.onDisconnected();
        }
    }

    public void disconnect() {
        //TODO: is really necessary notify failure when I saying to disconnect?
        if (isGooglePlayServicesAvailable()) {
            if (mApiClient.isConnected()) {
                mApiClient.disconnect();
            }
        } else {
            mListener.onDisconnected();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        final int availability = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);

        switch (availability) {
            case ConnectionResult.SUCCESS:
                return true;
            case ConnectionResult.SERVICE_DISABLED:
            case ConnectionResult.SERVICE_INVALID:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                GooglePlayServicesUtil.getErrorDialog(availability, mActivity,
                        REQUEST_CODE_AVAILABILITY).show();
                return false;
            default:
                return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mListener.onConnected();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mListener.onDisconnected();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(mActivity, REQUEST_CODE_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
                Timber.e(e, "Error trying to start connection failed resolution");
                connect();
            }
        } else {
            mListener.onDisconnected();
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_RESOLUTION || requestCode == REQUEST_CODE_AVAILABILITY) {
            if (resultCode == RESULT_OK) {
                connect();
            } else {
                mListener.onDisconnected();
            }
        }
    }
}
