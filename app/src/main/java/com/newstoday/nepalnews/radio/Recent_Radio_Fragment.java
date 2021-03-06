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

package com.newstoday.nepalnews.radio;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newstoday.nepalnews.R;
import com.newstoday.nepalnews.radio.radio_recent.Recent_All_Radio_Adapter;
import com.newstoday.nepalnews.radio.radio_recent.Recent_Radio_Items;

import java.util.Collections;
import java.util.List;

public class Recent_Radio_Fragment extends Fragment {


    public Recent_Radio_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_radio, container, false);
        RecyclerView favourite_Radio_Recycler = view.findViewById(R.id.all_Radio_Recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        favourite_Radio_Recycler.setLayoutManager(layoutManager);
        List<Recent_Radio_Items> recentRadioItems = All_Radio_Fragment.recentDatabase.favoriteDao().getFavoriteData();
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String recentradio = m.getString("currentradio", "");
        try {
            String newCurrentRadio = recentRadioItems.get(0).stationName;
            if (!recentradio.equals(newCurrentRadio)) {
                Collections.reverse(recentRadioItems);
            }
            Recent_All_Radio_Adapter adapter = new Recent_All_Radio_Adapter(Recent_Radio_Fragment.this.getContext(), recentRadioItems);
            favourite_Radio_Recycler.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

}
