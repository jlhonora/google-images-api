package org.honorato.imagegrid.api;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.honorato.imagegrid.BuildConfig;

/**
 * Created by jlh on 10/7/15.
 */
public class ApiManager {
    public static final String TAG = ApiManager.class.getSimpleName();
    public static final String BASE_URL = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0";

    Context mCtx;
    public ApiManager(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Fetch images from the API using a callback
     * @param query The query string
     * @param handler A callback for success and error
     */
    public void fetch(String query, ResponseHandler handler) {
        fetch(query, null, handler);
    }

    public void fetch(String query, String start, ResponseHandler handler) {
        String url = BASE_URL + "&imgsz=medium&rsz=8&q=" + Uri.encode(query);
        if (start != null) {
            url += "&start=" + start;
        }
        performRequest(url, handler);
    }

    /**
     * Perform a request with a callback
     * @param url
     * @param handler
     */
    protected void performRequest(final String url, final ResponseHandler handler) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Performing request for url " + url);
        }
        Ion.with(mCtx)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "Failed!");
                                e.printStackTrace();
                            }
                            handler.onFailure(e);
                        } else {
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "Success");
                                prettyPrint(result);
                            }
                            handler.onSuccess(result);
                        }
                    }
                });
    }

    public static void prettyPrint(JsonObject object) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Log.d(TAG, "" + gson.toJson(object));
    }

    public static class ResponseHandler {
        public void onSuccess(JsonObject object) {

        }

        public void onFailure(Exception e) {

        }
    }
}
