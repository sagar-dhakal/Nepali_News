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


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newstoday.nepalnews.onlineTools.OnlineTools_Sites_Adapter;

import static com.newstoday.nepalnews.activities.MainActivity.educationSites;
import static com.newstoday.nepalnews.activities.MainActivity.hotelBookings;
import static com.newstoday.nepalnews.activities.MainActivity.jobSites;
import static com.newstoday.nepalnews.activities.MainActivity.onlineShoppings;
import static com.newstoday.nepalnews.activities.MainActivity.otherSites;


/**
 * A simple {@link Fragment} subclass.
 */
public class Online_Tools_Fragment extends Fragment {


    public Online_Tools_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_tools_fragment, container, false);
        RecyclerView shoppingRecyclerview = view.findViewById(R.id.shoppingRecyclerview);
        RecyclerView hotelBookingRecyclerview = view.findViewById(R.id.hotelRecyclerview);
        RecyclerView jobSitesRecyclerview = view.findViewById(R.id.jobRecyclerview);
        RecyclerView educationSitesRecyclerview = view.findViewById(R.id.educationRecyclerview);
        RecyclerView otherSitesRecyclerview = view.findViewById(R.id.otherRecyclerview);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        shoppingRecyclerview.setLayoutManager(layoutManager);
        OnlineTools_Sites_Adapter adapter = new OnlineTools_Sites_Adapter(getActivity(), "onlineshopping", onlineShoppings, hotelBookings, jobSites, educationSites, otherSites);
        shoppingRecyclerview.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        hotelBookingRecyclerview.setLayoutManager(layoutManager2);
        OnlineTools_Sites_Adapter adapter1 = new OnlineTools_Sites_Adapter(getActivity(), "hotelbooking", onlineShoppings, hotelBookings, jobSites, educationSites, otherSites);
        hotelBookingRecyclerview.setAdapter(adapter1);

        RecyclerView.LayoutManager layoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        jobSitesRecyclerview.setLayoutManager(layoutManager3);
        OnlineTools_Sites_Adapter adapter2 = new OnlineTools_Sites_Adapter(getActivity(), "jobsites", onlineShoppings, hotelBookings, jobSites, educationSites, otherSites);
        jobSitesRecyclerview.setAdapter(adapter2);

        RecyclerView.LayoutManager layoutManager4 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        educationSitesRecyclerview.setLayoutManager(layoutManager4);
        OnlineTools_Sites_Adapter adapter3 = new OnlineTools_Sites_Adapter(getActivity(), "educationsites", onlineShoppings, hotelBookings, jobSites, educationSites, otherSites);
        educationSitesRecyclerview.setAdapter(adapter3);

        RecyclerView.LayoutManager layoutManager5 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        otherSitesRecyclerview.setLayoutManager(layoutManager5);
        OnlineTools_Sites_Adapter adapter5 = new OnlineTools_Sites_Adapter(getActivity(), "othersites", onlineShoppings, hotelBookings, jobSites, educationSites, otherSites);
        otherSitesRecyclerview.setAdapter(adapter5);
        return view;
    }
// Patterns.WEB_URL.matcher(potentialUrl).matches()
}
