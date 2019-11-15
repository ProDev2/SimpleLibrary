package com.simplelib.fragments;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.simplelib.interfaces.VisibilityAdapter;

public abstract class SimpleAbstractFragmentPagerAdapter extends FragmentPagerAdapter {
    public boolean CHANGE_VISIBILITY = true;

    private Fragment currentItem;

    public SimpleAbstractFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Object instantiatedObj = super.instantiateItem(container, position);

        if (CHANGE_VISIBILITY && instantiatedObj instanceof VisibilityAdapter) {
            VisibilityAdapter visibilityAdapter = (VisibilityAdapter) instantiatedObj;
            visibilityAdapter.setVisibility(false, false);
        }

        return instantiatedObj;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);

        if (object instanceof Fragment && currentItem != null && object.equals(currentItem))
            currentItem = null;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (object instanceof Fragment && object != currentItem) {
            Fragment fragment = (Fragment) object;

            if (CHANGE_VISIBILITY && currentItem instanceof VisibilityAdapter) {
                VisibilityAdapter visibilityAdapter = (VisibilityAdapter) currentItem;
                visibilityAdapter.setVisibility(false, false);
            }

            currentItem = null;
        }

        super.setPrimaryItem(container, position, object);

        if (object instanceof Fragment && object != currentItem) {
            Fragment fragment = (Fragment) object;

            if (CHANGE_VISIBILITY && fragment instanceof VisibilityAdapter) {
                VisibilityAdapter visibilityAdapter = (VisibilityAdapter) fragment;
                visibilityAdapter.setVisibility(true, false);
            }

            currentItem = fragment;
        }
    }
}
