package com.client.therevgo.fragments.followUp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.therevgo.R;
import com.client.therevgo.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shubham on 31/10/16.
 */

public class ShowFollowUpContainerFragment extends Fragment {

    public static final String TAG = ShowFollowUpContainerFragment.class.getName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewpagerAdater adapter;
    private FragmentManager fragmentManager;

    private boolean isLoadOne=true ,isLoadTwo=true ;
     int pos = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_pager, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.tab);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pos = position ;
                if(pos ==0 && isLoadOne) {
                    isLoadOne = false ;
                    DateFollowFragment fragment = (DateFollowFragment) adapter.getItem(pos);
                    fragment.refreshFollowList();
                } else if ( pos ==1 && isLoadTwo) {
                    isLoadTwo = false ;
                    DateFollowFragment fragment = (DateFollowFragment) adapter.getItem(pos);
                    fragment.refreshFollowList();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        fragmentManager = getChildFragmentManager();
        setUpPager();
        return view;
    }

    private void setUpPager() {
        adapter = new ViewpagerAdater(fragmentManager);
        adapter.AddData(DateFollowFragment.newInstance(1,DateFollowFragment.TODAY) , "Today");
        adapter.AddData(DateFollowFragment.newInstance(1,DateFollowFragment.YESTERDAY) , "Yesterday");
        adapter.AddData(new CustomDateFilterFollowUpFragment() , "All");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private class ViewpagerAdater extends FragmentStatePagerAdapter {

        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> titlelist = new ArrayList<>();

        ViewpagerAdater(FragmentManager fm) {
            super(fm);
        }

        void AddData(Fragment fragment , String title){
            fragmentList.add(fragment);
            titlelist.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titlelist.get(position);
        }
    }
}
