package com.macroyau.blue2serial.demo.activity;
import static com.macroyau.blue2serial.demo.constants.Const.API_URL;
import static com.macroyau.blue2serial.demo.constants.Const.myVolleyError;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.macroyau.blue2serial.demo.R;
import com.macroyau.blue2serial.demo.other.InitApplication;
import com.macroyau.blue2serial.demo.realm.RealmUser;
import com.macroyau.blue2serial.demo.util.RealmUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;


public class SigninActivity extends AppCompatActivity {

    private static final String TAG = "LogMeIn";
    ImageView passwordIcon;
    TextView forgotpassword;
    boolean passwordShow = false;
    Context context;
    private EditText usernameField, passwordField;
    private Button login;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        context = getApplicationContext();

        FragmentManager fm = getSupportFragmentManager();

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Signing in...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        login = findViewById(R.id.login);
        usernameField = findViewById(R.id.locality);
        passwordField = findViewById(R.id.password);
        passwordIcon = findViewById(R.id.passwordIcon);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignin();
            }
        });

        passwordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordShow = !passwordShow;
                if (passwordShow) {
                    passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hide_password);
                    passwordIcon.setImageBitmap(bitmap);
                } else {
                    passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.see_password);
                    passwordIcon.setImageBitmap(bitmap);
                }
            }
        });
    }

    public void attemptSignin() {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        boolean canLogin = true;

        if (TextUtils.isEmpty(username)) {
            usernameField.setError(getString(R.string.error_field_required));
            canLogin = false;
        } else {
            usernameField.setError(null);
        }
        if (TextUtils.isEmpty(password)) {
            passwordField.setError(getString(R.string.error_field_required));
            canLogin = false;
        } else {
            passwordField.setError(null);
        }

        if (canLogin) {
            mProgress.show();
            Signin(username, password);
        }
    }

    public void Signin(String username, String password) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                API_URL + "auth/jwt/create",
                response -> {
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("details")) {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("details"), Toast.LENGTH_SHORT).show();
                            } else if (jsonObject.has("access")) {
                                PreferenceManager
                                        .getDefaultSharedPreferences(SigninActivity.this)
                                        .edit()
                                        .putString("com.macroyau.blue2serial.demo" + "ACCESS", jsonObject.getString("access"))
                                        .putString("com.macroyau.blue2serial.demo" + "REFRESH", jsonObject.getString("refresh"))
                                        .apply();
                                StringRequest innerStringRequest = new StringRequest(
                                        Request.Method.GET,
                                        API_URL + "users/me",
                                        innerResponse -> {
                                            if (innerResponse != null) {
                                                try {
                                                    mProgress.dismiss();
                                                    JSONObject innerJsonObject = new JSONObject(innerResponse);
                                                    Realm.init(SigninActivity.this);
                                                    Realm.getInstance(RealmUtility.getDefaultConfig(SigninActivity.this)).executeTransaction(realm -> {
                                                        try {
                                                            PreferenceManager
                                                                    .getDefaultSharedPreferences(SigninActivity.this)
                                                                    .edit()
                                                                    .putString("com.macroyau.blue2serial.demo" + "COMPANY_ID", String.valueOf(innerJsonObject.getInt("company")))
                                                                    .putString("com.macroyau.blue2serial.demo" + "BRANCH_ID", String.valueOf(innerJsonObject.getInt("branch")))
                                                                    .putString("com.macroyau.blue2serial.demo" + "USER_ID", String.valueOf(innerJsonObject.getInt("id")))
                                                                    .apply();
                                                            realm.createOrUpdateObjectFromJson(RealmUser.class, innerJsonObject);
                                                            startActivity(new Intent(getApplicationContext(), TerminalActivity.class));
                                                            finish();
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
                                            mProgress.dismiss();
                                            error.printStackTrace();
                                            myVolleyError(SigninActivity.this, error);
                                            Log.d("Cyrilll", error.toString());
                                        }
                                ) {
                                    @Override
                                    public Map getHeaders() throws AuthFailureError {
                                        HashMap headers = new HashMap();
                                        headers.put("accept", "application/json");
                                        headers.put("Authorization", "JWT " + PreferenceManager.getDefaultSharedPreferences(SigninActivity.this).getString("com.macroyau.blue2serial.demo" + "ACCESS", ""));
                                        return headers;
                                    }
                                };
                                innerStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                        0,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                InitApplication.getInstance().addToRequestQueue(innerStringRequest);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    mProgress.dismiss();
                    error.printStackTrace();
                    if (error instanceof AuthFailureError) {
                        Toast.makeText(getApplicationContext(), "Invalid login credentials!", Toast.LENGTH_SHORT).show();

                    }
                    myVolleyError(SigninActivity.this, error);
                    Log.d("Cyrilll", error.toString());
                }
        ) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("accept", "application/json");
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