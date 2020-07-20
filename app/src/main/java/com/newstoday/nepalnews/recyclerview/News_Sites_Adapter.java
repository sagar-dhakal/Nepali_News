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

package com.newstoday.nepalnews.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.newstoday.nepalnews.R;
import com.newstoday.nepalnews.items.NepalNewsItem;
import com.newstoday.nepalnews.services.ChromeOpener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class News_Sites_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context c;
    private final String data;
    private final List<NepalNewsItem.NepalNews.TopNewsSites> topNewsSites;
    private final List<NepalNewsItem.NepalNews.Categories.Entertainment.Entertainmentnewssites> entertainmentnewssites;
    private final List<NepalNewsItem.NepalNews.Categories.Finance.Financenewssites> financenewssites;
    private final List<NepalNewsItem.NepalNews.Categories.Health.Healthnewssites> healthnewssites;
    private final List<NepalNewsItem.NepalNews.Categories.Politics.Politicsnewssites> politicsnewssites;
    private final List<NepalNewsItem.NepalNews.Categories.Sports.Sportsnewssites> sportsnewssites;
    private final List<NepalNewsItem.NepalNews.Categories.Technology.Technologynewssites> technologynewssites;
    private final List<NepalNewsItem.NepalNews.Categories.World.Worldnewssites> worldnewssites;
    private final List<NepalNewsItem.NepalNews.Location.Province1.Province1newssites> province1newssites;
    private final List<NepalNewsItem.NepalNews.Location.Province2.Province2newssites> province2newssites;
    private final List<NepalNewsItem.NepalNews.Location.Province3.Province3newssites> province3newssites;
    private final List<NepalNewsItem.NepalNews.Location.Province4.Province4newssites> province4newssites;
    private final List<NepalNewsItem.NepalNews.Location.Province5.Province5newssites> province5newssites;
    private final List<NepalNewsItem.NepalNews.Location.Province6.Province6newssites> province6newssites;
    private final List<NepalNewsItem.NepalNews.Location.Province7.Province7newssites> province7newssites;


    public News_Sites_Adapter(Context c, String data, List<NepalNewsItem.NepalNews.TopNewsSites> topNewsSites,
                              List<NepalNewsItem.NepalNews.Categories.Entertainment.Entertainmentnewssites> entertainmentnewssites,
                              List<NepalNewsItem.NepalNews.Categories.Finance.Financenewssites> financenewssites,
                              List<NepalNewsItem.NepalNews.Categories.Health.Healthnewssites> healthnewssites,
                              List<NepalNewsItem.NepalNews.Categories.Politics.Politicsnewssites> politicsnewssites,
                              List<NepalNewsItem.NepalNews.Categories.Sports.Sportsnewssites> sportsnewssites,
                              List<NepalNewsItem.NepalNews.Categories.Technology.Technologynewssites> technologynewssites,
                              List<NepalNewsItem.NepalNews.Categories.World.Worldnewssites> worldnewssites,
                              List<NepalNewsItem.NepalNews.Location.Province1.Province1newssites> province1newssites,
                              List<NepalNewsItem.NepalNews.Location.Province2.Province2newssites> province2newssites,
                              List<NepalNewsItem.NepalNews.Location.Province3.Province3newssites> province3newssites,
                              List<NepalNewsItem.NepalNews.Location.Province4.Province4newssites> province4newssites,
                              List<NepalNewsItem.NepalNews.Location.Province5.Province5newssites> province5newssites,
                              List<NepalNewsItem.NepalNews.Location.Province6.Province6newssites> province6newssites,
                              List<NepalNewsItem.NepalNews.Location.Province7.Province7newssites> province7newssites) {
        this.c = c;
        this.data = data;
        this.topNewsSites = topNewsSites;
        this.entertainmentnewssites = entertainmentnewssites;
        this.financenewssites = financenewssites;
        this.healthnewssites = healthnewssites;
        this.politicsnewssites = politicsnewssites;
        this.sportsnewssites = sportsnewssites;
        this.technologynewssites = technologynewssites;
        this.worldnewssites = worldnewssites;
        this.province1newssites = province1newssites;
        this.province2newssites = province2newssites;
        this.province3newssites = province3newssites;
        this.province4newssites = province4newssites;
        this.province5newssites = province5newssites;
        this.province6newssites = province6newssites;
        this.province7newssites = province7newssites;
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
            case "topNewsSites": {
                newsSitesViewHolder.siteName.setText(topNewsSites.get(position).siteName);
                String siteImage = topNewsSites.get(position).siteImage;
                if (!siteImage.equals("")) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String link = topNewsSites.get(position).siteLink;
                    startWebView(link);
                });
                break;
            }
            case "entertainmentnewssites": {
                newsSitesViewHolder.siteName.setText(entertainmentnewssites.get(position).siteName);
                String siteImage = entertainmentnewssites.get(position).siteImage;
                if (!siteImage.equals("")) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String link = entertainmentnewssites.get(position).siteLink;
                    startWebView(link);
                });
                break;
            }
            case "financenewssites": {
                newsSitesViewHolder.siteName.setText(financenewssites.get(position).siteName);
                String siteImage = financenewssites.get(position).siteImage;
                if (!siteImage.equals("")) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String link = financenewssites.get(position).siteLink;
                    startWebView(link);
                });
                break;
            }
            case "healthnewssites": {
                newsSitesViewHolder.siteName.setText(healthnewssites.get(position).siteName);
                String siteImage = healthnewssites.get(position).siteImage;
                if (!siteImage.equals("")) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String link = healthnewssites.get(position).siteLink;
                    startWebView(link);
                });
                break;
            }
            case "politicsnewssites": {
                newsSitesViewHolder.siteName.setText(politicsnewssites.get(position).siteName);
                String siteImage = politicsnewssites.get(position).siteImage;
                if (!siteImage.equals("")) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String link = politicsnewssites.get(position).siteLink;
                    startWebView(link);
                });
                break;
            }
            case "sportsnewssites": {
                newsSitesViewHolder.siteName.setText(sportsnewssites.get(position).siteName);
                String siteImage = sportsnewssites.get(position).siteImage;
                if (!siteImage.equals("")) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String link = sportsnewssites.get(position).siteLink;
                    startWebView(link);
                });
                break;
            }
            case "technologynewssites": {
                newsSitesViewHolder.siteName.setText(technologynewssites.get(position).siteName);
                String siteImage = technologynewssites.get(position).siteImage;
                if (!siteImage.equals("")) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String link = technologynewssites.get(position).siteLink;
                    startWebView(link);
                });
                break;
            }
            case "worldnewssites": {
                newsSitesViewHolder.siteName.setText(worldnewssites.get(position).siteName);
                String siteImage = worldnewssites.get(position).siteImage;
                if (!siteImage.equals("")) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String link = worldnewssites.get(position).siteLink;
                    startWebView(link);
                });
                break;
            }
            case "province1newssites": {
                newsSitesViewHolder.siteName.setText(province1newssites.get(position).siteName);
                String siteImage = province1newssites.get(position).siteImage;
                if (!siteImage.equals("")) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String link = province1newssites.get(position).siteLink;
                    startWebView(link);
                });
                break;
            }
            case "province2newssites": {
                newsSitesViewHolder.siteName.setText(province2newssites.get(position).siteName);
                String siteImage = province2newssites.get(position).siteImage;
                if (!siteImage.equals("")) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String link = province2newssites.get(position).siteLink;
                    startWebView(link);
                });
                break;
            }
            case "province3newssites": {
                newsSitesViewHolder.siteName.setText(province3newssites.get(position).siteName);
                String siteImage = province3newssites.get(position).siteImage;
                if (!siteImage.equals("")) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String link = province3newssites.get(position).siteLink;
                    startWebView(link);
                });
                break;
            }
            case "province4newssites": {
                newsSitesViewHolder.siteName.setText(province4newssites.get(position).siteName);
                String siteImage = province4newssites.get(position).siteImage;
                if (!siteImage.equals("")) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String link = province4newssites.get(position).siteLink;
                    startWebView(link);
                });
                break;
            }
            case "province5newssites": {
                newsSitesViewHolder.siteName.setText(province5newssites.get(position).siteName);
                String siteImage = province5newssites.get(position).siteImage;
                if (!siteImage.equals("")) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String link = province5newssites.get(position).siteLink;
                    startWebView(link);
                });
                break;
            }
            case "province6newssites": {
                newsSitesViewHolder.siteName.setText(province6newssites.get(position).siteName);
                String siteImage = province6newssites.get(position).siteImage;
                if (!siteImage.equals("")) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String link = province6newssites.get(position).siteLink;
                    startWebView(link);
                });
                break;
            }
            case "province7newssites": {
                newsSitesViewHolder.siteName.setText(province7newssites.get(position).siteName);
                String siteImage = province7newssites.get(position).siteImage;
                if (!siteImage.equals("")) {
                    Picasso.get().load(siteImage).into(((News_Sites_ViewHolder) holder).siteImage);
                }
                newsSitesViewHolder.newsParent.setOnClickListener(v -> {
                    String link = province7newssites.get(position).siteLink;
                    startWebView(link);
                });
                break;
            }
        }
    }

    private void startWebView(String link) {
        ChromeOpener opener = new ChromeOpener();
        opener.openLink(c, link);
    }

    @Override
    public int getItemCount() {
        try {
            switch (data) {
                case "topNewsSites":
                    return topNewsSites.size();
                case "entertainmentnewssites":
                    return entertainmentnewssites.size();
                case "financenewssites":
                    return financenewssites.size();
                case "healthnewssites":
                    return healthnewssites.size();
                case "politicsnewssites":
                    return politicsnewssites.size();
                case "sportsnewssites":
                    return sportsnewssites.size();
                case "technologynewssites":
                    return technologynewssites.size();
                case "worldnewssites":
                    return worldnewssites.size();
                case "province1newssites":
                    return province1newssites.size();
                case "province2newssites":
                    return province2newssites.size();
                case "province3newssites":
                    return province3newssites.size();
                case "province4newssites":
                    return province4newssites.size();
                case "province5newssites":
                    return province5newssites.size();
                case "province6newssites":
                    return province6newssites.size();
                case "province7newssites":
                    return province7newssites.size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
