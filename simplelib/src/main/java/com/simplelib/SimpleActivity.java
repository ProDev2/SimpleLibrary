package com.simplelib;

import android.content.Context;
import android.content.pm.ApplicationInfo;
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

    public String getDefaultName() {
        return getApplicationName(this);
    }

    public void resetToolbar() {
        setBackButtonEnabled(false);

        resetTitle();
        resetSubtitle();
    }

    public void resetTitle() {
        String title = getDefaultName();
        setTitle(title);
    }

    public void resetSubtitle() {
        String subtitle = "";
        setSubtitle(subtitle);
    }

    public void setTitle(String title) {
        try {
            if (title == null)
                title = "";

            getSupportActionBar().setTitle(title);
        } catch (Exception e) {
        }
    }

    public void setSubtitle(String subtitle) {
        try {
            if (subtitle == null)
                subtitle = "";

            getSupportActionBar().setSubtitle(subtitle);
        } catch (Exception e) {
        }
    }

    public void setBackButtonEnabled(boolean enabled) {
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(enabled);
            getSupportActionBar().setDisplayShowHomeEnabled(enabled);
        } catch (Exception e) {
        }
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

    public static String getApplicationName(Context context) {
        if (context == null) return null;

        try {
            ApplicationInfo applicationInfo = context.getApplicationInfo();
            if (applicationInfo == null) return null;

            int labelResId = applicationInfo.labelRes;
            return labelResId != 0 ? context.getString(labelResId) : applicationInfo.nonLocalizedLabel.toString();
        } catch (Exception e) {
        }
        return null;
    }
}
