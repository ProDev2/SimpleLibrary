package com.simplelib;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import java.util.List;

public class SimpleActivity extends AppCompatActivity {
    private boolean sendBackEvent;

    public SimpleActivity() {
        this.sendBackEvent = true;
    }

    public void setSendBackEvent(boolean sendBackEvent) {
        this.sendBackEvent = sendBackEvent;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && sendBackEvent) {
            try {
                Fragment fragment = getVisibleFragment();
                if (fragment instanceof SimpleFragment) {
                    SimpleFragment simpleFragment = (SimpleFragment) fragment;
                    if (!simpleFragment.back()) {
                        return false;
                    }
                }
            } catch (Exception e) {
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setToolbar(int toolbarId) {
        try {
            View view = findViewById(toolbarId);

            if (view instanceof Toolbar)
                setSupportActionBar((Toolbar) view);
        } catch (Exception e) {
        }
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    public void switchTo(int containerViewId, Fragment fragment, String tag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(containerViewId, fragment, tag);
        ft.commit();
    }
}
