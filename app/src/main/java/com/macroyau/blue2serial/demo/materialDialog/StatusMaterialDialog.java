package com.macroyau.blue2serial.demo.materialDialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.macroyau.blue2serial.demo.R;
import com.macroyau.blue2serial.demo.activity.TagIndexActivity;
import com.macroyau.blue2serial.demo.activity.TerminalActivity;

import java.util.Timer;
import java.util.TimerTask;

public class StatusMaterialDialog extends DialogFragment {

    ProgressDialog mProgress;
    RadioGroup rg;
    RadioButton rbActive, rbInactive, rbDeactivated;
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.list_item_tag,null);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        rg = (RadioGroup) view.findViewById(R.id.radiogroup);

        rbActive = view.findViewById(R.id.active);
        rbInactive = view.findViewById(R.id.inactive);
        rbDeactivated = view.findViewById(R.id.inactive);

        if (status.equals("A")) rbActive.setChecked(true);
        else if (status.equals("I")) rbInactive.setChecked(true);
        else if (status.equals("D")) rbDeactivated.setChecked(true);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String status = null;
                RadioButton rb = view.findViewById(i);
                String text = rb.getText().toString();
                if (text.equals("Active")) status = "A";
                else if (text.equals("Inactive")) status = "I";
                else if (text.equals("Deactivated")) status = "D";
                TagIndexActivity.dialog.show();
                TagIndexActivity.saveTag(status);
            }
        });


        // doneBtn.setOnClickListener(doneAction);
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
}