package com.macroyau.blue2serial.demo.util;

import static com.macroyau.blue2serial.demo.constants.Const.API_URL;

import android.app.Activity;
import android.app.ProgressDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.macroyau.blue2serial.demo.other.InitApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RefreshToken {

    Activity activity;
    OnResponseInterface onResponseInterface;
    OnErrorInterface onErrorInterface;

    public RefreshToken(Activity activity, OnResponseInterface onResponseInterface, OnErrorInterface onErrorInterface) {
        this.activity = activity;
        this.onResponseInterface = onResponseInterface;
        this.onErrorInterface = onErrorInterface;
    }

    public void getNewToken(String id, ProgressDialog dialog) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject()
                    .put("username", "android_user")
                    .put("password", "test1234");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                API_URL + "auth/jwt/create",
                jsonObject,
                response -> {
                    OnResponseInterface.onResponse(response);
                },
                error -> {
                    OnErrorInterface.onError(error);
                }
        );
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public interface OnResponseInterface {
        static void onResponse(JSONObject response) {
        }
    }

    public interface OnErrorInterface {
        static void onError(VolleyError error) {

        }
    }
}
