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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newstoday.nepalnews.recyclerview.SocialMedia_Recycler_Adapter;

import static com.newstoday.nepalnews.activities.MainActivity.socialMedia;

public class Social_Media_Fragment extends Fragment {

    public Social_Media_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.social_media_fragment, container, false);
        RecyclerView socialRecyclerView = view.findViewById(R.id.socialRecyclerView);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getActivity(), 3);
        socialRecyclerView.setLayoutManager(manager);
        SocialMedia_Recycler_Adapter adapter = new SocialMedia_Recycler_Adapter(getActivity(), socialMedia);
        socialRecyclerView.setAdapter(adapter);
        return view;
    }
}
