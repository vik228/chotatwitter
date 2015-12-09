package com.chotatwitter.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.chotatwitter.R;

import java.util.Locale;

/**
 * Created by vikas-pc on 10/12/15.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    Context mContext;

    private static final int USERS_TWEET = 0;
    private static final int ALL_SELECTED_TWEETS = 1;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case USERS_TWEET:
                return mContext.getString(R.string.title_section1).toUpperCase(l);
            case ALL_SELECTED_TWEETS:
                return mContext.getString(R.string.title_section2).toUpperCase(l);
        }
        return null;
    }
}
