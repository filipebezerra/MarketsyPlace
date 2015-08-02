package com.github.filipebezerra.marketsyplace.api;

import com.github.filipebezerra.marketsyplace.model.ActiveListings;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 01/08/2015
 * @since #
 */
public class Etsy {
    private static final String API_KEY = " zp7z2eo4lw48q00ksy3eztd5";
    private static final String ENDPOINT = "https://openapi.etsy.com/v2";

    private static RequestInterceptor getInterceptor() {
        //TODO: use dagger to inject this
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addEncodedQueryParam("api_key", API_KEY);
            }
        };
    }

    private static Api getApi() {
        //TODO: use dagget to inject this
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setRequestInterceptor(getInterceptor())
                .build()
                .create(Api.class); //TODO: use dagger to inject this
    }

    public static void getActiveListings(Callback<ActiveListings> callback) {
        getApi().activeListings("Shop,Images", callback);
    }
}
