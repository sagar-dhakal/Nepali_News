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

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.newstoday.nepalnews.R;
import com.newstoday.nepalnews.radio.All_Radio_Fragment;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;


public class Favorite_Radio_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static int TYPE_AD = 0;
    private static int TYPE_ITEM = 1;
    private final Context context;
    public static List<Favorites_Radio_Items> radioItems;

    public Favorite_Radio_Adapter(Context context, List<Favorites_Radio_Items> radioItems) {
        this.context = context;
        Favorite_Radio_Adapter.radioItems = radioItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_AD) { // for call layout
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.aa_radio_recycler_native, parent, false);
            return new RadioAdViewHolder(view);

        } else {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.radio_recycler_item_layout, parent, false);
            return new RadioViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderr, int position) {
        if (holderr instanceof RadioAdViewHolder) {

            final RadioAdViewHolder holder = (RadioAdViewHolder) holderr;

            AdLoader adLoader = new AdLoader.Builder(Objects.requireNonNull(context), context.getString(R.string.native_ad))
                    .forUnifiedNativeAd(unifiedNativeAd -> {

                        UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(context)
                                .inflate(R.layout.aa_radio_native, null);
                        populateUnifiedNativeAdView(unifiedNativeAd, adView);
                        holder.frameLayout.removeAllViews();
                        holder.frameLayout.addView(adView);
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(int errorCode) {

                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder()
                            .build())
                    .build();
            adLoader.loadAd(new AdRequest.Builder().build());
        } else {
            final RadioViewHolder holder = (RadioViewHolder) holderr;

            holder.radio_Name.setText(radioItems.get(position).stationName);
            holder.radio_Detail.setText(radioItems.get(position).stationDetail);


            ColorGenerator generator = ColorGenerator.DEFAULT;
            int color = generator.getColor(position);
            TextDrawable letterDrawable = TextDrawable.builder().buildRoundRect(radioItems.get(position).stationName.substring(0, 1).toUpperCase(), color, 8);
            try {
                Picasso.get().load(radioItems.get(position).stationimage).placeholder(letterDrawable).error(letterDrawable).into(holder.radio_Image);
            } catch (Exception e) {
                e.printStackTrace();
            }


            holder.radioConstraint.setOnClickListener(v -> {
                Intent intent = new Intent(context, Fav_Detail_Activity.class);
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

            if (All_Radio_Fragment.favoriteDatabase.favoriteDao().isFavorite(radioItems.get(position).stationName) == 1)
                holder.addto_fav.setImageResource(R.drawable.ic_heart_filled);
            else
                holder.addto_fav.setImageResource(R.drawable.ic_heart_empty);

            holder.addto_fav.setOnClickListener(v -> {
                Favorites_Radio_Items favoriteList = new Favorites_Radio_Items();
                favoriteList.setStationName(radioItems.get(position).stationName);
                favoriteList.setStationDetail(radioItems.get(position).stationDetail);
                favoriteList.setStationimage(radioItems.get(position).stationimage);
                favoriteList.setStationLink(radioItems.get(position).stationLink);
                favoriteList.setStationLocation(radioItems.get(position).stationLocation);

                if (All_Radio_Fragment.favoriteDatabase.favoriteDao().isFavorite(radioItems.get(position).stationName) != 1) {
                    holder.addto_fav.setImageResource(R.drawable.ic_heart_filled);
                    All_Radio_Fragment.favoriteDatabase.favoriteDao().addData(favoriteList);
                    Toast.makeText(context, "Added to Favorite List", Toast.LENGTH_SHORT).show();

                } else {
                    holder.addto_fav.setImageResource(R.drawable.ic_heart_empty);
                    All_Radio_Fragment.favoriteDatabase.favoriteDao().delete(favoriteList);
                    Toast.makeText(context, "Removed from Favorite List", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        String pos = String.valueOf(position);
        if (pos.endsWith("4")) {
            return TYPE_AD;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return radioItems.size();
    }


    static class RadioViewHolder extends RecyclerView.ViewHolder {

        final TextView radio_Name;
        final TextView radio_Detail;
        final ImageView radio_Image;
        final ImageView addto_fav;
        final ConstraintLayout radioConstraint;

        RadioViewHolder(View itemView) {
            super(itemView);
            addto_fav = itemView.findViewById(R.id.favourite);
            radio_Name = itemView.findViewById(R.id.radio_name);
            radio_Detail = itemView.findViewById(R.id.radio_detail);
            radio_Image = itemView.findViewById(R.id.radio_image);
            radioConstraint = itemView.findViewById(R.id.radio_constraint);

        }
    }

    static class RadioAdViewHolder extends RecyclerView.ViewHolder {
        final FrameLayout frameLayout;
        final TextView radio_Name;
        final TextView radio_Detail;
        final ImageView radio_Image;
        final ImageView addto_fav;
        final ConstraintLayout radioConstraint;

        RadioAdViewHolder(View itemView) {
            super(itemView);
            frameLayout = itemView.findViewById(R.id.adFrame);
            addto_fav = itemView.findViewById(R.id.favourite);
            radio_Name = itemView.findViewById(R.id.radio_name);
            radio_Detail = itemView.findViewById(R.id.radio_detail);
            radio_Image = itemView.findViewById(R.id.radio_image);
            radioConstraint = itemView.findViewById(R.id.radio_constraint);
        }
    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        adView.setMediaView(adView.findViewById(R.id.native_ad_media_view));
        adView.setHeadlineView(adView.findViewById(R.id.native_ad_headline));
        adView.setCallToActionView(adView.findViewById(R.id.native_ad_call_to_action_button));
        adView.setBodyView(adView.findViewById(R.id.native_ad_body));


        if (nativeAd.getMediaContent() == null) {
            adView.getMediaView().setVisibility(View.VISIBLE);
        } else {
            adView.getMediaView().setVisibility(View.VISIBLE);
            adView.getMediaView().setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            (adView.getMediaView()).setMediaContent(nativeAd.getMediaContent());
        }

        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }


        if (nativeAd.getHeadline() == null) {
            adView.getHeadlineView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
            adView.getHeadlineView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);

    }


}
