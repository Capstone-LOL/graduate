package com.example.fordisabled.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.fordisabled.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
//        1) FragmentManager를 받는 생성자 구현
//
//        2) 실제 Fragment를 반환하는 getItem 구현
//
//        3) 페이지의 개수를 반환하는 getCount구현
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragments = new ArrayList<>();

    // Fragment manager 받는 생성자
    public SectionsPagerAdapter( FragmentManager fm) {
        super(fm);

        fragments.add(new Fragment1());
        fragments.add(new Fragment2());
    }

    // 프래그먼트 반환 하는 함수
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return fragments.get(position);
    }

//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return mContext.getResources().getString(TAB_TITLES[position]);
//    }

    // tab 페이지 개수 반환
    @Override
    public int getCount() {

        return fragments.size();
    }
}