/*
 * Copyright (c) 2020 ProDev+ (Pascal Gerner).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simplelib.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.simplelib.interfaces.VisibilityAdapter;

public class FragmentPageChangeListener implements ViewPager.OnAdapterChangeListener, ViewPager.OnPageChangeListener {
    private final ViewPager pager;

    private Fragment currentFragment;

    public FragmentPageChangeListener(ViewPager pager) {
        this(pager, true);
    }

    public FragmentPageChangeListener(ViewPager pager, boolean attach) {
        if (pager == null)
            throw new NullPointerException("No ViewPager attached");

        this.pager = pager;

        if (attach)
            attach();
    }

    public FragmentPageChangeListener attach() {
        try {
            pager.addOnAdapterChangeListener(this);
            pager.addOnPageChangeListener(this);
        } catch (Exception e) {
        }
        return this;
    }

    public FragmentPageChangeListener detach() {
        try {
            pager.removeOnAdapterChangeListener(this);
            pager.removeOnPageChangeListener(this);
        } catch (Exception e) {
        }
        return this;
    }

    @Override
    public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
        if (newAdapter == null) {
            if (currentFragment instanceof VisibilityAdapter) {
                VisibilityAdapter visibilityAdapter = (VisibilityAdapter) currentFragment;
                visibilityAdapter.setVisibility(false, false);
            }

            currentFragment = null;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageSelected(int position) {
        Fragment fragment = position >= 0 ? getFragmentAt(position) : null;
        if (fragment == null)
            return;

        if (fragment != currentFragment && currentFragment instanceof VisibilityAdapter) {
            VisibilityAdapter visibilityAdapter = (VisibilityAdapter) currentFragment;
            visibilityAdapter.setVisibility(false, false);
        }

        if (fragment instanceof VisibilityAdapter) {
            VisibilityAdapter visibilityAdapter = (VisibilityAdapter) fragment;
            visibilityAdapter.setVisibility(true, false);
        }

        currentFragment = fragment;
    }

    protected Fragment getFragmentAt(int index) {
        PagerAdapter adapter = pager.getAdapter();
        if (adapter == null)
            return null;

        try {
            if (adapter instanceof FragmentStatePagerAdapter)
                return ((FragmentStatePagerAdapter) adapter).getItem(index);
            if (adapter instanceof FragmentPagerAdapter)
                return ((FragmentPagerAdapter) adapter).getItem(index);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
