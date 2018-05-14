package com.prodev.simple;

import android.os.Bundle;

import com.prodev.simple.fragments.MainFragment;
import com.simplelib.SimpleActivity;

public class MainActivity extends SimpleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar(R.id.main_toolbar);

        switchTo(R.id.main_frame_layout, new MainFragment(), "main");
    }
}
