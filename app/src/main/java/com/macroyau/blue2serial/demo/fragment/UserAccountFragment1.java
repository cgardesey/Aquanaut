package com.macroyau.blue2serial.demo.fragment;


import static com.macroyau.blue2serial.demo.activity.UserAccountActivity.realmUser;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;

import com.macroyau.blue2serial.demo.R;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by 2CLearning on 12/13/2017.
 */

public class UserAccountFragment1 extends Fragment{
    public static final String PICTURE_TYPE = "PICTURE_TYPE";
    public static final String TYPE_PROFILE_PIC = "TYPE_PROFILE_PIC";
    private static final String TAG = "AccountFragment1";
    Context mContext;
    public static File profile_pic_file = null;
    public static EditText firstName, lastName, phone, email, district, ghana_card;
    RelativeLayout date_select_layout;
    private ImageView opendate;
    SimpleDateFormat simpleDateFormat;
    public static TextView dob;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();

        final View rootView = inflater.inflate(R.layout.fragment_user_account1, container, false);
        firstName = rootView.findViewById(R.id.first_name);
        lastName = rootView.findViewById(R.id.last_name);
        phone = rootView.findViewById(R.id.phone);
        email = rootView.findViewById(R.id.locality);
        district = rootView.findViewById(R.id.district);
        ghana_card = rootView.findViewById(R.id.ghana_card);
        date_select_layout = rootView.findViewById(R.id.date_select_layout);
        dob = rootView.findViewById(R.id.dob);
        date_select_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate(1980, 0, 1, R.style.NumberPickerStyle);
            }

        });

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // txtData = (TextView)view.findViewById(R.id.txtData);
    }

    public void init() {
        if (realmUser != null) {
            firstName.setText(realmUser.getFirst_name());
            lastName.setText(realmUser.getLast_name());
            district.setText(realmUser.getAddress());
            email.setText(realmUser.getEmail());
            ghana_card.setText(realmUser.getGhana_card());
            phone.setText(realmUser.getPhone());
            dob.setText(realmUser.getDate_of_birth());
        }
    }

    public boolean validate() {
        boolean validated = true;

        if (TextUtils.isEmpty(firstName.getText().toString())) {
            firstName.setError(getString(R.string.error_field_required));
            validated = false;
        }
        if (TextUtils.isEmpty(lastName.getText().toString())) {
            lastName.setError(getString(R.string.error_field_required));
            validated = false;
        }
        if (TextUtils.isEmpty(district.getText().toString())) {
            district.setError(getString(R.string.error_field_required));
            validated = false;
        }
        if (!isValidEmail(email.getText().toString())) {
            email.setError("Invalid email");
            validated = false;
        }
        if (TextUtils.isEmpty(phone.getText().toString())) {
            phone.setError(getString(R.string.error_field_required));
            validated = false;
        }
        if (TextUtils.isEmpty(ghana_card.getText().toString())) {
            ghana_card.setError(getString(R.string.error_field_required));
            validated = false;
        }
        return validated;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @VisibleForTesting
    void showDate(int year, int monthOfYear, int dayOfMonth, int spinnerTheme) {
        new SpinnerDatePickerDialogBuilder()
                .context(getContext())
                .callback(new com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                        dob.setError(null);
                        dob.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                })
                .spinnerTheme(R.style.NumberPickerStyle)
                .defaultDate(year, monthOfYear, dayOfMonth)
                .build()
                .show();
    }
}