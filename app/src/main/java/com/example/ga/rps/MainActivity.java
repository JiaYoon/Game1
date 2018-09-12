package com.example.ga.rps;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.ga.rps.fragment.CameraPreviewFragment;
import com.example.ga.rps.fragment.ModeChangeFragment;

/**
 * Created by GA on 2018. 4. 10..
 */

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(2);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {
                Log.d("asd", "" + position);
                final Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + 2);
                if (page == null) return;

                if (position < 2) {
                    viewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((CameraPreviewFragment) page).releaseCamera();
                            final Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + position);
                            if (page == null) return;
                            ((ModeChangeFragment) page).startAnimation();

                        }
                    }, 200);
                } else {
                    viewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((CameraPreviewFragment) page).setCamera();
                        }
                    }, 300);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + 2);
        if (page == null) return;
        ((CameraPreviewFragment) page).setCamera();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 2:
                    return new CameraPreviewFragment();
                case 1:
                    return ModeChangeFragment.getInstance(R.layout.fragment_mode_selection_2);
                default:
                    return ModeChangeFragment.getInstance(R.layout.fragment_mode_selection_1);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public void showCameraPage() {
        viewPager.setCurrentItem(2, true);
    }

    public void showGameMode() {
        viewPager.setCurrentItem(1, true);
    }

}