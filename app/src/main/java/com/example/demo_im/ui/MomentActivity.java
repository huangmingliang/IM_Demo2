package com.example.demo_im.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.example.demo_im.R;
import com.module.moments.fragment.MomentsFragment;

public class MomentActivity extends FragmentActivity {

    private MomentsFragment momentsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment);
        momentsFragment=new MomentsFragment();
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer,momentsFragment);
        fragmentTransaction.commit();
    }
}
