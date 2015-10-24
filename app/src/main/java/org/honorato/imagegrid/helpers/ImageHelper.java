package org.honorato.imagegrid.helpers;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.honorato.imagegrid.BuildConfig;
import org.honorato.imagegrid.api.ApiManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jlh on 10/7/15.
 */
public class ImageHelper {
    public static final String TAG = ImageHelper.class.getSimpleName();

    /**
     * Process an API result and return a list of string urls
     * @param result
     * @return
     */
    public static List<String> getUrlsFromApiResult(JsonObject result) {
        if (result == null) {
            return new ArrayList<>();
        }
        JsonArray resultsArray = result
                .get("responseData").getAsJsonObject()
                .get("results").getAsJsonArray();

        List<String> urls = new ArrayList<>(resultsArray.size());
        for (int i = 0; i < resultsArray.size(); i++) {
            JsonObject jsonObj = resultsArray.get(i).getAsJsonObject();
            urls.add(jsonObj.get("url").getAsString());
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Got " + urls.size() + " results");
        }

        return urls;
    }

    public static String getNextPage(JsonObject result) {
        if (result == null) {
            return null;
        }
        if (BuildConfig.DEBUG) {
            ApiManager.prettyPrint(result.get("responseData").getAsJsonObject().get("cursor").getAsJsonObject());
        }
        JsonObject cursor = result.get("responseData").getAsJsonObject()
                .get("cursor").getAsJsonObject();
        JsonArray pages = cursor.get("pages").getAsJsonArray();
        int current = cursor.get("currentPageIndex").getAsInt();
        if (current < (pages.size() - 1)) {
            return pages.get(current + 1).getAsJsonObject()
                    .get("start").getAsString();
        }

        return null;
    }
}
