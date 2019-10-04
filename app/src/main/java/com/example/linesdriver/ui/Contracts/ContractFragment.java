package com.example.linesdriver.ui.Contracts;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.example.linesdriver.R;
import com.example.linesdriver.TabFragment.ContractsFragment;
import com.example.linesdriver.TabFragment.NewContractsFragment;
import com.google.android.material.tabs.TabLayout;

public class ContractFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        __init__(root);
        return root;
    }
    ViewPager vp;
    TabLayout tab;
    private void __init__(View root) {
        tab=root.findViewById(R.id.tb);
        vp=root.findViewById(R.id.vp);
        vp.setAdapter(new PagerAdapter(getFragmentManager()));
        tab.setupWithViewPager(vp);
        tab.getTabAt(0).setText("Contracts");
        tab.getTabAt(1).setText("New");


    }
    public class  PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 1:
                        return new NewContractsFragment();
                case 0:
                default:
                    return new ContractsFragment();
            }
        }
        @Override
        public int getCount() {
            return 2;
        }
    }
}