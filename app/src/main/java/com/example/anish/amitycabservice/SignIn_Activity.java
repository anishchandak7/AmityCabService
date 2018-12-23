package com.example.anish.amitycabservice;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

//import io.fabric.sdk.android.Fabric;

public class SignIn_Activity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    //Trace myTrace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    //  myTrace= FirebasePerformance.getInstance().newTrace("test_trace");
    //    myTrace.start();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_);

        tabLayout= findViewById(R.id.tabLayout);
        viewPager= findViewById(R.id.ViewPager);

        viewPager.setAdapter(new CustomAdapter(getSupportFragmentManager(),getApplicationContext()));

        tabLayout.setupWithViewPager(viewPager);

    //    Fabric.with(this, new Crashlytics());

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());

            }
        });
    }

    @Override
    protected void onStop() {
    //    myTrace.stop();
        super.onStop();
    }

    private class CustomAdapter extends FragmentPagerAdapter {

        private String fragments[]={"SIGN IN","SIGN UP"};

        public CustomAdapter(FragmentManager supportFragmentManager, Context applicationContext) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new signin();
                case 1:
                    return new signup();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragments[position];
        }
    }
}

