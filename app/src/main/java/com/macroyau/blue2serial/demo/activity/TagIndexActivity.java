package com.macroyau.blue2serial.demo.activity;

import static com.macroyau.blue2serial.demo.activity.TerminalActivity.bluetoothSerial;
import static com.macroyau.blue2serial.demo.activity.TerminalActivity.serialSend;
import static com.macroyau.blue2serial.demo.activity.TerminalActivity.showDeviceListDialog;
import static com.macroyau.blue2serial.demo.activity.TerminalActivity.signOut;
import static com.macroyau.blue2serial.demo.activity.TerminalActivity.terminalActivity;
import static com.macroyau.blue2serial.demo.constants.Const.API_URL;
import static com.macroyau.blue2serial.demo.constants.Const.myVolleyError;
import static com.macroyau.blue2serial.demo.receiver.NetworkReceiver.activeActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.greysonparrelli.permiso.Permiso;
import com.greysonparrelli.permiso.PermisoActivity;
import com.macroyau.blue2serial.demo.R;
import com.macroyau.blue2serial.demo.adapter.TagIndexAdapter;
import com.macroyau.blue2serial.demo.materialDialog.StatusMaterialDialog;
import com.macroyau.blue2serial.demo.other.InitApplication;
import com.macroyau.blue2serial.demo.realm.RealmTag;
import com.macroyau.blue2serial.demo.receiver.NetworkReceiver;
import com.macroyau.blue2serial.demo.util.RealmUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

@SuppressWarnings("HardCodedStringLiteral")
public class TagIndexActivity extends PermisoActivity {

    public static RealmTag realmTag = new RealmTag();
    static Context context;
    static RecyclerView recyclerview;
    public static TextView connection_status;
    public  TextView name;
    NetworkReceiver networkReceiver;
    public static ProgressDialog dialog;
    public static String TAG_UID;
    public static String CLIENT;
    static TagIndexAdapter tagIndexAdapter;
    public static StatusMaterialDialog statusMaterialDialog = new StatusMaterialDialog();
    ImageView refresh, back, menu;
    PopupMenu popup;
    public static Activity tagIndexActivity;
    public static String NAME, CUSTOMER_ID, FIRST_NAME, LAST_NAME;

    public static String TAG_MODE;

    static ArrayList<RealmTag> tags = new ArrayList<>();
    static ArrayList<RealmTag> newTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        tagIndexActivity = this;

        networkReceiver = new NetworkReceiver();
        Permiso.getInstance().setActivity(this);

        setContentView(R.layout.activity_tag_index);

        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        menu = findViewById(R.id.menu);
        connection_status = findViewById(R.id.connection_status);
        refresh = findViewById(R.id.refresh);
        back = findViewById(R.id.back);
        recyclerview = findViewById(R.id.recyclerview);
        name = findViewById(R.id.name);
        back.setOnClickListener(v -> finish());
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                reloadTags();
            }
        });

        menu.setOnClickListener(v -> {

            popup = new PopupMenu(TagIndexActivity.this, menu);

            if (bluetoothSerial.isConnected()) {
                popup.inflate(R.menu.tag_index_connected_menu);
            } else {
                popup.inflate(R.menu.tag_index_disconnected_menu);
            }

            popup.setOnMenuItemClickListener(item -> {

                dialog = new ProgressDialog(TagIndexActivity.this);
                dialog.setMessage("Please wait...");
                dialog.setCancelable(false);
                dialog.setIndeterminate(true);
                int itemId = item.getItemId();
                if (itemId == R.id.action_connect) {
                    showDeviceListDialog(terminalActivity);
                    return true;
                } else if (itemId == R.id.action_disconnect) {
                    showDeviceListDialog(terminalActivity);
                    return true;
                } else if (itemId == R.id.action_sign_out) {
                    signOut();
                    return true;
                } else {
                    return false;
                }
            });

            popup.show();
        });

        name.setText(NAME);
        tagIndexAdapter = new TagIndexAdapter(new TagIndexAdapter.TagIndexAdapterInterface() {
            @Override
            public void onItemClick(ArrayList<RealmTag> realmTags, int position, TagIndexAdapter.ViewHolder holder) {

            }

            @Override
            public void onMenuClick(ArrayList<RealmTag> realmTags, int position, TagIndexAdapter.ViewHolder holder) {
                realmTag = realmTags.get(position);
                PopupMenu popup = new PopupMenu(TagIndexActivity.this, holder.menu);

                popup.inflate(R.menu.tag_menu);

                popup.setOnMenuItemClickListener(item -> {

                    dialog.setIndeterminate(true);
                    int itemId = item.getItemId();
                    if (itemId == R.id.status) {
                        if (statusMaterialDialog != null && statusMaterialDialog.isAdded()) {

                        } else {
                            statusMaterialDialog.setStatus(realmTag.getStatus());
                            statusMaterialDialog.setCancelable(false);
                            statusMaterialDialog.show(getSupportFragmentManager(), "statusMaterialDialog");
                            statusMaterialDialog.setCancelable(true);
                        }
                        return true;
                    }
                    else if (itemId == R.id.reset_tag) {
                        deleteTag();
                        return true;
                    }
                    else if (itemId == R.id.balance) {
                        //fetch balance from api and display in a material dialog.

                        return true;
                    }
                    else {
                        return false;
                    }

                });
                popup.show();
            }
        }, TagIndexActivity.this, tags);
        recyclerview.setLayoutManager(new LinearLayoutManager(TagIndexActivity.this));
        recyclerview.setNestedScrollingEnabled(false);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(tagIndexAdapter);


        if (TerminalActivity.connection_status.getText().toString().toLowerCase().contains("disconnected")) {
            TagIndexActivity.connection_status.setText(Html.fromHtml("<font color='red'><b>Disconnected from Writer</b></font>"));
        }
        else {
            TagIndexActivity.connection_status.setText(Html.fromHtml("<font color='#006400'><b>Connected to Writer</b></font>"));
        }

        /*try {
            String message = new JSONObject()
                    .put("type", "register")
                    .put("tag_id", "0465346")
                    .put("customer_id", "")
                    .put("first_name", "")
                    .put("last_name", "")
                    .toString();
            bluetoothSerialRead(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        Permiso.getInstance().setActivity(this);
        activeActivity = this;
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        populateTags(TagIndexActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
    }

    private static void refresh(String status) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                API_URL + "auth/jwt/refresh",
                response -> {
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            PreferenceManager
                                    .getDefaultSharedPreferences(activeActivity)
                                    .edit()
                                    .putString("com.macroyau.blue2serial.demo" + "ACCESS", jsonObject.getString("access"))
                                    .apply();
                            saveTag(status);
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
                params.put("refresh", PreferenceManager.getDefaultSharedPreferences(activeActivity).getString("com.macroyau.blue2serial.demo" + "REFRESH",  ""));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }

    public static void saveTag(String status) {
        final String[] tag_uid = {realmTag.getTag_uid()};

        final String[] client = {realmTag.getClient()};

        int method = Request.Method.POST;
        String url = API_URL + "tags/";
        if (realmTag.getId() > 0) {
            method = Request.Method.PATCH;
            url = API_URL + "tags/" + realmTag.getId() + "/";
        }

        StringRequest stringRequest = new StringRequest(
                method,
                url,
                response -> {
                    if (response != null) {
                        dialog.dismiss();
                        serialSend("register*" + String.valueOf(realmTag.getClient()) + "#" + FIRST_NAME + "$" + LAST_NAME);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Realm.init(activeActivity);
                            Realm.getInstance(RealmUtility.getDefaultConfig(activeActivity)).executeTransaction(realm -> {
                                realm.createOrUpdateObjectFromJson(RealmTag.class, jsonObject);
                                if (TAG_MODE != null && TAG_MODE.equals("ADD")) {
                                    Toast.makeText(activeActivity, "Tag successfully created!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activeActivity, "Tag details successfully edited!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            populateTags(activeActivity);

                            if (statusMaterialDialog.isAdded()) statusMaterialDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                error -> {
                    if (error instanceof AuthFailureError && error.networkResponse.statusCode == 401) {
                        refresh(status);
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
                params.put("tag_uid", tag_uid[0]);
                params.put("client", client[0]);
                params.put("company", PreferenceManager.getDefaultSharedPreferences(activeActivity).getString("com.macroyau.blue2serial.demo" + "COMPANY_ID", ""));
                params.put("status", status);
                return params;
            }

            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("accept", "application/json");
                headers.put("Authorization", "JWT " + PreferenceManager.getDefaultSharedPreferences(activeActivity).getString("com.macroyau.blue2serial.demo" + "ACCESS", ""));
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }

    public static void deleteTag() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE,
                API_URL + "tags/" + realmTag.getId() + "/",
                response -> {
                    if (true) {
                        dialog.dismiss();
                        serialSend("reset*");
                        Toast.makeText(activeActivity, "Tag successfully deleted!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error instanceof AuthFailureError && error.networkResponse.statusCode == 401) {
                        deleteTagRefresh();
                    } else {
                        dialog.dismiss();
                        error.printStackTrace();
                        myVolleyError(activeActivity, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("accept", "application/json");
                headers.put("Authorization", "JWT " + PreferenceManager.getDefaultSharedPreferences(activeActivity).getString("com.macroyau.blue2serial.demo" + "ACCESS", ""));
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }

    public static void populateTags(final Activity context) {
        Realm.init(context);
        Realm.getInstance(RealmUtility.getDefaultConfig(context)).executeTransaction(realm -> {

            RealmResults<RealmTag> results = realm.where(RealmTag.class).findAll();

            newTags.clear();
            for (RealmTag realmTag : results) {
                realmTag.setClient(CUSTOMER_ID);
                newTags.add(realmTag);
            }
            tags.clear();
            tags.addAll(newTags);
            tagIndexAdapter.notifyDataSetChanged();
        });
    }

    private void reloadTags() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                API_URL + "customers/" + CUSTOMER_ID,
                response -> {
                    if (response != null) {
                        try {
                            dialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            Realm.init(TagIndexActivity.this);
                            Realm.getInstance(RealmUtility.getDefaultConfig(TagIndexActivity.this)).executeTransaction(realm -> {
                                try {
                                    realm.where(RealmTag.class).equalTo("client", CUSTOMER_ID).findAll().deleteAllFromRealm();
                                    realm.createOrUpdateAllFromJson(RealmTag.class, jsonObject.getJSONArray("tag_client"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                            populateTags(TagIndexActivity.this);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    if (error instanceof AuthFailureError && error.networkResponse.statusCode == 401) {
                        reloadTagsRefresh();
                    } else {
                        dialog.dismiss();
                        error.printStackTrace();
                        myVolleyError(TagIndexActivity.this, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("accept", "application/json");
                headers.put("Authorization", "JWT " + PreferenceManager.getDefaultSharedPreferences(TagIndexActivity.this).getString("com.macroyau.blue2serial.demo" + "ACCESS", ""));
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }

    private void reloadTagsRefresh() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                API_URL + "auth/jwt/refresh",
                response -> {
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            PreferenceManager
                                    .getDefaultSharedPreferences(TagIndexActivity.this)
                                    .edit()
                                    .putString("com.macroyau.blue2serial.demo" + "ACCESS", jsonObject.getString("access"))
                                    .apply();
                            reloadTags();
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
                        myVolleyError(TagIndexActivity.this, error);
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

    private static void deleteTagRefresh() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                API_URL + "auth/jwt/refresh",
                response -> {
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            PreferenceManager
                                    .getDefaultSharedPreferences(activeActivity)
                                    .edit()
                                    .putString("com.macroyau.blue2serial.demo" + "ACCESS", jsonObject.getString("access"))
                                    .apply();
                            deleteTag();
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
                params.put("refresh", PreferenceManager.getDefaultSharedPreferences(activeActivity).getString("com.macroyau.blue2serial.demo" + "REFRESH",  ""));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }
}
