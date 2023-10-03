package com.macroyau.blue2serial.demo.activity;

import static com.macroyau.blue2serial.demo.activity.TerminalActivity.signOut;
import static com.macroyau.blue2serial.demo.constants.Const.API_URL;
import static com.macroyau.blue2serial.demo.constants.Const.isNetworkAvailable;
import static com.macroyau.blue2serial.demo.constants.Const.myVolleyError;
import static com.macroyau.blue2serial.demo.fragment.UserAccountFragment1.district;
import static com.macroyau.blue2serial.demo.fragment.UserAccountFragment1.dob;
import static com.macroyau.blue2serial.demo.fragment.UserAccountFragment1.email;
import static com.macroyau.blue2serial.demo.fragment.UserAccountFragment1.firstName;
import static com.macroyau.blue2serial.demo.fragment.UserAccountFragment1.ghana_card;
import static com.macroyau.blue2serial.demo.fragment.UserAccountFragment1.lastName;
import static com.macroyau.blue2serial.demo.fragment.UserAccountFragment1.phone;
import static com.macroyau.blue2serial.demo.receiver.NetworkReceiver.activeActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.greysonparrelli.permiso.Permiso;
import com.greysonparrelli.permiso.PermisoActivity;
import com.macroyau.blue2serial.demo.R;
import com.macroyau.blue2serial.demo.fragment.UserAccountFragment1;
import com.macroyau.blue2serial.demo.other.InitApplication;
import com.macroyau.blue2serial.demo.pagerAdapter.UserAccountPageAdapter;
import com.macroyau.blue2serial.demo.realm.RealmUser;
import com.macroyau.blue2serial.demo.receiver.NetworkReceiver;
import com.macroyau.blue2serial.demo.util.RealmUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

@SuppressWarnings("HardCodedStringLiteral")
public class UserAccountActivity extends PermisoActivity {

    public static RealmUser realmUser = new RealmUser();
    static Context context;

    boolean close = false;
    ViewPager mViewPager;
    UserAccountPageAdapter userAccountPageAdapter;
    FloatingActionButton fab;

    RelativeLayout rootview;
    ProgressBar progressBar;
    String tag1 = "android:switcher:" + R.id.pageques + ":" + 0;
    //    String tag2 = "android:switcher:" + R.id.pageques + ":" + 1;
    NetworkReceiver networkReceiver;
    String user_id;
    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        networkReceiver = new NetworkReceiver();
        Permiso.getInstance().setActivity(this);

        setContentView(R.layout.activity_user_account);

        dialog = new ProgressDialog(this);
        dialog.setTitle(getString(R.string.updating_profile));
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);

        fab = findViewById(R.id.done);
        fab.setOnClickListener(v -> sendData());

        rootview = findViewById(R.id.root);

        progressBar = findViewById(R.id.pbar_pic);
        userAccountPageAdapter = new UserAccountPageAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.pageques);
        mViewPager.setAdapter(userAccountPageAdapter);
//        mViewPager.setOffscreenPageLimit(1);
        progressBar.setVisibility(View.GONE);
        if (realmUser != null) {
            user_id = String.valueOf(realmUser.getId());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Permiso.getInstance().setActivity(this);
        activeActivity = this;
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void sendData() {

        final UserAccountFragment1 tabFrag1 = (UserAccountFragment1) getSupportFragmentManager().findFragmentByTag(tag1);
//        final AccountFragment2 tabFrag2 = (AccountFragment2) getSupportFragmentManager().findFragmentByTag(tag2);

        Realm.init(context);
        Realm.getInstance(RealmUtility.getDefaultConfig(UserAccountActivity.this)).executeTransaction(realm -> {
            if (tabFrag1 != null) {

                if (tabFrag1.validate()) {
                    if (isNetworkAvailable(UserAccountActivity.this)) {
                        dialog.show();
                        saveUser();
                    } else {
                        Toast.makeText(UserAccountActivity.this, getString(R.string.internet_connection_is_needed), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserAccountActivity.this, getString(R.string.pls_correct_the_errors), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showTwoButtonSnackbar() {

        // Create the Snackbar
        LinearLayout.LayoutParams objLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final Snackbar snackbar = Snackbar.make(rootview, "Exit?", Snackbar.LENGTH_INDEFINITE);

        // Get the Snackbar layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        // Inflate our courseListMaterialDialog viewBitmap bitmap = ((RoundedDrawable)profilePic.getDrawable()).getSourceBitmap();
        View snackView = getLayoutInflater().inflate(R.layout.snackbar, null);


        TextView textViewOne = snackView.findViewById(R.id.first_text_view);
        textViewOne.setText(this.getResources().getString(R.string.yes));
        textViewOne.setOnClickListener(v -> {
            snackbar.dismiss();
            close = true;
            UserAccountActivity.this.onBackPressed();

            //  finish();
        });

        final TextView textViewTwo = snackView.findViewById(R.id.second_text_view);

        textViewTwo.setText(this.getResources().getString(R.string.no));
        textViewTwo.setOnClickListener(v -> {
            Log.d("Deny", "showTwoButtonSnackbar() : deny clicked");
            snackbar.dismiss();


        });

        // Add our courseListMaterialDialog view to the Snackbar's layout
        layout.addView(snackView, objLayoutParams);

        // Show the Snackbar
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        if (close) {
            super.onBackPressed();
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
        showTwoButtonSnackbar();
    }

    private void refresh() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                API_URL + "auth/jwt/refresh",
                response -> {
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            PreferenceManager
                                    .getDefaultSharedPreferences(getApplicationContext())
                                    .edit()
                                    .putString("com.macroyau.blue2serial.demo" + "ACCESS", jsonObject.getString("access"))
                                    .apply();
                            saveUser();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    dialog.dismiss();
                    if (error instanceof AuthFailureError && error.networkResponse.statusCode == 401) {
                        signOut();
                    } else {
                        dialog.dismiss();
                        error.printStackTrace();
                        myVolleyError(activeActivity, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("refresh", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("com.macroyau.blue2serial.demo" + "REFRESH",  ""));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }

    private void saveUser() {

        StringRequest stringRequest = new StringRequest(
                Request.Method.PATCH,
                API_URL + "users/" + user_id + "/",
                response -> {
                    if (response != null) {
                        try {
                            dialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            Realm.init(UserAccountActivity.this);
                            Realm.getInstance(RealmUtility.getDefaultConfig(UserAccountActivity.this)).executeTransaction(realm -> {
                                realmUser = realm.createOrUpdateObjectFromJson(RealmUser.class, jsonObject);
                                Toast.makeText(getApplicationContext(), "User details successfully edited!", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    if (error instanceof AuthFailureError && error.networkResponse.statusCode == 401) {
                        refresh();
                    } else {
                        dialog.dismiss();
                        error.printStackTrace();
                        myVolleyError(UserAccountActivity.this, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", firstName.getText().toString());
                params.put("last_name", lastName.getText().toString());
                params.put("district", district.getText().toString());
                params.put("email", email.getText().toString());
                params.put("ghana_card", ghana_card.getText().toString());
                params.put("phone", phone.getText().toString());
                params.put("date_of_birth", dob.getText().toString());
                return params;
            }

            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("accept", "application/json");
                headers.put("Authorization", "JWT " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("com.macroyau.blue2serial.demo" + "ACCESS", ""));
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }
}
