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

package com.newstoday.nepalnews;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.newstoday.nepalnews.news_package.recent_news.activity.HomeActivity;
import com.newstoday.nepalnews.recyclerview.News_Sites_Adapter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.newstoday.nepalnews.activities.MainActivity.entertainmentnewssites;
import static com.newstoday.nepalnews.activities.MainActivity.financenewssites;
import static com.newstoday.nepalnews.activities.MainActivity.healthnewssites;
import static com.newstoday.nepalnews.activities.MainActivity.politicsnewssites;
import static com.newstoday.nepalnews.activities.MainActivity.province1newssites;
import static com.newstoday.nepalnews.activities.MainActivity.province2newssites;
import static com.newstoday.nepalnews.activities.MainActivity.province3newssites;
import static com.newstoday.nepalnews.activities.MainActivity.province4newssites;
import static com.newstoday.nepalnews.activities.MainActivity.province5newssites;
import static com.newstoday.nepalnews.activities.MainActivity.province6newssites;
import static com.newstoday.nepalnews.activities.MainActivity.province7newssites;
import static com.newstoday.nepalnews.activities.MainActivity.sportsnewssites;
import static com.newstoday.nepalnews.activities.MainActivity.technologynewssites;
import static com.newstoday.nepalnews.activities.MainActivity.topNewsSites;
import static com.newstoday.nepalnews.activities.MainActivity.worldnewssites;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home_Fragment extends Fragment {

    private InterstitialAd mInterstitialAd;

    public Home_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        MobileAds.initialize(getActivity(), initializationStatus -> {
        });
        List<String> testDeviceIds = Collections.singletonList("BEE69BA4713E1A126269D8A901AA9297");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        mInterstitialAd = new InterstitialAd(Objects.requireNonNull(getActivity()));
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intrestial_ad));
        mInterstitialAd.loadAd(new AdRequest.Builder().addKeyword("Insurance").build());

        CardView explore_News = view.findViewById(R.id.explore_News);
        explore_News.setOnClickListener(v -> {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        startActivity(new Intent(getActivity(), HomeActivity.class));
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        super.onAdFailedToLoad(i);
                        startActivity(new Intent(getActivity(), HomeActivity.class));
                    }
                });
            } else {
                mInterstitialAd.loadAd(new AdRequest.Builder().addKeyword("Insurance").build());
                startActivity(new Intent(getActivity(), HomeActivity.class));
            }
        });
        RecyclerView categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(layoutManager1);
        News_Sites_Adapter adapter = new News_Sites_Adapter(getActivity(), "topNewsSites", topNewsSites, entertainmentnewssites, financenewssites, healthnewssites, politicsnewssites, sportsnewssites, technologynewssites,
                worldnewssites, province1newssites, province2newssites, province3newssites, province4newssites, province5newssites, province6newssites, province7newssites);
        categoryRecyclerView.setAdapter(adapter);

        CardView pOne = view.findViewById(R.id.p_one);
        CardView pTwo = view.findViewById(R.id.p_two);
        CardView pthree = view.findViewById(R.id.p_three);
        CardView pfour = view.findViewById(R.id.p_four);
        CardView pfive = view.findViewById(R.id.p_five);
        CardView psix = view.findViewById(R.id.p_six);
        CardView pseven = view.findViewById(R.id.p_seven);

        CardView politics = view.findViewById(R.id.politics);
        CardView entertainment = view.findViewById(R.id.entertainment);
        CardView finance = view.findViewById(R.id.finance);
        CardView health = view.findViewById(R.id.health);
        CardView sports = view.findViewById(R.id.sports);
        CardView technology = view.findViewById(R.id.technology);
        CardView world = view.findViewById(R.id.world);

        pOne.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.newstoday.nepalnews.news_package.news_location.province_one.activity.HomeActivity.class);
            loadAD(intent);
        });
        pTwo.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.newstoday.nepalnews.news_package.news_location.province_two.activity.HomeActivity.class);
            loadAD(intent);
        });
        pthree.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.newstoday.nepalnews.news_package.news_location.province_three.activity.HomeActivity.class);
            loadAD(intent);
        });
        pfour.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.newstoday.nepalnews.news_package.news_location.province_four.activity.HomeActivity.class);
            loadAD(intent);
        });
        pfive.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.newstoday.nepalnews.news_package.news_location.province_five.activity.HomeActivity.class);
            loadAD(intent);
        });
        psix.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.newstoday.nepalnews.news_package.news_location.province_six.activity.HomeActivity.class);
            loadAD(intent);
        });
        pseven.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.newstoday.nepalnews.news_package.news_location.province_seven.activity.HomeActivity.class);
            loadAD(intent);
        });

        politics.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.newstoday.nepalnews.news_package.news_category.politics.activity.HomeActivity.class);
            loadAD(intent);
        });
        entertainment.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.newstoday.nepalnews.news_package.news_category.entertainment.activity.HomeActivity.class);
            loadAD(intent);
        });
        finance.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.newstoday.nepalnews.news_package.news_category.finance.activity.HomeActivity.class);
            loadAD(intent);
        });
        health.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.newstoday.nepalnews.news_package.news_category.health.activity.HomeActivity.class);
            loadAD(intent);
        });
        sports.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.newstoday.nepalnews.news_package.news_category.sports.activity.HomeActivity.class);
            loadAD(intent);
        });
        technology.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.newstoday.nepalnews.news_package.news_category.technology.activity.HomeActivity.class);
            loadAD(intent);
        });
        world.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.newstoday.nepalnews.news_package.news_category.world.activity.HomeActivity.class);
            loadAD(intent);
        });

        return view;
    }

    private void prepareAd() {
        mInterstitialAd = new InterstitialAd(Objects.requireNonNull(getActivity()));
        mInterstitialAd.setAdUnitId(getString(R.string.intrestial_ad));
        mInterstitialAd.loadAd(new AdRequest.Builder().addKeyword("Insurance").build());
    }

    private void loadAD(Intent intent) {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    startActivity(intent);
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    startActivity(intent);
                }
            });
        } else {
            prepareAd();
            startActivity(intent);
        }
    }
}