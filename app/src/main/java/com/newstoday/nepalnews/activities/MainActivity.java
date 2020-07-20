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

package com.newstoday.nepalnews.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.newstoday.nepalnews.Home_Fragment;
import com.newstoday.nepalnews.Online_Tools_Fragment;
import com.newstoday.nepalnews.Other_Tools_Fragment;
import com.newstoday.nepalnews.R;
import com.newstoday.nepalnews.Social_Media_Fragment;
import com.newstoday.nepalnews.items.NepalNewsItem;
import com.newstoday.nepalnews.services.CacheCleaner;
import com.newstoday.nepalnews.services.InternetIsConnected;
import com.newstoday.nepalnews.services.Navigation;
import com.squareup.picasso.Cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends Navigation {
    public static List<NepalNewsItem.NepalNews.RecentRssLinks> recentRssLinks;
    public static List<NepalNewsItem.NepalNews.TopNewsSites> topNewsSites;
    public static List<NepalNewsItem.NepalNews.NepaliRadios> radioItems;

    public static List<NepalNewsItem.NepalNews.Categories.Entertainment.Entertainmentnewssites> entertainmentnewssites;
    public static List<NepalNewsItem.NepalNews.Categories.Entertainment.EntertainmentrssLinks> entertainmentrssLinks;
    public static List<NepalNewsItem.NepalNews.Categories.Finance.Financenewssites> financenewssites;
    public static List<NepalNewsItem.NepalNews.Categories.Finance.FinancerssLinks> financerssLinks;
    public static List<NepalNewsItem.NepalNews.Categories.Health.Healthnewssites> healthnewssites;
    public static List<NepalNewsItem.NepalNews.Categories.Health.HealthrssLinks> healthrssLinks;
    public static List<NepalNewsItem.NepalNews.Categories.Politics.Politicsnewssites> politicsnewssites;
    public static List<NepalNewsItem.NepalNews.Categories.Politics.PoliticsrssLinks> politicsrssLinks;
    public static List<NepalNewsItem.NepalNews.Categories.Sports.Sportsnewssites> sportsnewssites;
    public static List<NepalNewsItem.NepalNews.Categories.Sports.SportsrssLinks> sportsrssLinks;
    public static List<NepalNewsItem.NepalNews.Categories.Technology.Technologynewssites> technologynewssites;
    public static List<NepalNewsItem.NepalNews.Categories.Technology.TechnologyrssLinks> technologyrssLinks;
    public static List<NepalNewsItem.NepalNews.Categories.World.Worldnewssites> worldnewssites;
    public static List<NepalNewsItem.NepalNews.Categories.World.WorldrssLinks> worldrssLinks;

    public static List<NepalNewsItem.NepalNews.Location.Province1.Province1newssites> province1newssites;
    public static List<NepalNewsItem.NepalNews.Location.Province1.Province1rssLinks> province1rssLinks;
    public static List<NepalNewsItem.NepalNews.Location.Province2.Province2newssites> province2newssites;
    public static List<NepalNewsItem.NepalNews.Location.Province2.Province2rssLinks> province2rssLinks;
    public static List<NepalNewsItem.NepalNews.Location.Province3.Province3newssites> province3newssites;
    public static List<NepalNewsItem.NepalNews.Location.Province3.Province3rssLinks> province3rssLinks;
    public static List<NepalNewsItem.NepalNews.Location.Province4.Province4newssites> province4newssites;
    public static List<NepalNewsItem.NepalNews.Location.Province4.Province4rssLinks> province4rssLinks;
    public static List<NepalNewsItem.NepalNews.Location.Province5.Province5newssites> province5newssites;
    public static List<NepalNewsItem.NepalNews.Location.Province5.Province5rssLinks> province5rssLinks;
    public static List<NepalNewsItem.NepalNews.Location.Province6.Province6newssites> province6newssites;
    public static List<NepalNewsItem.NepalNews.Location.Province6.Province6rssLinks> province6rssLinks;
    public static List<NepalNewsItem.NepalNews.Location.Province7.Province7newssites> province7newssites;
    public static List<NepalNewsItem.NepalNews.Location.Province7.Province7rssLinks> province7rssLinks;

    public static List<NepalNewsItem.NepalNews.OnlineTools.OnlineShopping> onlineShoppings;
    public static List<NepalNewsItem.NepalNews.OnlineTools.HotelBooking> hotelBookings;
    public static List<NepalNewsItem.NepalNews.OnlineTools.JobSites> jobSites;
    public static List<NepalNewsItem.NepalNews.OnlineTools.EducationSites> educationSites;
    public static List<NepalNewsItem.NepalNews.OnlineTools.OtherSites> otherSites;

    public static List<NepalNewsItem.NepalNews.SocialMedia> socialMedia;
    private CoordinatorLayout homeFullLayout;
    InternetIsConnected isConnected;

    SharedPreferences m;
    BottomNavigationView bottomnavigation;

    public static final int MULTIPLE_PERMISSIONS = 10; // cod
    @SuppressLint("InlinedApi")
    String[] permissions = {
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.VIBRATE,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.CLEAR_APP_CACHE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }

        CacheCleaner cacheCleaner = new CacheCleaner();
        cacheCleaner.clearCacheFolder(this.getCacheDir(), 10);
        homeFullLayout = findViewById(R.id.homeFullLayout);
        homeFullLayout.setVisibility(View.GONE);
        m = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        isConnected = new InternetIsConnected();
        bottomnavigation = findViewById(R.id.bottom_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);


        Intent intent = getIntent();
        boolean realtimeURL = intent.getBooleanExtra("realtimeURL", false);
        if (realtimeURL = true) {
            try {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference().child("News Today").child("Nepal News");
            scoresRef.keepSynced(false);
            DatabaseReference jokeslinks = FirebaseDatabase.getInstance().getReference().child("News Today").child("Nepal News");
            jokeslinks.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        String databaseUrl = Objects.requireNonNull(dataSnapshot.child("database").getValue()).toString();
                        getRealtimeUrl(databaseUrl);
                        FirebaseDatabase.getInstance().goOffline();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        Cache.NONE.clear();
        com.android.volley.Cache cache = new com.android.volley.Cache() {
            @Override
            public Entry get(String key) {
                return null;
            }

            @Override
            public void put(String key, Entry entry) {

            }

            @Override
            public void initialize() {

            }

            @Override
            public void invalidate(String key, boolean fullExpire) {

            }

            @Override
            public void remove(String key) {

            }

            @Override
            public void clear() {

            }
        };
        cache.clear();


        bottomnavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.home:
                    fragment = new Home_Fragment();
                    break;
                case R.id.onlineTools:
                    fragment = new Online_Tools_Fragment();
                    break;
                case R.id.otherTools:
                    fragment = new Other_Tools_Fragment();
                    break;
                case R.id.socialMedia:
                    fragment = new Social_Media_Fragment();
                    break;
                case R.id.more:
                    fragment = new com.newstoday.nepalnews.todo_diary.MainActivity();
                    break;
            }
            return loadFragment(fragment);
        });
    }

    private void getRealtimeUrl(String databaseUrl) {
        View view = bottomnavigation.findViewById(R.id.home);
        view.performClick();
        String responses = m.getString("NepalNewsSites", "");
        if (Objects.requireNonNull(responses).equals("")) {
            if (isConnected.internetIsConnected()) {
                AsyncTask.execute(() -> {
                    StringRequest stringRequest = new StringRequest(databaseUrl, response -> {
                        sharedResponse(response);
                        nepalNewsItem(response);
                    }, error -> {
                        //Todo
                        Toast.makeText(MainActivity.this, "Server is busy. Please close the app and try again later.", Toast.LENGTH_SHORT).show();
                    });
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    queue.add(stringRequest);
                });

            } else {
                Toast.makeText(MainActivity.this, "Please turn on internet connection and re-open", Toast.LENGTH_SHORT).show();
            }
        } else {
            nepalNewsItem(responses);
            AsyncTask.execute(() -> {
                StringRequest stringRequest = new StringRequest(databaseUrl, this::sharedResponse, error -> {
                });
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(stringRequest);
            });
        }
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.fragment_Container, fragment)
                    .commit();

            return true;
        }
        return false;
    }

    private void nepalNewsItem(String response) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        NepalNewsItem newsItems = gson.fromJson(response, NepalNewsItem.class);
        try {
            topNewsSites = newsItems.NepalNews.get(4).TopNewsSites;
            recentRssLinks = newsItems.NepalNews.get(5).RecentRssLinks;
            radioItems = newsItems.NepalNews.get(6).NepaliRadios;
            entertainmentnewssites = newsItems.NepalNews.get(0).Categories.get(0).Entertainment.Entertainmentnewssites;
            entertainmentrssLinks = newsItems.NepalNews.get(0).Categories.get(0).Entertainment.EntertainmentrssLinks;
            financenewssites = newsItems.NepalNews.get(0).Categories.get(1).Finance.Financenewssites;
            financerssLinks = newsItems.NepalNews.get(0).Categories.get(1).Finance.FinancerssLinks;
            healthnewssites = newsItems.NepalNews.get(0).Categories.get(2).Health.Healthnewssites;
            healthrssLinks = newsItems.NepalNews.get(0).Categories.get(2).Health.HealthrssLinks;
            politicsnewssites = newsItems.NepalNews.get(0).Categories.get(3).Politics.Politicsnewssites;
            politicsrssLinks = newsItems.NepalNews.get(0).Categories.get(3).Politics.PoliticsrssLinks;
            sportsnewssites = newsItems.NepalNews.get(0).Categories.get(4).Sports.Sportsnewssites;
            sportsrssLinks = newsItems.NepalNews.get(0).Categories.get(4).Sports.SportsrssLinks;
            technologynewssites = newsItems.NepalNews.get(0).Categories.get(5).Technology.Technologynewssites;
            technologyrssLinks = newsItems.NepalNews.get(0).Categories.get(5).Technology.TechnologyrssLinks;
            worldnewssites = newsItems.NepalNews.get(0).Categories.get(6).World.Worldnewssites;
            worldrssLinks = newsItems.NepalNews.get(0).Categories.get(6).World.WorldrssLinks;

            province1newssites = newsItems.NepalNews.get(1).Location.get(0).Province1.Province1newssites;
            province1rssLinks = newsItems.NepalNews.get(1).Location.get(0).Province1.Province1rssLinks;
            province2newssites = newsItems.NepalNews.get(1).Location.get(1).Province2.Province2newssites;
            province2rssLinks = newsItems.NepalNews.get(1).Location.get(1).Province2.Province2rssLinks;
            province3newssites = newsItems.NepalNews.get(1).Location.get(2).Province3.Province3newssites;
            province3rssLinks = newsItems.NepalNews.get(1).Location.get(2).Province3.Province3rssLinks;
            province4newssites = newsItems.NepalNews.get(1).Location.get(3).Province4.Province4newssites;
            province4rssLinks = newsItems.NepalNews.get(1).Location.get(3).Province4.Province4rssLinks;
            province5newssites = newsItems.NepalNews.get(1).Location.get(4).Province5.Province5newssites;
            province5rssLinks = newsItems.NepalNews.get(1).Location.get(4).Province5.Province5rssLinks;
            province6newssites = newsItems.NepalNews.get(1).Location.get(5).Province6.Province6newssites;
            province6rssLinks = newsItems.NepalNews.get(1).Location.get(5).Province6.Province6rssLinks;
            province7newssites = newsItems.NepalNews.get(1).Location.get(6).Province7.Province7newssites;
            province7rssLinks = newsItems.NepalNews.get(1).Location.get(6).Province7.Province7rssLinks;

            onlineShoppings = newsItems.NepalNews.get(2).OnlineTools.get(0).OnlineShopping;
            hotelBookings = newsItems.NepalNews.get(2).OnlineTools.get(1).HotelBooking;
            jobSites = newsItems.NepalNews.get(2).OnlineTools.get(2).JobSites;
            educationSites = newsItems.NepalNews.get(2).OnlineTools.get(3).EducationSites;
            otherSites = newsItems.NepalNews.get(2).OnlineTools.get(4).OtherSites;

            socialMedia = newsItems.NepalNews.get(3).SocialMedia;
            homeFullLayout.setVisibility(View.VISIBLE);
            loadFragment(new Home_Fragment());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void sharedResponse(String response) {
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = m.edit();
        editor.putString("NepalNewsSites", (response));
        editor.apply();
    }

    private void checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(MainActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), MULTIPLE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permissions granted.
            } else {
                // no permissions granted.
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}
