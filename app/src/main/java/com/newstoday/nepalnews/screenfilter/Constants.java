/*
  NepalNews
  <p/>
  Copyright (c) 2019-2020 Sagar Dhakal
  Copyright (C) 2016 Paper Airplane Dev Team
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

package com.newstoday.nepalnews.screenfilter;

public final class Constants {

    public static final String NOTIFICATION_CHANNEL_ID_RS = "running_status";

    public static final String ACTION_UPDATE_STATUS = "com.newstoday.nepalnews.screenfilter.ACTION_UPDATE_STATUS";
    public static final String ACTION_ALARM_START = "com.newstoday.nepalnews.screenfilter.ALARM_ACTION_START";
    public static final String ACTION_ALARM_STOP = "com.newstoday.nepalnews.screenfilter.ALARM_ACTION_STOP";
    public static final String ACTION_TOGGLE = "com.newstoday.nepalnews.screenfilter.ACTION_TOGGLE";

    public static final class AdvancedMode {

        public static final int NONE = 0;
        public static final int NO_PERMISSION = 1;
        public static final int OVERLAY_ALL = 2;

    }

    public static final class Extra {

        public static final String EVENT_ID = "event_id";
        public static final String ACTION = "action_name";
        public static final String BRIGHTNESS = "brightness";
        public static final String ADVANCED_MODE = "advanced_mode";
        public static final String IS_SHOWING = "is_showing";
        public static final String YELLOW_FILTER_ALPHA = "yellow_filter_alpha";

    }

    public static final class Action {

        public static final int START = 1;
        public static final int UPDATE = 2;
        public static final int PAUSE = 3;
        public static final int STOP = 4;
        public static final int CHECK = 5;

    }

    public static final class Event {

        public static final int CANNOT_START = 1;
        public static final int DESTROY_SERVICE = 2;
        public static final int CHECK = 3;

    }

}
