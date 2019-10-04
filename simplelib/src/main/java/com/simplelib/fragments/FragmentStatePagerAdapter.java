package com.simplelib.fragments;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.simplelib.SimpleFragment;
import com.simplelib.interfaces.NameableAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class FragmentStatePagerAdapter extends SimpleFragmentStatePagerAdapter {
    private ViewPager viewPager;
    private Context context;

    private ArrayList<Page> pages;
    private boolean noPosition;

    private boolean makeAllPagesSwipeable;

    private Data data;

    public FragmentStatePagerAdapter(ViewPager viewPager, FragmentManager fm, int behavior) {
        this(viewPager, fm, behavior, true);
    }

    public FragmentStatePagerAdapter(ViewPager viewPager, FragmentManager fm, int behavior, boolean attach) {
        super(fm, behavior);

        if (viewPager == null)
            throw new NullPointerException("No view pager");

        this.viewPager = viewPager;
        this.context = viewPager.getContext();

        this.pages = new ArrayList<>();
        this.noPosition = false;

        this.makeAllPagesSwipeable = true;

        this.data = new Data(pages);

        if (attach)
            attach();
    }

    public void setViewPager(ViewPager viewPager) {
        if (viewPager == null)
            throw new NullPointerException("No view pager");

        try {
            if (this.viewPager != viewPager)
                detach();
        } catch (Exception e) {
        }

        this.viewPager = viewPager;

        try {
            attach();
        } catch (Exception e) {
        }
    }

    public boolean attach() {
        try {
            if (this.viewPager != null &&
                    this.viewPager.getAdapter() != this) {
                this.viewPager.setAdapter(this);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean detach() {
        try {
            if (this.viewPager != null &&
                    this.viewPager.getAdapter() == this) {
                this.viewPager.setAdapter(null);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public Data getData() {
        if (data == null)
            data = new Data(getPages());
        return data;
    }

    public void setData(Data data) {
        if (data == null) return;

        try {
            this.pages.clear();

            ArrayList<Page> pagesList = data.getPages();
            if (pagesList != null)
                this.pages.addAll(pagesList);
        } catch (Exception e) {
        }

        update();
    }

    public void setMakeAllPagesSwipeable(boolean makeAllPagesSwipeable) {
        this.makeAllPagesSwipeable = makeAllPagesSwipeable;
    }

    public void add(Fragment fragment) {
        add(null, fragment);
    }

    public void add(int titleId, Fragment fragment) {
        String title = null;
        try {
            if (context != null)
                title = context.getString(titleId);
        } catch (Exception e) {
        }
        add(title, fragment);
    }

    public void add(String title, Fragment fragment) {
        if (fragment != null && !pages.contains(fragment)) {
            Page page = new Page(title, fragment);

            pages.add(page);
            update();

            int index = pages.indexOf(page);
            if (index >= 0 && viewPager != null) {
                viewPager.setCurrentItem(index, true);
            }
        }
    }

    public void remove(Fragment fragment) {
        remove(fragment, true);
    }

    public void remove(Fragment fragment, boolean updateEntirely) {
        if (fragment != null) {
            int index = indexOfFragment(fragment);

            if (index >= 0 && index < pages.size()) {
                pages.remove(index);

                if (updateEntirely)
                    updateEntirely();
                else
                    update();
            }
        }
    }

    public void remove(int index) {
        remove(index, true);
    }

    public void remove(int index, boolean updateEntirely) {
        if (index >= pages.size())
            index = pages.size() - 1;
        if (index < 0)
            index = 0;

        if (pages.size() > 0) {
            pages.remove(index);

            if (updateEntirely)
                updateEntirely();
            else
                update();
        }
    }

    public void update() {
        try {
            onUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        notifyDataSetChanged();
    }

    public void updateEntirely() {
        try {
            onUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        noPosition = true;
        notifyDataSetChanged();
        noPosition = false;
    }

    public ArrayList<Page> getPages() {
        return pages;
    }

    @Override
    public Fragment getItem(int position) {
        Page page = null;
        try {
            page = pages.get(position);
        } catch (Exception e) {
        }

        try {
            if (makeAllPagesSwipeable && page != null && page.getFragment() != null) {
                Fragment fragment = page.getFragment();
                if (fragment != null && fragment instanceof SimpleFragment) {
                    View contentView = ((SimpleFragment) fragment).getContentView();
                    if (contentView != null) contentView.setClickable(true);
                }
                if (fragment != null && fragment.getView() != null) {
                    View contentView = fragment.getView();
                    if (contentView != null) contentView.setClickable(true);
                }
            }
        } catch (Exception e) {
        }

        try {
            if (page != null && page.getFragment() != null)
                return page.getFragment();
        } catch (Exception e) {
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        Page page = null;
        try {
            page = pages.get(position);
        } catch (Exception e) {
        }

        try {
            if (page != null) {
                String title = page.getTitle();
                if (title != null && title.length() > 0)
                    return title;
            }
        } catch (Exception e) {
        }

        try {
            if (page != null) {
                Fragment fragment = page.getFragment();
                if (fragment != null && fragment instanceof NameableAdapter) {
                    String name = ((NameableAdapter) fragment).getName();
                    if (name != null && name.length() > 0)
                        return name;
                }
                if (fragment != null && fragment instanceof SimpleFragment) {
                    String name = ((SimpleFragment) fragment).getTitle();
                    if (name != null && name.length() > 0)
                        return name;
                }
            }
        } catch (Exception e) {
        }

        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        if (noPosition)
            return POSITION_NONE;

        int index = indexOfObject(object);

        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    public int indexOfObject(Object obj) {
        try {
            if (obj != null && obj instanceof Fragment)
                return indexOfFragment((Fragment) obj);
        } catch (Exception e) {
        }
        return -1;
    }

    public int indexOfFragment(Fragment fragment) {
        if (fragment == null) return -1;

        if (pages != null && pages.size() > 0) {
            for (int pos = 0; pos < pages.size(); pos++) {
                try {
                    Page page = pages.get(pos);
                    if (page == null) continue;

                    if (page.getFragment() == fragment)
                        return pos;
                } catch (Exception e) {
                }
            }
        }

        return -1;
    }

    protected void onUpdate() {
        try {
            for (Page page : pages) {
                if (page == null) continue;

                Fragment fragment = page.getFragment();
                if (fragment == null) continue;

                try {
                    Class<?> fragmentClass = fragment.getClass();
                    Field field = fragmentClass.getDeclaredField("willResumeOnlyCurrentFragment");
                    if (field == null) continue;

                    try {
                        field.setAccessible(true);
                    } catch (Exception e) {
                    }

                    field.set(fragment, true);
                } catch (Exception e) {
                } catch (Throwable tr) {
                    tr.printStackTrace();
                }
            }
        } catch (Exception e) {
        }

        //Override this method if needed
    }

    public static class Page {
        private String title;
        private Fragment fragment;

        public Page(String title, Fragment fragment) {
            this.title = title;
            this.fragment = fragment;

            if (fragment == null)
                throw new NullPointerException("No fragment");
        }

        public String getTitle() {
            return title;
        }

        public Fragment getFragment() {
            return fragment;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof Page) {
                Page src = (Page) obj;
                return src.title == title && src.fragment == fragment;
            }
            if (obj != null && obj instanceof Fragment) {
                Fragment src = (Fragment) obj;
                return src == fragment;
            }
            return super.equals(obj);
        }
    }

    public static final class Data {
        private ArrayList<Page> pages;

        public Data(ArrayList<Page> pages) {
            if (pages == null)
                pages = new ArrayList<>();
            this.pages = pages;
        }

        public ArrayList<Page> getPages() {
            return pages;
        }
    }
}