package com.chernov.android.android_time;

import android.support.v4.app.Fragment;

public class TimeActivity extends TimeSingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new TimeFragment();
    }
}
