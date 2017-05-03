package com.example.demo_im.ui;

import android.app.Activity;
import android.os.Bundle;

import com.example.demo_im.R;

public class QRcodeActivity extends Activity {
    private String TAG=QRcodeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
    }
}
