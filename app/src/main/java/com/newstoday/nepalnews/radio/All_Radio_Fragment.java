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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.newstoday.nepalnews.R;
import com.newstoday.nepalnews.activities.MainActivity;
import com.newstoday.nepalnews.items.NepalNewsItem;
import com.newstoday.nepalnews.radio.radio_favorites.FavoriteDatabase;
import com.newstoday.nepalnews.radio.radio_recent.RecentDatabase;

import java.util.List;
import java.util.Objects;

public class All_Radio_Fragment extends Fragment {

    public static FavoriteDatabase favoriteDatabase;
    public static RecentDatabase recentDatabase;

    public static Radio_Recycler_Adapter adapter;
    public static List<NepalNewsItem.NepalNews.NepaliRadios> radioItems;

    private RecyclerView all_Radio_Recycler;

    public All_Radio_Fragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_radio, container, false);
        all_Radio_Recycler = view.findViewById(R.id.all_Radio_Recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        all_Radio_Recycler.setLayoutManager(layoutManager);

        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(this.getContext()));
        favoriteDatabase = Room.databaseBuilder(this.getContext(), FavoriteDatabase.class, "favouritedatabase").allowMainThreadQueries().build();
        recentDatabase = Room.databaseBuilder(this.getContext(), RecentDatabase.class, "recentdatabse").allowMainThreadQueries().build();
        radioItems = MainActivity.radioItems;
        adapter = new Radio_Recycler_Adapter(All_Radio_Fragment.this.getContext(), radioItems);
        All_Radio_Fragment.this.all_Radio_Recycler.setAdapter(adapter);
        return view;
    }
}
