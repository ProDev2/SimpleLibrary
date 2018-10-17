package com.simplelib;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

public abstract class SimpleFragment extends Fragment {
    private int id;
    private View contentView;

    private int menuId;
    private Menu menu;

    public SimpleFragment(int id) {
        this.id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(id, container, false);
        this.contentView = view;

        resetToolbar();

        create(view);

        return view;
    }

    public View getContentView() {
        return contentView;
    }

    public View findViewById(int id) {
        try {
            return getContentView().findViewById(id);
        } catch (Exception e) {
        }
        return null;
    }

    public abstract void create(View view);

    public boolean back() {
        return true;
    }

    public void resetToolbar() {
        setBackButtonEnabled(false);
        disableOptionsMenu();

        try {
            ActivityInfo activityInfo = getActivity().getPackageManager().getActivityInfo(getActivity().getComponentName(), PackageManager.GET_META_DATA);
            String title = activityInfo.loadLabel(getActivity().getPackageManager()).toString();

            setTitle(title);
        } catch (Exception e) {
        }
    }

    public void setToolbar(int toolbarId) {
        try {
            View view = findViewById(toolbarId);

            if (view instanceof Toolbar)
                setToolbar((Toolbar) view);
        } catch (Exception e) {
        }
    }

    public void setToolbar(Toolbar toolbar) {
        try {
            if (getActivity() instanceof SimpleActivity) {
                SimpleActivity activity = (SimpleActivity) getActivity();
                activity.setSupportActionBar(toolbar);
            }
        } catch (Exception e) {
        }
    }

    public ActionBar getToolbar() {
        try {
            if (getActivity() instanceof SimpleActivity) {
                SimpleActivity activity = (SimpleActivity) getActivity();
                return activity.getSupportActionBar();
            }
        } catch (Exception e) {
        }
        return null;
    }

    public void setTitle(String title) {
        try {
            if (getActivity() instanceof SimpleActivity) {
                SimpleActivity activity = (SimpleActivity) getActivity();
                activity.getSupportActionBar().setTitle(title);
            }
        } catch (Exception e) {
        }
    }

    public void setBackButtonEnabled(boolean enabled) {
        try {
            if (getActivity() instanceof SimpleActivity) {
                SimpleActivity activity = (SimpleActivity) getActivity();
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(enabled);
                activity.getSupportActionBar().setDisplayShowHomeEnabled(enabled);
            }
        } catch (Exception e) {
        }
    }

    public void enableOptionsMenu(int menu) {
        this.setHasOptionsMenu(true);
        this.menuId = menu;
    }

    public void disableOptionsMenu() {
        this.setHasOptionsMenu(false);
        this.menuId = -1;
    }

    public Menu getMenu() {
        return menu;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        try {
            if (menuId >= 0)
                inflater.inflate(menuId, menu);
        } catch (Exception e) {
        }

        this.menu = menu;

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStop() {
        super.onStop();

        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }
}
