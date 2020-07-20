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

package com.newstoday.nepalnews.radio.radio_recent;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favoritelist")
public class Recent_Radio_Items {
    @PrimaryKey
    @NonNull
    public String stationName;
    @ColumnInfo(name = "detail")
    public String stationDetail;
    @ColumnInfo(name = "image")
    public String stationimage;
    @ColumnInfo(name = "link")
    public String stationLink;
    @ColumnInfo(name = "location")
    public String stationLocation;

    @NonNull
    public String getStationName() {
        return stationName;
    }

    public void setStationName(@NonNull String stationName) {
        this.stationName = stationName;
    }

    public String getStationDetail() {
        return stationDetail;
    }

    public void setStationDetail(String stationDetail) {
        this.stationDetail = stationDetail;
    }

    public String getStationimage() {
        return stationimage;
    }

    public void setStationimage(String stationimage) {
        this.stationimage = stationimage;
    }

    public String getStationLink() {
        return stationLink;
    }

    public void setStationLink(String stationLink) {
        this.stationLink = stationLink;
    }

    public String getStationLocation() {
        return stationLocation;
    }

    public void setStationLocation(String stationLocation) {
        this.stationLocation = stationLocation;
    }
}