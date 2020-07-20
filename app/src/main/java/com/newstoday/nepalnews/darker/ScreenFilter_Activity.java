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

package com.newstoday.nepalnews.darker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.newstoday.nepalnews.R;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ScreenFilter_Activity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private Button btn;
    private SeekBar red_seekbar;
    private SeekBar green_seekbar;
    private SeekBar blue_seekbar;
    private SeekBar brightness_seekbar;
    private int brightness;
    private int red;
    private int green;
    private int blue;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenfilter);

        MobileAds.initialize(this, initializationStatus -> {
        });
        List<String> testDeviceIds = Collections.singletonList("BEE69BA4713E1A126269D8A901AA9297");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        AdRequest adRequest = new AdRequest.Builder().build();
        AdView adView = findViewById(R.id.adView);
        adView.loadAd(adRequest);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView navigationTitle = findViewById(R.id.navigationTitle);
        navigationTitle.setText("Screen Filter");
        BottomNavigationView bottomnavigation = findViewById(R.id.bottom_navigation);
        bottomnavigation.setVisibility(View.GONE);
        try {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        btn = findViewById(R.id.start_button);
        brightness_seekbar = findViewById(R.id.brightness_seekbar);
        red_seekbar = findViewById(R.id.red_seekbar);
        green_seekbar = findViewById(R.id.green_seekbar);
        blue_seekbar = findViewById(R.id.blue_seekbar);

        red_seekbar.setMax(255);
        blue_seekbar.setMax(255);
        green_seekbar.setMax(255);
        brightness_seekbar.setMax(255);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        brightness = preferences.getInt("alpha", 0);
        red = preferences.getInt("red", 0);
        green = preferences.getInt("green", 0);
        blue = preferences.getInt("blue", 0);

        brightness_seekbar.setProgress(brightness);
        red_seekbar.setProgress(red);
        blue_seekbar.setProgress(blue);
        green_seekbar.setProgress(green);

        FilterService.brightness = brightness;
        FilterService.red = red;
        FilterService.green = green;
        FilterService.blue = blue;

        brightness_seekbar.setOnSeekBarChangeListener(this);
        red_seekbar.setOnSeekBarChangeListener(this);
        green_seekbar.setOnSeekBarChangeListener(this);
        blue_seekbar.setOnSeekBarChangeListener(this);

        stopServiceIfActive();

        btn.setOnClickListener(v -> {
            if (btn.getText() == getResources().getString(R.string.start)) {
                Intent intent = new Intent(ScreenFilter_Activity.this, FilterService.class);
                startService(intent);
                btn.setText(getResources().getString(R.string.stop));
            } else {
                Intent i = new Intent(ScreenFilter_Activity.this, FilterService.class);
                stopService(i);
                btn.setText(getResources().getString(R.string.start));
            }

        });

    }

    private void stopServiceIfActive() {
        if (FilterService.STATE == FilterService.ACTIVE) {
            Intent i = new Intent(ScreenFilter_Activity.this, FilterService.class);
            stopService(i);
            btn.setText(getResources().getString(R.string.start));
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == brightness_seekbar)
            brightness = progress;
        if (seekBar == red_seekbar)
            red = progress;
        if (seekBar == green_seekbar)
            green = progress;
        if (seekBar == blue_seekbar)
            blue = progress;

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("red", red);
        editor.putInt("green", green);
        editor.putInt("blue", blue);
        editor.putInt("alpha", brightness);

        FilterService.brightness = brightness;
        FilterService.red = red;
        FilterService.green = green;
        FilterService.blue = blue;

        editor.apply();

        if (btn.getText() == getResources().getString(R.string.stop)) {
            Intent intent = new Intent(ScreenFilter_Activity.this, FilterService.class);
            startService(intent);
            btn.setText(getResources().getString(R.string.stop));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
