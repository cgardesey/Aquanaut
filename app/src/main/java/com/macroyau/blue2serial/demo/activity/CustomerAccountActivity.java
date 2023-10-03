package com.macroyau.blue2serial.demo.activity;
import static com.macroyau.blue2serial.demo.activity.TerminalActivity.signOut;
import static com.macroyau.blue2serial.demo.constants.Const.API_URL;
import static com.macroyau.blue2serial.demo.constants.Const.isNetworkAvailable;
import static com.macroyau.blue2serial.demo.constants.Const.myVolleyError;
import static com.macroyau.blue2serial.demo.fragment.CustomerAccountFragment1.address;
import static com.macroyau.blue2serial.demo.fragment.CustomerAccountFragment1.district;
import static com.macroyau.blue2serial.demo.fragment.CustomerAccountFragment1.firstName;
import static com.macroyau.blue2serial.demo.fragment.CustomerAccountFragment1.locality;
import static com.macroyau.blue2serial.demo.fragment.CustomerAccountFragment1.lastName;
import static com.macroyau.blue2serial.demo.fragment.CustomerAccountFragment1.phone;
import static com.macroyau.blue2serial.demo.receiver.NetworkReceiver.*;

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
import com.macroyau.blue2serial.demo.fragment.CustomerAccountFragment1;
import com.macroyau.blue2serial.demo.other.InitApplication;
import com.macroyau.blue2serial.demo.pagerAdapter.CustomerAccountPageAdapter;
import com.macroyau.blue2serial.demo.realm.RealmCustomer;
import com.macroyau.blue2serial.demo.receiver.NetworkReceiver;
import com.macroyau.blue2serial.demo.util.RealmUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

@SuppressWarnings("HardCodedStringLiteral")
public class CustomerAccountActivity extends PermisoActivity {

    public static RealmCustomer realmCustomer = new RealmCustomer();
    static Context context;

    boolean close = false;
    ViewPager mViewPager;
    CustomerAccountPageAdapter customerAccountPageAdapter;
    FloatingActionButton fab;

    RelativeLayout rootview;
    ProgressBar progressBar;
    String tag1 = "android:switcher:" + R.id.pageques + ":" + 0;
    //    String tag2 = "android:switcher:" + R.id.pageques + ":" + 1;
    NetworkReceiver networkReceiver;
    String customer_id;
    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        networkReceiver = new NetworkReceiver();
        Permiso.getInstance().setActivity(this);

        setContentView(R.layout.activity_customer_account);

        dialog = new ProgressDialog(this);
        dialog.setTitle(getString(R.string.updating_profile));
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);

        fab = findViewById(R.id.done);
        fab.setOnClickListener(v -> sendData());

        rootview = findViewById(R.id.root);

        progressBar = findViewById(R.id.pbar_pic);
        customerAccountPageAdapter = new CustomerAccountPageAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.pageques);
        mViewPager.setAdapter(customerAccountPageAdapter);
//        mViewPager.setOffscreenPageLimit(1);
        progressBar.setVisibility(View.GONE);
        if (getIntent().getStringExtra("MODE").equals("EDIT")) {
            if (realmCustomer != null) {
                customer_id = String.valueOf(realmCustomer.getId());
            }
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

        final CustomerAccountFragment1 tabFrag1 = (CustomerAccountFragment1) getSupportFragmentManager().findFragmentByTag(tag1);
//        final AccountFragment2 tabFrag2 = (AccountFragment2) getSupportFragmentManager().findFragmentByTag(tag2);

        Realm.init(context);
        Realm.getInstance(RealmUtility.getDefaultConfig(CustomerAccountActivity.this)).executeTransaction(realm -> {
            if (tabFrag1 != null) {

                if (tabFrag1.validate()) {
                    if (isNetworkAvailable(CustomerAccountActivity.this)) {
                        dialog.show();
                        saveCustomer();
                    } else {
                        Toast.makeText(CustomerAccountActivity.this, getString(R.string.internet_connection_is_needed), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CustomerAccountActivity.this, getString(R.string.pls_correct_the_errors), Toast.LENGTH_SHORT).show();
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
            CustomerAccountActivity.this.onBackPressed();

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
                            saveCustomer();
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

    private void saveCustomer() {
        int method = Request.Method.POST;
        String url = API_URL + "customers/";
        dialog.setTitle("Adding user...");
        if (getIntent().getStringExtra("MODE").equals("EDIT")) {
            dialog.setTitle(getString(R.string.updating_profile));
            method = Request.Method.PATCH;
            url = API_URL + "customers/" + customer_id + "/";
        }

        StringRequest stringRequest = new StringRequest(
                method,
                url,
                response -> {
                    if (response != null) {
                        try {
                            dialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            Realm.init(CustomerAccountActivity.this);
                            Realm.getInstance(RealmUtility.getDefaultConfig(CustomerAccountActivity.this)).executeTransaction(realm -> {
                                realmCustomer = realm.createOrUpdateObjectFromJson(RealmCustomer.class, jsonObject);
                                if (getIntent().getStringExtra("MODE").equals("EDIT")) {
                                    Toast.makeText(getApplicationContext(), "User details successfully edited!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "User successfully created!", Toast.LENGTH_SHORT).show();
                                }
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
                        myVolleyError(CustomerAccountActivity.this, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", firstName.getText().toString());
                params.put("last_name", lastName.getText().toString());
                params.put("district", district.getText().toString());
                params.put("locality", locality.getText().toString());
                params.put("address", address.getText().toString());
                params.put("phone", phone.getText().toString());
                params.put("company", PreferenceManager.getDefaultSharedPreferences(CustomerAccountActivity.this).getString("com.macroyau.blue2serial.demo" + "COMPANY_ID", ""));
                params.put("branch", PreferenceManager.getDefaultSharedPreferences(CustomerAccountActivity.this).getString("com.macroyau.blue2serial.demo" + "BRANCH_ID", ""));
                params.put("created_by", PreferenceManager.getDefaultSharedPreferences(CustomerAccountActivity.this).getString("com.macroyau.blue2serial.demo" + "USER_ID", ""));
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
