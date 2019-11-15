package com.simplelib.pager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.simplelib.SimpleFragment;
import com.simplelib.interfaces.NameableAdapter;
import com.simplelib.interfaces.VisibilityAdapter;

import java.util.List;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
    public boolean CHANGE_VISIBILITY = true;

    public SimpleFragmentPagerAdapter(@NonNull FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public SimpleFragmentPagerAdapter(@NonNull FragmentManager fragmentManager, @Nullable List<Page> pages) {
        super(fragmentManager, pages);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        try {
            CharSequence title = super.getPageTitle(position);
            if (title != null && title.length() > 0)
                return title;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Page page = getPageAtOrThrow(position);
            Fragment fragment = page.getFragment();

            if (fragment instanceof NameableAdapter) {
                String name = ((NameableAdapter) fragment).getName();
                if (name != null && name.length() > 0)
                    return name;
            }
            if (fragment instanceof SimpleFragment) {
                String name = ((SimpleFragment) fragment).getTitle();
                if (name != null && name.length() > 0)
                    return name;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void setVisible(@NonNull Fragment fragment, boolean visible) {
        super.setVisible(fragment, visible);

        if (CHANGE_VISIBILITY && fragment instanceof VisibilityAdapter) {
            VisibilityAdapter visibilityAdapter = (VisibilityAdapter) fragment;
            visibilityAdapter.setVisibility(visible, false);
        }
    }
}
