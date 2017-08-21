package com.example.chuboy.mystore;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChuBoy on 2017/8/19.
 */

class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> FragmentList = new ArrayList<>();
    private final List<String> TitleList = new ArrayList<>();
    public ViewPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }
    @Override
    public Fragment getItem(int position){
        return FragmentList.get(position);
    }
    @Override
    public int getCount(){
        return FragmentList.size();
    }
    public void addFragment(Fragment fragment, String title){
        FragmentList.add(fragment);
        TitleList.add(title);
    }
    @Override
    public CharSequence getPageTitle(int position){
        return TitleList.get(position);
    }
}
