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
  <p>
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
  <p>
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

package com.newstoday.nepalnews.onlineTools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.newstoday.nepalnews.R;
import com.newstoday.nepalnews.items.NepalNewsItem;
import com.newstoday.nepalnews.services.ChromeOpener;
import com.newstoday.nepalnews.services.InternetIsConnected;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OnlineTools_Sites_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context c;
    private final String data;
    private final List<NepalNewsItem.NepalNews.OnlineTools.OnlineShopping> onlineShoppings;
    private final List<NepalNewsItem.NepalNews.OnlineTools.HotelBooking> hotelBookings;
    private final List<NepalNewsItem.NepalNews.OnlineTools.JobSites> jobSites;
    private final List<NepalNewsItem.NepalNews.OnlineTools.EducationSites> educationSites;
    private final List<NepalNewsItem.NepalNews.OnlineTools.OtherSites> otherSites;

    public OnlineTools_Sites_Adapter(Context c, String data, List<NepalNewsItem.NepalNews.OnlineTools.OnlineShopping> onlineShoppings,
                                     List<NepalNewsItem.NepalNews.OnlineTools.HotelBooking> hotelBookings,
                                     List<NepalNewsItem.NepalNews.OnlineTools.JobSites> jobSites,
                                     List<NepalNewsItem.NepalNews.OnlineTools.EducationSites> educationSites,
                                     List<NepalNewsItem.NepalNews.OnlineTools.OtherSites> otherSites) {
        this.c = c;
        this.data = data;
        this.onlineShoppings = onlineShoppings;
        this.hotelBookings = hotelBookings;
        this.jobSites = jobSites;
        this.educationSites = educationSites;
        this.otherSites = otherSites;
    }

    static class News_Sites_ViewHolder extends RecyclerView.ViewHolder {
        final ConstraintLayout newsParent;
        final ImageView siteImage;
        final TextView siteName;

        News_Sites_ViewHolder(View itemView) {
            super(itemView);
            newsParent = itemView.findViewById(R.id.newsParent);
            siteName = itemView.findViewById(R.id.siteName);
            siteImage = itemView.findViewById(R.id.siteImage);

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.category_scroll_item_layout, parent, false);
        return new News_Sites_ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        News_Sites_ViewHolder newsSitesViewHolder = (News_Sites_ViewHolder) holder;

        switch (data) {

            case "onlineshopping": {
                newsSitesViewHolder.siteName.setText(onlineShoppings.get(position).siteName);
                String siteImage = onlineShoppings.get(position).siteImage;
                if (siteImage != null) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String siteLink = onlineShoppings.get(position).siteLink;
                    startWebView(siteLink);
                });
                break;
            }
            case "hotelbooking": {
                newsSitesViewHolder.siteName.setText(hotelBookings.get(position).siteName);
                String siteImage = hotelBookings.get(position).siteImage;
                if (siteImage != null) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String siteLink = hotelBookings.get(position).siteLink;
                    startWebView(siteLink);
                });
                break;
            }
            case "jobsites": {
                newsSitesViewHolder.siteName.setText(jobSites.get(position).siteName);
                String siteImage = jobSites.get(position).siteImage;
                if (siteImage != null) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String siteLink = jobSites.get(position).siteLink;
                    startWebView(siteLink);
                });
                break;
            }
            case "educationsites": {
                newsSitesViewHolder.siteName.setText(educationSites.get(position).siteName);
                String siteImage = educationSites.get(position).siteImage;
                if (siteImage != null) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String siteLink = educationSites.get(position).siteLink;
                    startWebView(siteLink);
                });
                break;
            }
            case "othersites": {
                newsSitesViewHolder.siteName.setText(otherSites.get(position).siteName);
                String siteImage = otherSites.get(position).siteImage;
                if (siteImage != null) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String siteLink = otherSites.get(position).siteLink;
                    startWebView(siteLink);
                });
                break;
            }
        }

    }

    private void startWebView(String link) {
        InternetIsConnected isConnected = new InternetIsConnected();
        if (isConnected.internetIsConnected()) {
            ChromeOpener opener = new ChromeOpener();
            opener.openLink(c, link);
        } else {
            Toast.makeText(c, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        try {
            switch (data) {
                case "onlineshopping":
                    return onlineShoppings.size();
                case "hotelbooking":
                    return hotelBookings.size();
                case "jobsites":
                    return jobSites.size();
                case "educationsites":
                    return educationSites.size();
                case "othersites":
                    return otherSites.size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
