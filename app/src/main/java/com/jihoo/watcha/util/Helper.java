package com.jihoo.watcha.util;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jihoo.watcha.MainActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Helper {
    public static ArrayList<String> getSavedUrls() {
        ArrayList<String> urls = new ArrayList<>();
        // get saved urls
        Gson gson = new Gson();
        String json = MainActivity.sharedPreferences.getString("urls", "");
        if(!json.isEmpty()) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            urls = gson.fromJson(json, type);
        }
        return urls;
    }

    public static void addUrl(ArrayList<String> urls) {
        Gson gson = new Gson();
        String json = gson.toJson(urls);
        SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
        editor.putString("urls", json);
        editor.apply();
    }

    public static String splitString(String url) {
        String[] results = url.split("\\?");
        return results[0];
    }
}
