package com.xingqige.criminalintent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xingqige.criminalintent.lab.CrimeLab;
import com.xingqige.criminalintent.model.Crime;

import java.util.List;

public class CrimePagerActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        mViewPager = (ViewPager)findViewById(R.id.crime_view_pager);
        mCrimes = CrimeLab.get(this).getCrimes();

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                Crime crime = mCrimes.get(i);
                return CrimeFragment.newInstance(crime.getmId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
    }
}
