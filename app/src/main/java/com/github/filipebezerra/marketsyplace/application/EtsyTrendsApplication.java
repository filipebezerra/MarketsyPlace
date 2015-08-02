package com.github.filipebezerra.marketsyplace.application;

import android.app.Application;
import com.github.filipebezerra.marketsyplace.BuildConfig;
import timber.log.Timber;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 01/08/2015
 * @since #
 */
public class EtsyTrendsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initializeLogging();
    }

    private void initializeLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            //TODO: for release, plants Crashlytics or Parse.Analytics
        }
    }
}
