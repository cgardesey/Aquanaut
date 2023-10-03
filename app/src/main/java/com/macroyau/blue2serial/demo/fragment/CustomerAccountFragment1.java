package com.macroyau.blue2serial.demo.fragment;


import static com.macroyau.blue2serial.demo.activity.CustomerAccountActivity.realmCustomer;

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

public class CustomerAccountFragment1 extends Fragment{
    public static final String PICTURE_TYPE = "PICTURE_TYPE";
    public static final String TYPE_PROFILE_PIC = "TYPE_PROFILE_PIC";
    private static final String TAG = "AccountFragment1";
    Context mContext;
    public static File profile_pic_file = null;
    public static EditText firstName, lastName, phone, district, locality, address;
    SimpleDateFormat simpleDateFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();

        final View rootView = inflater.inflate(R.layout.fragment_customer_account1, container, false);
        firstName = rootView.findViewById(R.id.first_name);
        lastName = rootView.findViewById(R.id.last_name);
        phone = rootView.findViewById(R.id.phone);
        district = rootView.findViewById(R.id.district);
        locality = rootView.findViewById(R.id.locality);
        address = rootView.findViewById(R.id.address);

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
        if (realmCustomer != null) {
            firstName.setText(realmCustomer.getFirst_name());
            lastName.setText(realmCustomer.getLast_name());
            district.setText(realmCustomer.getDistrict());
            locality.setText(realmCustomer.getLocality());
            address.setText(realmCustomer.getAddress());
            phone.setText(realmCustomer.getPhone());
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
        if (TextUtils.isEmpty(locality.getText().toString())) {
            locality.setError(getString(R.string.error_field_required));
            validated = false;
        }
        if (TextUtils.isEmpty(phone.getText().toString())) {
            phone.setError(getString(R.string.error_field_required));
            validated = false;
        }
        if (TextUtils.isEmpty(address.getText().toString())) {
            address.setError(getString(R.string.error_field_required));
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

                    }
                })
                .spinnerTheme(R.style.NumberPickerStyle)
                .defaultDate(year, monthOfYear, dayOfMonth)
                .build()
                .show();
    }
}