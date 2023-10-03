package com.macroyau.blue2serial.demo.activity;

import static com.macroyau.blue2serial.demo.activity.CustomerAccountActivity.realmCustomer;
import static com.macroyau.blue2serial.demo.activity.TagIndexActivity.*;
import static com.macroyau.blue2serial.demo.constants.Const.API_URL;
import static com.macroyau.blue2serial.demo.constants.Const.myVolleyError;
import static com.macroyau.blue2serial.demo.receiver.NetworkReceiver.activeActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.greysonparrelli.permiso.PermisoActivity;
import com.macroyau.blue2serial.BluetoothDeviceListDialog;
import com.macroyau.blue2serial.BluetoothSerial;
import com.macroyau.blue2serial.BluetoothSerialListener;
import com.macroyau.blue2serial.demo.R;
import com.macroyau.blue2serial.demo.adapter.CustomerIndexAdapter;
import com.macroyau.blue2serial.demo.materialDialog.BranchMaterialDialog;
import com.macroyau.blue2serial.demo.materialDialog.CompanyMaterialDialog;
import com.macroyau.blue2serial.demo.materialDialog.TopupMaterialDialog;
import com.macroyau.blue2serial.demo.other.InitApplication;
import com.macroyau.blue2serial.demo.realm.RealmBalance;
import com.macroyau.blue2serial.demo.realm.RealmCustomer;
import com.macroyau.blue2serial.demo.realm.RealmTag;
import com.macroyau.blue2serial.demo.realm.RealmUser;
import com.macroyau.blue2serial.demo.receiver.NetworkReceiver;
import com.macroyau.blue2serial.demo.util.RealmUtility;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * This is an example Bluetooth terminal application built using the Blue2Serial library.
 *
 * @author Macro Yau
 */
public class TerminalActivity extends PermisoActivity implements BluetoothSerialListener, BluetoothDeviceListDialog.OnDeviceSelectedListener {

    public static final int REQUEST_ENABLE_BLUETOOTH = 1;

    public static BluetoothSerial bluetoothSerial;

    private MenuItem actionConnect, actionDisconnect;

    private static LinearLayout swipe_load;

    PopupMenu popup;

    private boolean crlf = false;

    static RecyclerView recyclerview;
    static TextView no_data;
    static CustomerIndexAdapter customerIndexAdapter;
    static ArrayList<RealmCustomer> cartArrayList = new ArrayList<>();
    static ArrayList<RealmCustomer> newCart = new ArrayList<>();
    public static Activity activity, terminalActivity;
    FloatingActionButton fab;
    public TopupMaterialDialog topupMaterialDialog = new TopupMaterialDialog();
    public BranchMaterialDialog branchMaterialDialog = new BranchMaterialDialog();
    public CompanyMaterialDialog companyMaterialDialog = new CompanyMaterialDialog();
    public ProgressDialog dialog;
    ImageView menu, searchIcon;
    public static TextView connection_status;
    EditText search;
    String received, to_send;
    AlertDialog writer_dialog;
    NetworkReceiver networkReceiver;

    public TerminalActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        activity = this;
        terminalActivity = this;

        networkReceiver = new NetworkReceiver();

        /*writer_dialog = new AlertDialog.Builder(TerminalActivity.this)
                .setTitle(Const.toTitleCase("Connect to Writer"))
                .setMessage("You must be connected to the writer in order to regiser customers and top up balance for customers.")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Connect", (dialog, which) -> {
                    showDeviceListDialog();
                })
                .setCancelable(false)
                .show();*/

        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait");
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);

        recyclerview = findViewById(R.id.recyclerview);

        no_data = findViewById(R.id.no_data);
        fab = findViewById(R.id.fab);
        menu = findViewById(R.id.menu);
        searchIcon = findViewById(R.id.searchIcon);
        connection_status = findViewById(R.id.connection_status);
        swipe_load = findViewById(R.id.swipe_load);
        search = findViewById(R.id.search);


        customerIndexAdapter = new CustomerIndexAdapter(new CustomerIndexAdapter.CustomerIndexAdapterInterface() {

            @Override
            public void onItemClick(ArrayList<RealmCustomer> realmCustomers, int position, CustomerIndexAdapter.ViewHolder holder) {

            }

            @Override
            public void onImageClick(ArrayList<RealmCustomer> realmCustomers, int position, CustomerIndexAdapter.ViewHolder holder) {

            }

            @Override
            public void onMenuClick(ArrayList<RealmCustomer> realmCustomers, int position, CustomerIndexAdapter.ViewHolder holder) {
                final RealmCustomer[] realmCustomer = {realmCustomers.get(position)};
                PopupMenu popup = new PopupMenu(TerminalActivity.this, holder.menu);

                popup.inflate(R.menu.customer_menu);

                popup.setOnMenuItemClickListener(item -> {

                    dialog = new ProgressDialog(TerminalActivity.this);
                    dialog.setMessage("Please wait...");
                    dialog.setCancelable(false);
                    dialog.setIndeterminate(true);
                    int itemId = item.getItemId();
                    if (itemId == R.id.tags) {
                        CLIENT = String.valueOf(realmCustomer[0].getId());
                        NAME = realmCustomer[0].getFirst_name() + " " + realmCustomer[0].getLast_name();
                        dialog.show();
                        goToTags(String.valueOf(realmCustomer[0].getId()), realmCustomer[0].getFirst_name(), realmCustomer[0].getLast_name());
                        return true;
                    } else if (itemId == R.id.edit) {
                        CustomerAccountActivity.realmCustomer = realmCustomer[0];
                        startActivity(
                                new Intent(TerminalActivity.this, CustomerAccountActivity.class)
                                        .putExtra("MODE", "EDIT")
                        );
                        return true;
                    } else if (itemId == R.id.balance) {
                        dialog.show();
                        checkStatus(String.valueOf(realmCustomer[0].getId()), realmCustomer[0].getFirst_name() + " " + realmCustomer[0].getLast_name());
                        return true;
                    }
                    else if (itemId == R.id.reset_tag) {
                        serialSend("reset*");
                        Toast.makeText(activeActivity, "Tag successfully deleted!", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    else {
                        return false;
                    }
                });
                popup.show();
            }
        }, TerminalActivity.this, cartArrayList, true);
        recyclerview.setLayoutManager(new LinearLayoutManager(TerminalActivity.this));
        recyclerview.setNestedScrollingEnabled(false);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(customerIndexAdapter);

        recyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
//                    Toast.makeText(TerminalActivity.this, "Last", Toast.LENGTH_SHORT).show();
                    dialog.show();
                    fetchCustomers();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        fab.setOnClickListener(v -> {
            //startActivity(new Intent(TerminalActivity.this, CustomerAccountActivity.class));
            realmCustomer = null;
            startActivity(
                    new Intent(TerminalActivity.this, CustomerAccountActivity.class)
                            .putExtra("MODE", "ADD")
            );
        });

        bluetoothSerial = new BluetoothSerial(this, this);


        menu.setOnClickListener(v -> {

            popup = new PopupMenu(TerminalActivity.this, menu);

            if (bluetoothSerial.isConnected()) {
                popup.inflate(R.menu.customer_index_connected_menu);
            } else {
                popup.inflate(R.menu.customer_index_disconnected_menu);
            }

            popup.setOnMenuItemClickListener(item -> {

                dialog = new ProgressDialog(TerminalActivity.this);
                dialog.setMessage("Please wait...");
                dialog.setCancelable(false);
                dialog.setIndeterminate(true);

                int itemId = item.getItemId();
                if (itemId == R.id.action_connect) {
                    showDeviceListDialog(terminalActivity);
                    return true;
                } else if (itemId == R.id.action_disconnect) {
                    bluetoothSerial.stop();
                    return true;
                } else if (itemId == R.id.action_reset_username) {
                    return true;
                } else if (itemId == R.id.action_reset_password) {
                    return true;
                } else if (itemId == R.id.action_reset_password) {
                    return true;
                } else if (itemId == R.id.action_profile) {
                    dialog.show();
                    fetchUser();
                    return true;
                } else if (itemId == R.id.action_company) {
                    dialog.show();
                    companyInfo();
                    return true;
                } else if (itemId == R.id.action_branch) {
                    dialog.show();
                    branchInfo();
                    return true;
                } else if (itemId == R.id.action_sign_out) {
                    signOut();
                    return true;
                } else if (itemId == R.id.reset_tag) {
                    serialSend("reset*");
                    Toast.makeText(activeActivity, "Tag successfully deleted!", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else {
                    return false;
                }
            });

            popup.show();
        });

        connection_status.setText(Html.fromHtml("<font color='red'><b>Disconnected from Writer</b></font>"));
        if (activeActivity instanceof TagIndexActivity) {
            TagIndexActivity.connection_status.setText(Html.fromHtml("<font color='red'><b>Disconnected from Writer</b></font>"));
        }
        // Check Bluetooth availability on the device and set up the Bluetooth adapter
        bluetoothSerial.setup();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(cartArrayList, s.toString());
                customerIndexAdapter.setFilter(filter(cartArrayList, s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("com.macroyau.blue2serial.demo" + "TERMINAL_ACTIVITY_TIPS_DISMISSED", false)) {
            ViewTreeObserver vto = menu.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        menu.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        menu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    // make an target
                    SimpleTarget firstTarget = new SimpleTarget.Builder(TerminalActivity.this).setPoint(menu)
                            .setRadius(150F)
                            .setTitle("Menu Tip")
                            .setDescription("You can click on the menu icon to see menu actions\n\nClick anywhere on the screen to dismiss tip")
                            .build();

                    SimpleTarget secondTarget = new SimpleTarget.Builder(TerminalActivity.this).setPoint(fab)
                            .setRadius(150F)
                            .setTitle("Register Tip")
                            .setDescription("You can click on the add button to register a customer\n\nClick anywhere on the screen to dismiss tip")
                            .build();

                    Spotlight.with(TerminalActivity.this)
//                .setOverlayColor(ContextCompat.getColor(getActivity(), R.color.background))
                            .setDuration(250L)
                            .setAnimation(new DecelerateInterpolator(2f))
                            .setTargets(firstTarget, secondTarget)
                            .setClosedOnTouchedOutside(true)
                            .setOnSpotlightEndedListener(() -> {
                            })
                            .setOnSpotlightStartedListener(() -> PreferenceManager
                                    .getDefaultSharedPreferences(getApplicationContext())
                                    .edit()
                                    .putBoolean("com.macroyau.blue2serial.demo" + "TERMINAL_ACTIVITY_TIPS_DISMISSED", true)
                                    .apply())
                            .start();

                }
            });
        }

        /*try {
            String message = new JSONObject()
                    .put("type", "topup")
                    .put("tag_id", "df09ef3c")
                    .put("customer_id", "50")
                    .put("first_name", "")
                    .put("last_name", "")
                    .put("balance", 100)
                    .toString();
            bluetoothSerialRead(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        /*try {
            String message = new JSONObject()
                    .put("type", "register")
                    .put("tag_id", "df910e04")
                    .put("customer_id", "")
                    .put("first_name", "")
                    .put("last_name", "")
                    .put("balance", "")
                    .toString();
            bluetoothSerialRead(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    private void branchInfo() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                API_URL + "branchs/" + PreferenceManager.getDefaultSharedPreferences(TerminalActivity.this).getString("com.macroyau.blue2serial.demo" + "BRANCH_ID", ""),
                response -> {
                    if (response != null) {
                        try {
                            dialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);

                            if (branchMaterialDialog != null && branchMaterialDialog.isAdded()) {

                            } else {
                                branchMaterialDialog.setBranch_name(jsonObject.getString("name"));
                                branchMaterialDialog.setLocation_lat(jsonObject.getDouble("location_lat"));
                                branchMaterialDialog.setLocation_long(jsonObject.getDouble("location_long"));
                                branchMaterialDialog.setCancelable(false);
                                branchMaterialDialog.show(getSupportFragmentManager(), "branchMaterialDialog");
                                branchMaterialDialog.setCancelable(true);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    if (error instanceof AuthFailureError && error.networkResponse.statusCode == 401) {
                        branchInfoRefresh();
                    } else {
                        dialog.dismiss();
                        error.printStackTrace();
                        myVolleyError(TerminalActivity.this, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("accept", "application/json");
                headers.put("Authorization", "JWT " + PreferenceManager.getDefaultSharedPreferences(TerminalActivity.this).getString("com.macroyau.blue2serial.demo" + "ACCESS", ""));
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }

    private void companyInfo() {
        StringRequest companyStringRequest = new StringRequest(
                Request.Method.GET,
                API_URL + "companys/" + PreferenceManager.getDefaultSharedPreferences(TerminalActivity.this).getString("com.macroyau.blue2serial.demo" + "COMPANY_ID", ""),
                response -> {
                    if (response != null) {
                        try {
                            dialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);

                            if (companyMaterialDialog != null && companyMaterialDialog.isAdded()) {

                            } else {
                                companyMaterialDialog.setCompany_name(jsonObject.getString("name"));
                                companyMaterialDialog.setLocation_lat(jsonObject.getDouble("location_lat"));
                                companyMaterialDialog.setLocation_long(jsonObject.getDouble("location_long"));
                                companyMaterialDialog.setAddress(jsonObject.getString("address"));
                                companyMaterialDialog.setPhone(jsonObject.getString("phone"));
                                companyMaterialDialog.setEmail(jsonObject.getString("email"));
                                companyMaterialDialog.setCancelable(false);
                                companyMaterialDialog.show(getSupportFragmentManager(), "companyMaterialDialog");
                                companyMaterialDialog.setCancelable(true);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    if (error instanceof AuthFailureError && error.networkResponse.statusCode == 401) {
                        companyInfoRefresh();
                    } else {
                        dialog.dismiss();
                        error.printStackTrace();
                        myVolleyError(TerminalActivity.this, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("accept", "application/json");
                headers.put("Authorization", "JWT " + PreferenceManager.getDefaultSharedPreferences(TerminalActivity.this).getString("com.macroyau.blue2serial.demo" + "ACCESS", ""));
                return headers;
            }
        };
        companyStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(companyStringRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Disconnect from the remote device and close the serial port
        bluetoothSerial.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        populateChatIndex(TerminalActivity.this);

        // Open a Bluetooth serial port and get ready to establish a connection
        if (bluetoothSerial.checkBluetooth() && bluetoothSerial.isBluetoothEnabled()) {
            if (!bluetoothSerial.isConnected()) {
                bluetoothSerial.start();
            }
        }
        activeActivity = this;
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
    }

    @Override
    public void invalidateOptionsMenu() {
        if (bluetoothSerial == null)
            return;

        // Show or hide the "Connect" and "Disconnect" buttons on the app bar
        if (bluetoothSerial.isConnected()) {
            connection_status.setText(Html.fromHtml("<font color='#006400'><b>Connected to Writer</b></font>"));
            if (activeActivity instanceof TagIndexActivity) {
                TagIndexActivity.connection_status.setText(Html.fromHtml("<font color='#006400'><b>Connected to Writer</b></font>"));
            }
            Toast.makeText(getApplicationContext(), "Connection successful!", Toast.LENGTH_SHORT).show();
            /*if (writer_dialog != null && writer_dialog.isShowing()) {
                writer_dialog.dismiss();
            }*/
        } else {
            connection_status.setText(Html.fromHtml("<font color='red'><b>Disconnected from Writer</b></font>"));
            if (activeActivity instanceof TagIndexActivity) {
                TagIndexActivity.connection_status.setText(Html.fromHtml("<font color='red'><b>Disconnected from Writer</b></font>"));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                // Set up Bluetooth serial port when Bluetooth adapter is turned on
                if (resultCode == Activity.RESULT_OK) {
                    bluetoothSerial.setup();
                }
                break;
        }
    }

    private void updateBluetoothState() {
        // Get the current Bluetooth state
        final int state;
        if (bluetoothSerial != null)
            state = bluetoothSerial.getState();
        else
            state = BluetoothSerial.STATE_DISCONNECTED;

        // Display the current state on the app bar as the subtitle
        String subtitle;
        switch (state) {
            case BluetoothSerial.STATE_CONNECTING:
                subtitle = getString(R.string.status_connecting);
                break;
            case BluetoothSerial.STATE_CONNECTED:
                subtitle = getString(R.string.status_connected, bluetoothSerial.getConnectedDeviceName());
                break;
            default:
                subtitle = getString(R.string.status_disconnected);
                break;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(subtitle);
        }
    }

    public static void showDeviceListDialog(Activity activity) {
        // Display dialog for selecting a remote Bluetooth device
        BluetoothDeviceListDialog dialog = new BluetoothDeviceListDialog(activeActivity);
        dialog.setOnDeviceSelectedListener((BluetoothDeviceListDialog.OnDeviceSelectedListener) activity);
        dialog.setTitle(R.string.paired_devices);
        dialog.setDevices(bluetoothSerial.getPairedDevices());
        dialog.showAddress(true);
        dialog.show();
    }

    @Override
    public void onBluetoothNotSupported() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.no_bluetooth)
                .setPositiveButton(R.string.action_quit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onBluetoothDisabled() {
        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBluetooth, REQUEST_ENABLE_BLUETOOTH);
    }

    @Override
    public void onBluetoothDeviceDisconnected() {
        invalidateOptionsMenu();
        updateBluetoothState();
    }

    @Override
    public void onConnectingBluetoothDevice() {

        updateBluetoothState();
    }

    @Override
    public void onBluetoothDeviceConnected(String name, String address) {
        invalidateOptionsMenu();
        updateBluetoothState();
    }

    @Override
    public void onBluetoothSerialRead(String message) {

        bluetoothSerialRead(message);
    }


    @Override
    public void onBluetoothSerialWrite(String message) {

    }

    @Override
    public void onBluetoothDeviceSelected(BluetoothDevice device) {
        // Connect to the selected remote Bluetooth device
        bluetoothSerial.connect(device);
    }

    private void topUp(String id, String first_name, String last_name, String balance) {
        topupMaterialDialog.setClient(id);
        topupMaterialDialog.setFirst_name(first_name);
        topupMaterialDialog.setLast_name(last_name);
        topupMaterialDialog.setBalance(balance);
        topupMaterialDialog.show(getSupportFragmentManager(), "topupMaterialDialog");
        topupMaterialDialog.setCancelable(true);
    }

    private void goToTags(String customer_id, String first_name, String last_name) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                API_URL + "customers/" + customer_id,
                response -> {
                    if (response != null) {
                        try {
                            dialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            Realm.init(TerminalActivity.this);
                            Realm.getInstance(RealmUtility.getDefaultConfig(TerminalActivity.this)).executeTransaction(realm -> {
                                try {
                                    realm.where(RealmTag.class).findAll().deleteAllFromRealm();
                                    realm.createOrUpdateAllFromJson(RealmTag.class, jsonObject.getJSONArray("tag_client"));

                                    CUSTOMER_ID = customer_id;
                                    FIRST_NAME = first_name;
                                    LAST_NAME = last_name;
                                    startActivity(
                                            new Intent(TerminalActivity.this, TagIndexActivity.class)
                                    );
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    if (error instanceof AuthFailureError && error.networkResponse.statusCode == 401) {
                        goToTagsRefresh(customer_id, first_name, last_name);
                    } else {
                        dialog.dismiss();
                        error.printStackTrace();
                        myVolleyError(TerminalActivity.this, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("accept", "application/json");
                headers.put("Authorization", "JWT " + PreferenceManager.getDefaultSharedPreferences(TerminalActivity.this).getString("com.macroyau.blue2serial.demo" + "ACCESS", ""));
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }
/*
    private void getTag(String tag_uid, String customer_id, String name) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                API_URL + "tags/" + customer_id,
                response -> {
                    if (response != null) {
                        try {
                            dialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("client").equals(activeActivity.getIntent().getStringExtra("customer_id"))) {
                                if (jsonObject.getString("status").equals("D")) {
                                    // Seek clarification as to what to do
                                } else {
                                    new AlertDialog.Builder(activeActivity)
                                            .setMessage(name + " is already using this tag.")
                                            .setCancelable(true)
                                            .setPositiveButton("Ok", (dlg, which) -> {
                                                dlg.dismiss();
                                            })
                                            .show();
                                }
                            } else {
                                if (jsonObject.getString("status").equals("D")) {
                                    // Seek clarification as to what to do
                                } else {
                                    new AlertDialog.Builder(activeActivity)
                                            .setMessage("This tag is in use by another customer")
                                            .setCancelable(true)
                                            .setPositiveButton("Ok", (dlg, which) -> {
                                                dlg.dismiss();
                                            })
                                            .show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    if (error instanceof AuthFailureError && error.networkResponse.statusCode == 401) {
                        balanceCheckRefresh(customer_id, name);
                    } else {
                        dialog.dismiss();
                        error.printStackTrace();
                        myVolleyError(TerminalActivity.this, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("accept", "application/json");
                headers.put("Authorization", "JWT " + PreferenceManager.getDefaultSharedPreferences(TerminalActivity.this).getString("com.macroyau.blue2serial.demo" + "ACCESS", ""));
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }*/

    private void fetchCustomers() {
        if (PreferenceManager.getDefaultSharedPreferences(TerminalActivity.this).getString("com.macroyau.blue2serial.demo" + "NEXT_CUSTOMERS", "").equals("")) {
            String company_id = PreferenceManager.getDefaultSharedPreferences(TerminalActivity.this).getString("com.macroyau.blue2serial.demo" + "COMPANY_ID", "");
            String branch_id = PreferenceManager.getDefaultSharedPreferences(TerminalActivity.this).getString("com.macroyau.blue2serial.demo" + "BRANCH_ID", "");
            PreferenceManager
                    .getDefaultSharedPreferences(TerminalActivity.this)
                    .edit()
                    .putString("com.macroyau.blue2serial.demo" + "NEXT_CUSTOMERS", API_URL + "customers?company_id=" + company_id + "&branch_id=" + branch_id)
                    .apply();
        }

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                PreferenceManager.getDefaultSharedPreferences(TerminalActivity.this).getString("com.macroyau.blue2serial.demo" + "NEXT_CUSTOMERS", ""),
                response -> {
                    dialog.dismiss();
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            Realm.init(TerminalActivity.this);
                            Realm.getInstance(RealmUtility.getDefaultConfig(TerminalActivity.this)).executeTransaction(realm -> {
                                try {
                                    realm.createOrUpdateAllFromJson(RealmCustomer.class, jsonObject.getJSONArray("results"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                            populateChatIndex(TerminalActivity.this);
                            if (!jsonObject.isNull("next")) {
                                PreferenceManager
                                        .getDefaultSharedPreferences(TerminalActivity.this)
                                        .edit()
                                        .putString("com.macroyau.blue2serial.demo" + "NEXT_CUSTOMERS", jsonObject.getString("next"))
                                        .apply();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    if (error instanceof AuthFailureError && error.networkResponse.statusCode == 401) {
                        indexRefresh();
                    } else {
                        dialog.dismiss();
                        error.printStackTrace();
                        myVolleyError(TerminalActivity.this, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {

            /*Passing some request headers*/
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("accept", "application/json");
                headers.put("Authorization", "JWT " + PreferenceManager.getDefaultSharedPreferences(TerminalActivity.this).getString("com.macroyau.blue2serial.demo" + "ACCESS", ""));
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }

    private void updateBalance(String customer_id, String customerFirstName, String customer_last_name, String balance) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                API_URL + "balances/update/" + customer_id + "/" + balance,
                response -> {
                    if (response != null) {
                        dialog.dismiss();
                        topUp(customer_id, customerFirstName, customer_last_name, balance);
                    }
                },
                error -> {
                    if (error instanceof AuthFailureError && error.networkResponse.statusCode == 401) {
                        updateBalanceRefresh(customer_id, customerFirstName, customer_last_name, balance);
                    } else {
                        dialog.dismiss();
                        error.printStackTrace();
                        myVolleyError(TerminalActivity.this, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("accept", "application/json");
                headers.put("Authorization", "JWT " + PreferenceManager.getDefaultSharedPreferences(TerminalActivity.this).getString("com.macroyau.blue2serial.demo" + "ACCESS", ""));
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }

    private void fetchUser() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                API_URL + "users/me",
                response -> {
                    dialog.dismiss();
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            final RealmUser[] realmUser = new RealmUser[1];
                            Realm.init(TerminalActivity.this);
                            Realm.getInstance(RealmUtility.getDefaultConfig(TerminalActivity.this)).executeTransaction(realm -> {
                                realmUser[0] = realm.createOrUpdateObjectFromJson(RealmUser.class, jsonObject);
                            });
                            UserAccountActivity.realmUser = realmUser[0];
                            startActivity(new Intent(getApplicationContext(), UserAccountActivity.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    if (error instanceof AuthFailureError && error.networkResponse.statusCode == 401) {
                        fetchUserRefresh();
                    } else {
                        dialog.dismiss();
                        error.printStackTrace();
                        myVolleyError(TerminalActivity.this, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {

            /*Passing some request headers*/
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("accept", "application/json");
                headers.put("Authorization", "JWT " + PreferenceManager.getDefaultSharedPreferences(TerminalActivity.this).getString("com.macroyau.blue2serial.demo" + "ACCESS", ""));
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }

    private void checkStatus(String customer_id, String name) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                API_URL + "customers/" + customer_id,
                response -> {
                    if (response != null) {
                        try {
                            dialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            String first_name = jsonObject.getString("first_name");
                            String last_name = jsonObject.getString("last_name");

                            JSONArray tags = jsonObject.getJSONArray("tag_client");

                            AlertDialog.Builder builder = new AlertDialog.Builder(TerminalActivity.this)
                                    .setTitle("User Details")
                                    .setMessage(Html.fromHtml("<b>Customer name: </b>" + name + "<br><br><font color='red'>Customer has no active tag</font>"))
                                    .setCancelable(true)
                                    .setPositiveButton("Ok", (dialog, which) -> {
                                        dialog.dismiss();
                                    });

                            for (int i = 0; i < tags.length(); i++) {

                                if (tags.getJSONObject(i).getString("status").equals("A")) {
                                    double bal = jsonObject.isNull("balance_client") ? 0.00 : jsonObject.getJSONObject("balance_client").getDouble("balance");
                                    serialSend("status*" + String.valueOf(tags.getJSONObject(i).getInt("id")) + "#" + first_name + "$" + last_name + "&" + String.format("%.2f", bal));
                                    builder.setMessage(Html.fromHtml("<b>Customer name: </b>" + name + "<br><br><b>Balance: </b>" + "GHS" + String.format("%.5f", bal)));
                                }
                            }
                            builder.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    if (error instanceof AuthFailureError && error.networkResponse.statusCode == 401) {
                        balanceCheckRefresh(customer_id, name);
                    } else {
                        dialog.dismiss();
                        error.printStackTrace();
                        myVolleyError(TerminalActivity.this, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("accept", "application/json");
                headers.put("Authorization", "JWT " + PreferenceManager.getDefaultSharedPreferences(TerminalActivity.this).getString("com.macroyau.blue2serial.demo" + "ACCESS", ""));
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }

    public static void populateChatIndex(final Context context) {
        Realm.init(context);
        Realm.getInstance(RealmUtility.getDefaultConfig(context)).executeTransaction(realm -> {
            RealmResults<RealmCustomer> results;

            results = realm.where(RealmCustomer.class)
                    .findAll();

            if (results.size() < 1) {
                swipe_load.setVisibility(View.VISIBLE);
                recyclerview.setVisibility(View.VISIBLE);
            } else {
                swipe_load.setVisibility(View.GONE);
                recyclerview.setVisibility(View.VISIBLE);
            }
            newCart.clear();
            for (RealmCustomer realmCustomer : results) {
                newCart.add(realmCustomer);
            }
            cartArrayList.clear();
            cartArrayList.addAll(newCart);
            customerIndexAdapter.notifyDataSetChanged();
        });
    }

    private void indexRefresh() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                API_URL + "auth/jwt/refresh",
                response -> {
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            PreferenceManager
                                    .getDefaultSharedPreferences(TerminalActivity.this)
                                    .edit()
                                    .putString("com.macroyau.blue2serial.demo" + "ACCESS", jsonObject.getString("access"))
                                    .apply();
                            fetchCustomers();
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
                        myVolleyError(TerminalActivity.this, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("refresh", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("com.macroyau.blue2serial.demo" + "REFRESH", ""));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }

    private void fetchUserRefresh() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                API_URL + "auth/jwt/refresh",
                response -> {
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            PreferenceManager
                                    .getDefaultSharedPreferences(TerminalActivity.this)
                                    .edit()
                                    .putString("com.macroyau.blue2serial.demo" + "ACCESS", jsonObject.getString("access"))
                                    .apply();
                            fetchUser();
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
                        myVolleyError(TerminalActivity.this, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("refresh", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("com.macroyau.blue2serial.demo" + "REFRESH", ""));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }

    private void updateBalanceRefresh(String customer_id, String customerFirstName, String customer_last_name, String balance) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                API_URL + "auth/jwt/refresh",
                response -> {
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            PreferenceManager
                                    .getDefaultSharedPreferences(TerminalActivity.this)
                                    .edit()
                                    .putString("com.macroyau.blue2serial.demo" + "ACCESS", jsonObject.getString("access"))
                                    .apply();
                            updateBalance(customer_id, customerFirstName, customer_last_name, balance);
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
                        myVolleyError(TerminalActivity.this, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("refresh", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("com.macroyau.blue2serial.demo" + "REFRESH", ""));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }

    private void goToTagsRefresh(String id, String first_name, String last_name) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                API_URL + "auth/jwt/refresh",
                response -> {
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            PreferenceManager
                                    .getDefaultSharedPreferences(TerminalActivity.this)
                                    .edit()
                                    .putString("com.macroyau.blue2serial.demo" + "ACCESS", jsonObject.getString("access"))
                                    .apply();
                            goToTags(id, first_name, last_name);
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
                        myVolleyError(TerminalActivity.this, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("refresh", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("com.macroyau.blue2serial.demo" + "REFRESH", ""));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }

    private void companyInfoRefresh() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                API_URL + "auth/jwt/refresh",
                response -> {
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            PreferenceManager
                                    .getDefaultSharedPreferences(TerminalActivity.this)
                                    .edit()
                                    .putString("com.macroyau.blue2serial.demo" + "ACCESS", jsonObject.getString("access"))
                                    .apply();
                            companyInfo();
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
                        myVolleyError(TerminalActivity.this, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("refresh", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("com.macroyau.blue2serial.demo" + "REFRESH", ""));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }

    private void branchInfoRefresh() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                API_URL + "auth/jwt/refresh",
                response -> {
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            PreferenceManager
                                    .getDefaultSharedPreferences(TerminalActivity.this)
                                    .edit()
                                    .putString("com.macroyau.blue2serial.demo" + "ACCESS", jsonObject.getString("access"))
                                    .apply();
                            branchInfo();
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
                        myVolleyError(TerminalActivity.this, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("refresh", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("com.macroyau.blue2serial.demo" + "REFRESH", ""));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }

    private void balanceCheckRefresh(String id, String name) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                API_URL + "auth/jwt/refresh",
                response -> {
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            PreferenceManager
                                    .getDefaultSharedPreferences(TerminalActivity.this)
                                    .edit()
                                    .putString("com.macroyau.blue2serial.demo" + "ACCESS", jsonObject.getString("access"))
                                    .apply();
                            checkStatus(id, name);
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
                        myVolleyError(TerminalActivity.this, error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("refresh", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("com.macroyau.blue2serial.demo" + "REFRESH", ""));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }

    public static void serialSend(String s) {
        bluetoothSerial.write(s, false);
    }

    public void bluetoothSerialRead(String message) {
        message = message.trim();
        try {
            JSONObject jsonObject = new JSONObject(message);

            String customer_id = null;
            String tag_id = null;
            try {
                tag_id = jsonObject.getString("tag_id");
                customer_id = jsonObject.getString("customer_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            switch (jsonObject.getString("type")) {
                case "topup":
                    if (activeActivity instanceof TagIndexActivity) activeActivity.finish();
                    Realm.init(getApplicationContext());
                    String finalTag_uid = tag_id;
                    String finalCustomer_id = customer_id;
                    final RealmCustomer[] customer = new RealmCustomer[1];
                    Realm.getInstance(RealmUtility.getDefaultConfig(getApplicationContext())).executeTransaction(realm -> {
                        if (!finalCustomer_id.equals("")) {
                            customer[0] = realm.where(RealmCustomer.class).equalTo("id", Integer.parseInt(finalCustomer_id)).findFirst();
                        }
                    });
                    if (customer[0] == null || finalCustomer_id.equals("")) {
                        dialog.dismiss();
                        new AlertDialog.Builder(TerminalActivity.this)
                                .setMessage("Unregistered tag!!")
                                .setPositiveButton("Ok", (dlg, which) -> {
                                    dlg.dismiss();
                                })
                                .setCancelable(true)
                                .show();
                        return;
                    }
                    String balance = "";
                    try {
                        balance = jsonObject.getString("balance");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.show();
                    updateBalance(finalCustomer_id, customer[0].getFirst_name(), customer[0].getLast_name(), balance);
                    break;
                case "register":
                    if (activeActivity instanceof TagIndexActivity && statusMaterialDialog != null && !statusMaterialDialog.isAdded()) {
                        if (customer_id == null || customer_id.equals("")) {
                            TAG_MODE = "ADD";
                            realmTag = new RealmTag();
                            realmTag.setClient(CUSTOMER_ID);
                            realmTag.setTag_uid(tag_id);
                            TagIndexActivity.dialog.show();
                            saveTag("A");
                        }
                        /*else {
                            TAG_MODE = "EDIT";
                            dialog.show();
                            getTag(tag_id, customer_id, first_name);
                        }*/
                    }
                    break;
                case "clear":
                    if (activeActivity instanceof TagIndexActivity && statusMaterialDialog != null && !statusMaterialDialog.isAdded()) {
                        if (customer_id == null || customer_id.equals("")) {
                            TAG_MODE = "ADD";
                            realmTag = new RealmTag();
                            realmTag.setClient(CUSTOMER_ID);
                            realmTag.setTag_uid(tag_id);
                            TagIndexActivity.dialog.show();
                            saveTag("A");
                        }
                        /*else {
                            TAG_MODE = "EDIT";
                            dialog.show();
                            getTag(tag_id, customer_id, first_name);
                        }*/
                    }
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<RealmCustomer> filter(ArrayList<RealmCustomer> models, String search_txt) {

        final ArrayList<RealmCustomer> filteredModelList = new ArrayList<>();
        for (RealmCustomer model : models) {

            if (
                    (model.getFirst_name() + " " + model.getLast_name()).toLowerCase().contains(search_txt.toLowerCase().toLowerCase()) ||
                    model.getLocality().toLowerCase().contains(search_txt.toLowerCase().toLowerCase()) ||
                    model.getDistrict().toLowerCase().contains(search_txt.toLowerCase().toLowerCase())

            ) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public static void signOut() {
        PreferenceManager
                .getDefaultSharedPreferences(activeActivity)
                .edit()
                .putString("com.macroyau.blue2serial.demo" + "ACCESS", "")
                .putString("com.macroyau.blue2serial.demo" + "REFRESH", "")
                .putString("com.macroyau.blue2serial.demo" + "BRANCH_ID", "")
                .putString("com.macroyau.blue2serial.demo" + "COMPANY_ID", "")
                .putString("com.macroyau.blue2serial.demo" + "NEXT_CUSTOMERS", "")
                .apply();
        Realm.init(activeActivity);
        Realm.getInstance(RealmUtility.getDefaultConfig(activeActivity)).executeTransaction(realm -> realm.deleteAll());
        activeActivity.startActivity(new Intent(activeActivity, SigninActivity.class));
        activeActivity.finish();
    }
}
