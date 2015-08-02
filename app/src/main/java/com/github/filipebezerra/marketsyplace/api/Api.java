package com.github.filipebezerra.marketsyplace.api;

import com.github.filipebezerra.marketsyplace.model.ActiveListings;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 01/08/2015
 * @since #
 */
public interface Api {
    @GET("/listings/active")
    void activeListings(@Query("includes") String includes, Callback<ActiveListings> callback);
}
