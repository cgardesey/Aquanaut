package com.macroyau.blue2serial.demo.materialDialog;

import static com.macroyau.blue2serial.demo.activity.TerminalActivity.serialSend;
import static com.macroyau.blue2serial.demo.activity.TerminalActivity.signOut;
import static com.macroyau.blue2serial.demo.constants.Const.API_URL;
import static com.macroyau.blue2serial.demo.constants.Const.myVolleyError;

import static io.realm.Realm.getApplicationContext;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.macroyau.blue2serial.demo.R;
import com.macroyau.blue2serial.demo.activity.CustomerAccountActivity;
import com.macroyau.blue2serial.demo.activity.TerminalActivity;
import com.macroyau.blue2serial.demo.constants.Const;
import com.macroyau.blue2serial.demo.other.InitApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TopupMaterialDialog extends DialogFragment {
    public static String client, first_name, last_name, balance;

    public static String getClient() {
        return client;
    }

    public static void setClient(String client) {
        TopupMaterialDialog.client = client;
    }

    public static String getFirst_name() {
        return first_name;
    }

    public static void setFirst_name(String first_name) {
        TopupMaterialDialog.first_name = first_name;
    }

    public static String getLast_name() {
        return last_name;
    }

    public static void setLast_name(String last_name) {
        TopupMaterialDialog.last_name = last_name;
    }

    @Nullable
    @Override
    public ProgressDialog getDialog() {
        return dialog;
    }

    public void setDialog(ProgressDialog dialog) {
        this.dialog = dialog;
    }

    public static String getBalance() {
        return balance;
    }

    public static void setBalance(String balance) {
        TopupMaterialDialog.balance = balance;
    }

    public EditText getAmount_edittext() {
        return amount_edittext;
    }

    public void setAmount_edittext(EditText amount_edittext) {
        this.amount_edittext = amount_edittext;
    }

    public static TextView getName_textview() {
        return name_textview;
    }

    public static void setName_textview(TextView name_textview) {
        TopupMaterialDialog.name_textview = name_textview;
    }

    public static TextView getBalance_textview() {
        return balance_textview;
    }

    public static void setBalance_textview(TextView balance_textview) {
        TopupMaterialDialog.balance_textview = balance_textview;
    }

    public Button getOk() {
        return ok;
    }

    public void setOk(Button ok) {
        this.ok = ok;
    }

    EditText amount_edittext;
    public static TextView name_textview, balance_textview;
    Button ok;
    ProgressDialog dialog;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.list_item_topup, null);

        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Topping up");
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);

        name_textview = view.findViewById(R.id.name);
        balance_textview = view.findViewById(R.id.balance);
        amount_edittext = view.findViewById(R.id.amount);
        ok = view.findViewById(R.id.ok);

        name_textview.setText(first_name + " " + last_name);
        balance_textview.setText(balance);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    dialog.show();
                    topups();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // If you want to modify a view in your Activity
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    }
                });
            }
        }, 5);
        return builder.create();
    }

    private void topups() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Const.API_URL + "topups_balances/add/" + client + "/" + amount_edittext.getText().toString() + "/" + amount_edittext.getText().toString() + "/" + PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("com.macroyau.blue2serial.demo" + "USER_ID", "") + "/",
                response -> {
                    if (response != null) {
                        dialog.dismiss();
                        String[] split = response.split(" ");
                        String value = split[split.length - 1];
                        serialSend("topup*" + String.format("%.5f", Float.parseFloat(value.substring(0, value.length() - 2))));

                        new AlertDialog.Builder(getActivity())
                                .setTitle("Top up Successful")
                                .setMessage(Html.fromHtml("<b> GHS" + String.format("%.5f", Float.parseFloat(amount_edittext.getText().toString())) + " </b>" + "successfully topped up to " + "<b>" + first_name + " " + last_name + "</b><br><br>" + "New balance is " + "<font color='blue'>" + "GHS" + String.format("%.5f", Float.parseFloat(split[split.length - 1].replace("}", "").replace("\"", ""))) + "</font>"))
                                .setCancelable(true)
                                .setPositiveButton("Ok", (dialog, which) -> {
                                    dialog.dismiss();
                                    dismiss();
                                })
                                .show();
                    }
                },
                error -> {
                    if (error instanceof AuthFailureError && error.networkResponse.statusCode == 401) {
                        refresh();
                    } else {
                        dialog.dismiss();
                        error.printStackTrace();
                        myVolleyError(getActivity(), error);
                        Log.d("Cyrilll", error.toString());
                    }
                }
        ) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("accept", "application/json");
                headers.put("Authorization", "JWT " + PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("com.macroyau.blue2serial.demo" + "ACCESS", ""));
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    public boolean validate() {
        boolean validated = true;
        if (TextUtils.isEmpty(amount_edittext.getText())) {
            amount_edittext.setError(getString(R.string.error_field_required));
            validated = false;
        }
        else {
            float top_up = Float.parseFloat(amount_edittext.getText().toString());
            if (top_up < 1.00000) {
                amount_edittext.setError("Minimum topup amount is GHC1.00");
                validated = false;
            }
        }
        return validated;
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
                                    .getDefaultSharedPreferences(getActivity())
                                    .edit()
                                    .putString("com.macroyau.blue2serial.demo" + "ACCESS", jsonObject.getString("access"))
                                    .apply();
                            topups();
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
                        myVolleyError(getActivity(), error);
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
}