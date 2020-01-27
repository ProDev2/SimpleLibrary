package com.simplelib;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.simplelib.interfaces.InitializeAdapter;
import com.simplelib.interfaces.OnEvent;
import com.simplelib.interfaces.UpdateAdapter;
import com.simplelib.interfaces.VisibilityAdapter;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SimpleFragment extends Fragment
        implements OnEvent, InitializeAdapter, UpdateAdapter, VisibilityAdapter {
    // States
    private AtomicBoolean stateInitialized;
    private AtomicBoolean stateNeedsUpdate;
    private AtomicBoolean stateVisibility;

    // General
    private int id;
    private View contentView;

    protected boolean overrideActivityDefaults;

    private String title, subtitle;
    private boolean backButton;

    private int menuId;
    private Menu menu;

    public boolean willResumeOnlyCurrentFragment = true;
    private AtomicBoolean resumed;

    public SimpleFragment(int id) {
        this.id = id;

        resetToolbar();

        setInitialized(false);
        setNeedsUpdate(true);
        setDefVisibility(false);
    }

    @NonNull
    public final AtomicBoolean getInitializedState() {
        if (stateInitialized == null)
            stateInitialized = new AtomicBoolean(false);
        return stateInitialized;
    }

    @NonNull
    public final AtomicBoolean getNeedsUpdateState() {
        if (stateNeedsUpdate == null)
            stateNeedsUpdate = new AtomicBoolean(false);
        return stateNeedsUpdate;
    }

    @NonNull
    public final AtomicBoolean getVisibleState() {
        if (stateVisibility == null)
            stateVisibility = new AtomicBoolean(false);
        return stateVisibility;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!willResumeOnlyCurrentFragment)
            setInitialized(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(id, container, false);
        this.contentView = view;

        onRestoreInstanceState(savedInstanceState);

        if (overrideActivityDefaults || getActivity() == null)
            resetToolbar();

        create(view, savedInstanceState);

        if (!willResumeOnlyCurrentFragment) {
            setInitialized(true);
            update(false);
        }

        return view;
    }

    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null)
            return;

        overrideActivityDefaults = savedInstanceState.getBoolean("overrideActivityDefaults", overrideActivityDefaults);

        title = savedInstanceState.getString("title", title);
        subtitle = savedInstanceState.getString("subtitle", subtitle);

        backButton = savedInstanceState.getBoolean("backButton", backButton);

        menuId = savedInstanceState.getInt("menuId", menuId);

        willResumeOnlyCurrentFragment = savedInstanceState.getBoolean("willResumeOnlyCurrentFragment", willResumeOnlyCurrentFragment);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState == null)
            return;

        outState.putBoolean("overrideActivityDefaults", overrideActivityDefaults);

        outState.putString("title", title);
        outState.putString("subtitle", subtitle);

        outState.putBoolean("backButton", backButton);

        outState.putInt("menuId", menuId);

        outState.putBoolean("willResumeOnlyCurrentFragment", willResumeOnlyCurrentFragment);
    }

    public View getContentView() {
        return contentView;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T findViewById(int id) {
        try {
            return (T) getContentView().findViewById(id);
        } catch (Exception e) {
        }
        return null;
    }

    public abstract void create(View view, Bundle savedInstanceState);

    @Override
    public void onUpdate() {
        updateVisibility();
    }

    @Override
    public void onVisibilitySet(boolean visible) {
        try {
            if (visible && overrideActivityDefaults) {
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
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
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
            if (getVisibility() && getActivity() instanceof SimpleActivity) {
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
            if (getVisibility() && getActivity() instanceof SimpleActivity) {
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
            if (getVisibility() && getActivity() instanceof SimpleActivity) {
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

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public boolean getUserVisibleHint() {
        return super.getUserVisibleHint();
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        setVisibility(isVisibleToUser, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (willResumeOnlyCurrentFragment) {
            if (this.resumed != null) {
                this.resumed.set(true);
                this.resumed = null;
            }

            final AtomicBoolean resumed = new AtomicBoolean(false);
            this.resumed = resumed;

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if (resumed.get()) return;

                    setVisibility(false, false);

                    setInitialized(true);
                    update(false);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (willResumeOnlyCurrentFragment) {
            if (this.resumed != null) {
                this.resumed.set(true);
                this.resumed = null;
            }

            setVisibility(true, false);

            setInitialized(true);
            update(false);
        }
    }

    public void enableOptionsMenu(int menu) {
        if (getVisibility() && getActivity() != null)
            this.setHasOptionsMenu(true);
        this.menuId = menu;
    }

    public void disableOptionsMenu() {
        if (getVisibility() && getActivity() != null)
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
