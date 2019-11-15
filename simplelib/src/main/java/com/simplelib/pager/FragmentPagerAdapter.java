package com.simplelib.pager;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.PagerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FragmentPagerAdapter extends PagerAdapter {
    // Behavior
    public boolean withSecuredExecution = true;
    public boolean withAdapterState = true;
    public boolean withFragmentManagerState = true;

    // Pager adapter
    @NonNull
    protected final FragmentManager fragmentManager;

    @NonNull
    private final List<Page> pages;

    private FragmentTransaction currentFragmentTransaction;
    private Fragment currentFragment;

    private boolean updating;

    public FragmentPagerAdapter(@NonNull FragmentManager fragmentManager) {
        this(fragmentManager, null);
    }

    public FragmentPagerAdapter(@NonNull FragmentManager fragmentManager,
            @Nullable List<Page> pages) {
        if (fragmentManager == null)
            throw new NullPointerException("No fragment manager attached");

        this.fragmentManager = fragmentManager;

        this.pages = new ArrayList<>();

        if (pages != null)
            this.pages.addAll(pages);
    }

    private final void throwIfNoContainer(ViewGroup container) {
        if (container == null)
            throw new NullPointerException("No container attached");
    }

    private final void throwIfUpdating(boolean ifNot) {
        if (!ifNot && updating)
            throw new IllegalStateException("Another update is being processed");
        if (ifNot && !updating)
            throw new IllegalStateException("No update is being processed");
    }

    private final void initializeFragmentTransaction() {
        try {
            if (currentFragmentTransaction == null && fragmentManager != null)
                currentFragmentTransaction = fragmentManager.beginTransaction();
        } catch (Exception e) {
            if (withSecuredExecution)
                e.printStackTrace();
            else
                throw e;
        }
    }

    @Nullable
    public synchronized final Page getPageSafelyAt(int index) {
        synchronized (pages) {
            if (index >= 0 && index < pages.size())
                return pages.get(index);
            else
                return null;
        }
    }

    @Nullable
    public synchronized final Page getPageAt(int index) {
        synchronized (pages) {
            if (index < 0 || index >= pages.size())
                throw new IndexOutOfBoundsException("Page at position " +
                        index +
                        " is not defined");
            else
                return pages.get(index);
        }
    }

    public synchronized final int getPageCount() {
        synchronized (pages) {
            return pages.size();
        }
    }

    @NonNull
    public synchronized final Page getPageAtOrThrow(int position) {
        Page page = getPageAt(position);
        if (page == null)
            throw new NullPointerException("Page is undefined");

        return page;
    }

    public synchronized final int indexOfObject(Object obj) {
        if (obj instanceof Fragment)
            return indexOfFragment((Fragment) obj);

        return -1;
    }

    public synchronized final int indexOfFragment(Fragment fragment) {
        synchronized (pages) {
            if (fragment == null)
                return -1;

            for (int pos = 0; pos < pages.size(); pos++) {
                Page page = pages.get(pos);
                if (page == null) continue;

                if (page.isCreated() && fragment.equals(page.getFragment()))
                    return pos;
            }

            return -1;
        }
    }

    public synchronized final void update() {
        notifyDataSetChanged();
    }

    public synchronized final boolean isEmpty() {
        synchronized (pages) {
            return pages.isEmpty();
        }
    }

    public synchronized final boolean contains(Object obj) {
        synchronized (pages) {
            return pages.contains(obj);
        }
    }

    public synchronized final void clear() {
        synchronized (pages) {
            if (pages.size() <= 0)
                return;

            pages.clear();
            update();
        }
    }

    public synchronized final boolean retainAll(@NonNull Collection<?> collection) {
        if (collection == null)
            throw new NullPointerException("No items attached");

        synchronized (pages) {
            boolean success = pages.retainAll(collection);
            if (success)
                update();
            return success;
        }
    }

    public synchronized final boolean removeAll(@NonNull Collection<?> collection) {
        if (collection == null)
            throw new NullPointerException("No items attached");

        synchronized (pages) {
            boolean success = pages.removeAll(collection);
            if (success)
                update();
            return success;
        }
    }

    public synchronized final Page remove(int index) {
        synchronized (pages) {
            Page page = pages.remove(index);
            update();
            return page;
        }
    }

    public synchronized final boolean remove(@NonNull Page page) {
        if (page == null)
            throw new NullPointerException("No page attached");

        synchronized (pages) {
            boolean success = pages.remove(page);
            if (success)
                update();
            return success;
        }
    }

    public synchronized final void addAll(int index,
            @NonNull Collection<? extends Page> pageCollection) {
        if (pageCollection == null)
            throw new NullPointerException("No pages attached");

        synchronized (pages) {
            pages.addAll(index, pageCollection);
            update();
        }
    }

    public synchronized final void addAll(@NonNull Collection<? extends Page> pageCollection) {
        if (pageCollection == null)
            throw new NullPointerException("No pages attached");

        synchronized (pages) {
            pages.addAll(pageCollection);
            update();
        }
    }

    public synchronized final void add(int index, @NonNull Fragment fragment) {
        add(index, fragment, null);
    }

    public synchronized final void add(@NonNull Fragment fragment) {
        add(fragment, null);
    }

    public synchronized final void add(int index,
            @NonNull Fragment fragment,
            @Nullable String title) {
        if (fragment == null)
            throw new NullPointerException("No fragment attached");

        Page page = new Page(fragment, title);
        add(index, page);
    }

    public synchronized final void add(@NonNull Fragment fragment, @Nullable String title) {
        if (fragment == null)
            throw new NullPointerException("No fragment attached");

        Page page = new Page(fragment, title);
        add(page);
    }

    public synchronized final void add(int index, @NonNull Page page) {
        if (page == null)
            throw new NullPointerException("No page attached");

        synchronized (pages) {
            pages.add(index, page);
            update();
        }
    }

    public synchronized final void add(@NonNull Page page) {
        if (page == null)
            throw new NullPointerException("No page attached");

        synchronized (pages) {
            pages.add(page);
            update();
        }
    }

    public synchronized final Page set(int index, @NonNull Page page) {
        if (page == null)
            throw new NullPointerException("No page attached");

        synchronized (pages) {
            Page previousPage = pages.set(index, page);
            update();
            return previousPage;
        }
    }

    public synchronized final boolean isUpdating() {
        return updating;
    }

    @Override
    public synchronized void startUpdate(@NonNull ViewGroup container) {
        throwIfNoContainer(container);

        if (container.getId() == View.NO_ID)
            throw new IllegalStateException("ViewPager with adapter " +
                    this +
                    " requires a view id");

        throwIfUpdating(false);
        updating = true;
    }

    @NonNull
    @Override
    public synchronized Object instantiateItem(@NonNull ViewGroup container, int position) {
        throwIfNoContainer(container);

        throwIfUpdating(true);

        Page page = getPageAtOrThrow(position);
        Fragment fragment = page.getFragment();

        if (fragment != null && page.isActive())
            return fragment;

        // Saved State
        if (!page.isActive()) {
            try {
                Fragment.SavedState savedState = page.removeSavedState();
                if (savedState != null)
                    fragment.setInitialSavedState(savedState);
            } catch (Exception e) {
                if (withSecuredExecution)
                    e.printStackTrace();
                else
                    throw e;
            }
        }

        // Fragment
        setVisible(fragment, false);

        // Page
        page.setActive(true);

        // Fragment Transaction
        initializeFragmentTransaction();

        try {
            currentFragmentTransaction.add(container.getId(), fragment);
            currentFragmentTransaction.setMaxLifecycle(fragment, Lifecycle.State.STARTED);
        } catch (Exception e) {
            if (withSecuredExecution)
                e.printStackTrace();
            else
                throw e;
        }

        return fragment;
    }

    @Override
    public synchronized void destroyItem(@NonNull ViewGroup container,
            int position,
            @NonNull Object object) {
        throwIfNoContainer(container);

        throwIfUpdating(true);

        if (!(object instanceof Fragment))
            throw new IllegalArgumentException("Object cannot be destroyed");

        Fragment fragment = (Fragment) object;

        // Page
        Page page = getPageSafelyAt(position);
        if (page != null && fragment.equals(page.getFragment())) {
            // Saved State
            if (page.isActive()) {
                try {
                    Fragment.SavedState savedState = fragmentManager.saveFragmentInstanceState(
                            fragment);
                    page.setSavedState(savedState);
                } catch (Exception e) {
                    if (withSecuredExecution)
                        e.printStackTrace();
                    else
                        throw e;
                }
            }

            // Page
            page.setActive(false);
        }

        // Fragment Transaction
        initializeFragmentTransaction();

        try {
            currentFragmentTransaction.remove(fragment);
        } catch (Exception e) {
            if (withSecuredExecution)
                e.printStackTrace();
            else
                throw e;
        }

        // Fragment
        if (currentFragment != null && fragment.equals(currentFragment))
            currentFragment = null;
    }

    @Override
    public synchronized void setPrimaryItem(@NonNull ViewGroup container,
            int position,
            @NonNull Object object) {
        throwIfNoContainer(container);

        throwIfUpdating(true);

        if (!(object instanceof Fragment))
            throw new IllegalArgumentException("Object cannot be set");

        Fragment fragment = (Fragment) object;
        if (currentFragment != null && fragment.equals(currentFragment))
            return;

        // Fragment Transaction & Fragment
        initializeFragmentTransaction();

        // Handle old
        if (currentFragment != null) {
            setVisible(currentFragment, false);

            try {
                currentFragmentTransaction.setMaxLifecycle(currentFragment,
                        Lifecycle.State.STARTED);
            } catch (Exception e) {
                if (withSecuredExecution)
                    e.printStackTrace();
                else
                    throw e;
            }
        }

        // Handle new
        if (fragment != null) {
            setVisible(fragment, true);

            try {
                currentFragmentTransaction.setMaxLifecycle(fragment, Lifecycle.State.RESUMED);
            } catch (Exception e) {
                if (withSecuredExecution)
                    e.printStackTrace();
                else
                    throw e;
            }
        }

        currentFragment = fragment;
    }

    @Override
    public synchronized void finishUpdate(@NonNull ViewGroup container) {
        throwIfNoContainer(container);

        throwIfUpdating(true);
        updating = false;

        if (currentFragmentTransaction != null) {
            try {
                currentFragmentTransaction.commitNowAllowingStateLoss();
            } catch (IllegalStateException e) {
                currentFragmentTransaction.commitAllowingStateLoss();
            }
            currentFragmentTransaction = null;
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        if (view == null)
            throw new NullPointerException("No view attached");
        if (!(object instanceof Fragment))
            throw new IllegalArgumentException("Object is not valid");

        return ((Fragment) object).getView() == view;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        int index = indexOfObject(object);

        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }

    @Override
    public int getCount() {
        return getPageCount();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        Page page = getPageAtOrThrow(position);
        return page.getTitle();
    }

    @Nullable
    @Override
    public synchronized final Parcelable saveState() {
        if (!withAdapterState)
            return null;

        Bundle bundle = new Bundle();

        // Save adapter state
        Parcelable adapterState = saveAdapterState();
        if (adapterState != null)
            bundle.putParcelable("adapter_state", adapterState);

        // Save pages
        synchronized (pages) {
            if (pages.size() > 0) {
                Parcelable[] pageArray = new Parcelable[pages.size()];
                for (int pos = 0; pos < pages.size(); pos++) {
                    Page page = pages.get(pos);
                    if (page == null)
                        continue;

                    Bundle pageState = new Bundle();

                    // Put fragment state
                    Parcelable fragmentState;
                    if (page.isCreated()) {
                        Fragment fragment = page.getFragment();
                        fragmentState = saveFragmentAsState(fragment);
                    } else {
                        fragmentState = page.getFragmentState();
                    }
                    if (fragmentState != null)
                        pageState.putParcelable("fragment_state", fragmentState);

                    // Put fragment
                    if (withFragmentManagerState && page.isActive()) {
                        Fragment fragment = page.getFragment();
                        try {
                            if (fragmentManager != null && fragment != null)
                                fragmentManager.putFragment(pageState, "fragment", fragment);
                        } catch (Exception e) {
                            if (withSecuredExecution)
                                e.printStackTrace();
                            else
                                throw e;
                        }
                    }

                    // Put saved state
                    Fragment.SavedState savedState = page.getSavedState();
                    if (page.isActive() && savedState == null) {
                        Fragment fragment = page.getFragment();
                        try {
                            if (fragmentManager != null)
                                savedState = fragmentManager.saveFragmentInstanceState(fragment);
                        } catch (Exception e) {
                            if (withSecuredExecution)
                                e.printStackTrace();
                            else
                                throw e;
                        }
                    }
                    pageState.putParcelable("saved_state", savedState);

                    // Put title
                    String title = page.getTitle();
                    pageState.putString("title", title);

                    // Put args
                    Bundle args = page.hasArgs() ? page.getArgs() : null;
                    pageState.putParcelable("args", args);

                    // Set saved page state
                    pageArray[pos] = pageState;
                }
                bundle.putParcelableArray("pages", pageArray);
            }
        }

        return bundle;
    }

    @Override
    public synchronized final void restoreState(@Nullable Parcelable state,
            @Nullable ClassLoader loader) {
        if (!withAdapterState)
            return;

        if (state == null)
            return;
        if (!(state instanceof Bundle))
            throw new IllegalArgumentException("State cannot be restored");

        Bundle bundle = (Bundle) state;
        if (loader != null)
            bundle.setClassLoader(loader);

        // Restore adapter state
        Parcelable adapterState = bundle.getParcelable("adapter_state");
        if (adapterState != null)
            restoreAdapterState(adapterState, loader);

        // Restore pages
        synchronized (pages) {
            Parcelable[] pageArray = bundle.getParcelableArray("pages");
            if (pageArray != null) {
                pages.clear();

                for (int pos = 0; pos < pageArray.length; pos++) {
                    Parcelable pageParcelable = pageArray[pos];
                    if (!(pageParcelable instanceof Bundle))
                        continue;

                    Bundle pageState = (Bundle) pageParcelable;
                    if (loader != null)
                        pageState.setClassLoader(loader);

                    // Get fragment or fragment creator
                    Fragment fragment = null;
                    Page.FragmentCreator fragmentCreator = null;
                    boolean active = false;

                    // Get fragment
                    if (withFragmentManagerState) {
                        try {
                            if (fragment == null && fragmentManager != null) {
                                fragment = fragmentManager.getFragment(pageState, "fragment");
                                active = fragment != null &&
                                        fragment.getFragmentManager() == fragmentManager;
                            }
                        } catch (Exception e) {
                            if (withSecuredExecution)
                                e.printStackTrace();
                            else
                                throw e;
                        }
                    }

                    // Get fragment state
                    if (fragment == null && fragmentCreator == null) {
                        Parcelable fragmentState = pageState.getParcelable("fragment_state");
                        if (fragmentState != null)
                            fragmentCreator = new RestoreFragmentCreator(fragmentState, loader);
                    }

                    // Skip page if fragment cannot be restored
                    if (fragment == null && fragmentCreator == null)
                        continue;

                    // Get saved state
                    Fragment.SavedState savedState = pageState.getParcelable("saved_state");

                    // Get title
                    String title = pageState.getString("title", null);

                    // Get args
                    Bundle args = pageState.getParcelable("args");

                    // Restore Page
                    Page page;
                    if (fragment != null)
                        page = new Page(fragment);
                    else
                        page = new Page(fragmentCreator);

                    page.setSavedState(savedState);
                    page.setTitle(title);

                    page.setArgs(args);

                    page.setActive(active);

                    // Add restored page
                    pages.add(page);
                }

                update();
            }
        }
    }

    protected void setVisible(@NonNull Fragment fragment, boolean visible) {
        if (fragment != null)
            fragment.setMenuVisibility(visible);
    }

    @Nullable
    protected Parcelable saveAdapterState() {
        return null;
    }

    protected void restoreAdapterState(@Nullable Parcelable parcelable,
            @Nullable ClassLoader loader) {
    }

    @Nullable
    protected Parcelable saveFragmentAsState(@NonNull Fragment fragment) {
        if (fragment == null)
            throw new NullPointerException("No fragment attached");

        Class<?> cls = fragment.getClass();
        if (cls == null)
            throw new IllegalArgumentException("No class found");

        Bundle state = new Bundle();
        state.putSerializable("fragment_cls", cls);
        return state;
    }

    @NonNull
    protected Fragment restoreFragmentFromState(@NonNull Parcelable parcelable,
            @Nullable ClassLoader loader) {
        if (parcelable == null)
            throw new NullPointerException("No fragment state attached");
        if (!(parcelable instanceof Bundle))
            throw new IllegalArgumentException("Given parcelable cannot be used");

        Bundle state = (Bundle) parcelable;
        if (loader != null)
            state.setClassLoader(loader);

        Serializable clsSerializable = state.getSerializable("fragment_cls");
        if (!(clsSerializable instanceof Class<?>))
            throw new IllegalArgumentException("Fragment class is missing");

        try {
            @SuppressWarnings("unchecked")
            Class<? extends Fragment> cls = (Class<? extends Fragment>) clsSerializable;
            return cls.newInstance();
        } catch (Throwable tr) {
            throw new RuntimeException("Fragment could not be created", tr);
        }
    }

    protected final class RestoreFragmentCreator implements Page.FragmentCreator {
        @NonNull
        private final Parcelable fragmentState;

        @Nullable
        private final ClassLoader loader;

        public RestoreFragmentCreator(@NonNull Parcelable fragmentState,
                @Nullable ClassLoader loader) {
            if (fragmentState == null)
                throw new NullPointerException("No fragment state attached");

            this.fragmentState = fragmentState;
            this.loader = loader;
        }

        @NonNull
        @Override
        public Fragment create() {
            return restoreFragmentFromState(fragmentState, loader);
        }

        @Nullable
        @Override
        public Parcelable getFragmentState() {
            return fragmentState;
        }

        @Nullable
        public ClassLoader getLoader() {
            return loader;
        }
    }

    public static final class Page {
        private Fragment fragment;
        private FragmentCreator fragmentCreator;

        private Fragment.SavedState savedState;

        private String title;

        private Bundle args;

        private boolean active;

        public Page(@NonNull Fragment fragment) {
            this(fragment, null);
        }

        public Page(@NonNull Fragment fragment, @Nullable String title) {
            if (fragment == null)
                throw new NullPointerException("No fragment attached");

            this.fragment = fragment;
            this.savedState = null;

            this.title = title;

            this.active = false;
        }

        public Page(@NonNull FragmentCreator fragmentCreator) {
            this(fragmentCreator, null);
        }

        public Page(@NonNull FragmentCreator fragmentCreator, @Nullable String title) {
            if (fragmentCreator == null)
                throw new NullPointerException("No fragment creator attached");

            this.fragmentCreator = fragmentCreator;
            this.savedState = null;

            this.title = title;

            this.active = false;
        }

        @NonNull
        public final FragmentCreator getFragmentCreator() {
            if (isCreated())
                throw new IllegalStateException("Fragment is already created");
            if (fragmentCreator == null)
                throw new IllegalStateException("No fragment creator attached");
            return fragmentCreator;
        }

        @Nullable
        public final Parcelable getFragmentState() {
            return getFragmentCreator().getFragmentState();
        }

        public final boolean isCreated() {
            if (fragment == null && fragmentCreator == null)
                throw new IllegalStateException("No fragment creator attached");
            return fragment != null;
        }

        @NonNull
        public final Fragment getFragment() {
            if (fragment == null && fragmentCreator != null)
                fragment = fragmentCreator.create();
            if (fragment == null)
                throw new IllegalStateException("No fragment available");
            fragmentCreator = null;

            return fragment;
        }

        @Nullable
        public Fragment.SavedState getSavedState() {
            return savedState;
        }

        @Nullable
        public Fragment.SavedState removeSavedState() {
            return setSavedState(null);
        }

        @Nullable
        public Fragment.SavedState setSavedState(@Nullable Fragment.SavedState savedState) {
            Fragment.SavedState previouslySavedState = this.savedState;
            this.savedState = savedState;
            return previouslySavedState;
        }

        @Nullable
        public String getTitle() {
            return title;
        }

        public void setTitle(@Nullable String title) {
            this.title = title;
        }

        public synchronized final void clearArgs() {
            args.clear();
            args = null;
        }

        public synchronized final boolean hasArgs() {
            return args != null && !args.isEmpty();
        }

        @NonNull
        public synchronized final Bundle getArgs() {
            if (args == null)
                args = new Bundle();
            return args;
        }

        public synchronized final void setArgs(@Nullable Bundle args) {
            this.args = args;
        }

        public final boolean isActive() {
            return active;
        }

        private final void setActive(boolean active) {
            this.active = active;
        }

        public interface FragmentCreator {
            @NonNull
            Fragment create();

            @Nullable
            Parcelable getFragmentState();
        }
    }
}
