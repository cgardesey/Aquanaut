package com.macroyau.blue2serial.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.macroyau.blue2serial.demo.R;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = "SplashScreenActivity";

    private int progressStatus = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final ProgressBar pb = findViewById(R.id.pb);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100) {
                    // Update the progress status
                    progressStatus += 1;

                    // Try to sleep the thread for 20 milliseconds
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Update the progress bar
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            pb.setProgress(progressStatus);
                        }
                    });
                }
                if (progressStatus == 100) {
                    if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("com.macroyau.blue2serial.demo" + "ACCESS",  "").equals("")) {
                        startActivity(new Intent(SplashScreenActivity.this, SigninActivity.class));
                    } else {
                        startActivity(new Intent(SplashScreenActivity.this, TerminalActivity.class));
                    }
                    finish();
                    return;
                }
            }
        }).start(); // Start the operation

    }
}
