/*
  NepalNews
  <p/>
  Copyright (c) 2019-2020 Sagar Dhakal
  <p/>
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  <p/>
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  <p/>
  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.newstoday.nepalnews.radio.radio_favorites;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.newstoday.nepalnews.R;
import com.newstoday.nepalnews.news_package.recent_news.utils.PrefUtils;
import com.newstoday.nepalnews.radio.Next_Prev_Callback;
import com.newstoday.nepalnews.radio.Radio_Activity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Fav_Detail_Activity extends AppCompatActivity implements Next_Prev_Callback {

    private ViewPager pager;
    private InterstitialAd mInterstitialAd;
    int slideAD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_radio_detail);

        slideAD = PrefUtils.getInt(PrefUtils.SLIDE_AD + "_RADIO", 0);
        MobileAds.initialize(this, initializationStatus -> {
        });
        List<String> testDeviceIds = Collections.singletonList("BEE69BA4713E1A126269D8A901AA9297");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        mInterstitialAd = new InterstitialAd(Objects.requireNonNull(this));
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intrestial_ad));
        mInterstitialAd.loadAd(new AdRequest.Builder().addKeyword("Insurance").build());

        int position = getIntent().getIntExtra("position", 0);
        pager = findViewById(R.id.radioViewPager);
        Fav_Pager_Adapter pagerAdapter = new Fav_Pager_Adapter(getSupportFragmentManager());
        pagerAdapter.notifyDataSetChanged();
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                slideAD = slideAD + 1;
                if (slideAD >= 10) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                        slideAD = 0;
                        PrefUtils.putInt(PrefUtils.SLIDE_AD, 0);
                        mInterstitialAd = new InterstitialAd(Objects.requireNonNull(Fav_Detail_Activity.this));
                        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intrestial_ad));
                        mInterstitialAd.loadAd(new AdRequest.Builder().addKeyword("Insurance").build());
                    } else {
                        mInterstitialAd = new InterstitialAd(Objects.requireNonNull(Fav_Detail_Activity.this));
                        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intrestial_ad));
                        mInterstitialAd.loadAd(new AdRequest.Builder().addKeyword("Insurance").build());
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pager.setCurrentItem(position);
    }

    @Override
    public void nextcallback(int i) {
        pager.setCurrentItem(i);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(Fav_Detail_Activity.this, Radio_Activity.class);
        startActivity(intent);
    }

}
