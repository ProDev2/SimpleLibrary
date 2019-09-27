package com.simplelib;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.preference.PreferenceFragmentCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

public abstract class SimplePreferencesFragment extends PreferenceFragmentCompat {
    private int preferencesId;
    private View contentView;

    protected boolean overrideActivityDefaults;

    private String title, subtitle;
    private boolean backButton;

    private int menuId;
    private Menu menu;

    public SimplePreferencesFragment(int preferencesId) {
        this.preferencesId = preferencesId;

        if (overrideActivityDefaults || getActivity() == null)
            resetToolbar();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        try {
            setPreferencesFromResource(preferencesId, rootKey);
        } catch (Exception e) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
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

    public String getDefaultName() {
        return SimpleActivity.getApplicationName(getActivity());
    }

    public void resetToolbar() {
        setBackButtonEnabled(false);
        disableOptionsMenu();

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

    public String getTitle() {
        if (title == null)
            resetTitle();
        return title;
    }

    public void setTitle(String title) {
        try {
            if (title == null)
                title = "";

            this.title = title;
            if (getUserVisibleHint() && getActivity() instanceof SimpleActivity) {
                SimpleActivity activity = (SimpleActivity) getActivity();
                activity.setTitle(title);
            }
        } catch (Exception e) {
        }
    }

    public String getSubtitle() {
        if (subtitle == null)
            resetSubtitle();
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        try {
            if (subtitle == null)
                subtitle = "";

            this.subtitle = subtitle;
            if (getUserVisibleHint() && getActivity() instanceof SimpleActivity) {
                SimpleActivity activity = (SimpleActivity) getActivity();
                activity.setSubtitle(subtitle);
            }
        } catch (Exception e) {
        }
    }

    public boolean isBackButtonEnabled() {
        return backButton;
    }

    public void setBackButtonEnabled(boolean enabled) {
        try {
            this.backButton = enabled;
            if (getUserVisibleHint() && getActivity() instanceof SimpleActivity) {
                SimpleActivity activity = (SimpleActivity) getActivity();
                activity.setBackButtonEnabled(enabled);
            }
        } catch (Exception e) {
        }
    }

    public void callActivity(int code, Object... args) {
        if (args == null) args = new Object[0];

        try {
            if (getActivity() instanceof SimpleActivity) {
                SimpleActivity activity = (SimpleActivity) getActivity();
                activity.onReceiveCall(code, args);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        try {
            if (isVisibleToUser && overrideActivityDefaults) {
                if (menuId < 0)
                    disableOptionsMenu();
                else
                    enableOptionsMenu(menuId);

                if (title != null) setTitle(title);
                if (subtitle != null) setSubtitle(subtitle);

                setBackButtonEnabled(backButton);
            }
        } catch (Exception e) {
        }

        try {
            if (isVisibleToUser)
                onBecomeVisible();
        } catch (Exception e) {
        }
    }

    public void onBecomeVisible() {
    }

    public void enableOptionsMenu(int menu) {
        if (getActivity() != null)
            this.setHasOptionsMenu(true);
        this.menuId = menu;
    }

    public void disableOptionsMenu() {
        if (getActivity() != null)
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
