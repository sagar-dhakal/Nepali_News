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
package com.newstoday.nepalnews.screenfilter.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.github.florent37.diagonallayout.DiagonalLayout;
import com.newstoday.nepalnews.R;
import com.newstoday.nepalnews.screenfilter.util.AlarmUtil;
import com.newstoday.nepalnews.screenfilter.util.Settings;
import com.newstoday.nepalnews.screenfilter.util.Utility;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Locale;
import java.util.Objects;

public class SchedulerDialog extends AlertDialog implements TimePickerDialog.OnTimeSetListener {

    private TimePickerDialog sunrisePicker, sunsetPicker;
    private TextView sunriseTime, sunsetTime;

    private FrameLayout mFrameLayout;
    private DiagonalLayout mLeftLayout, mRightLayout;

    private int hrsSunrise = 6, minSunrise = 0, hrsSunset = 22, minSunset = 0;

    private Settings mSettings;

    public SchedulerDialog(Context context) {
        super(context);
        if (context instanceof Activity) setOwnerActivity((Activity) context);
        init();
    }

    public SchedulerDialog(Context context, OnDismissListener onDismissListener) {
        super(context);
        if (context instanceof Activity) setOwnerActivity((Activity) context);
        if (onDismissListener != null) setOnDismissListener(onDismissListener);
        init();
    }

    public SchedulerDialog(Context context, int themeResId) {
        super(context, themeResId);
        if (context instanceof Activity) setOwnerActivity((Activity) context);
        init();
    }

    protected SchedulerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        if (context instanceof Activity) setOwnerActivity((Activity) context);
        init();
    }

    private void init() {
        mSettings = Settings.getInstance(getContext());
        hrsSunrise = mSettings.getInt(Settings.KEY_HOURS_SUNRISE, 6);
        minSunrise = mSettings.getInt(Settings.KEY_MINUTES_SUNRISE, 0);
        hrsSunset = mSettings.getInt(Settings.KEY_HOURS_SUNSET, 22);
        minSunset = mSettings.getInt(Settings.KEY_MINUTES_SUNSET, 0);

        View rootView = getLayoutInflater().inflate(R.layout.filter_dialog_scheduler, null);
        setView(rootView);

        mLeftLayout = rootView.findViewById(R.id.left_layout);
        mRightLayout = rootView.findViewById(R.id.right_layout);
        mFrameLayout = rootView.findViewById(R.id.frame_layout);
        mFrameLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mFrameLayout.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                        final int measureWidth = mFrameLayout.getMeasuredWidth();
                        final int dp20 = (int) Utility.dpToPx(getContext(), 20);
                        Log.i("TAG", "measureWidth: " + measureWidth);
                        mLeftLayout.getLayoutParams().width = measureWidth / 2 + dp20;
                        mRightLayout.getLayoutParams().width = measureWidth / 2 + dp20;
                    }
                });

        sunriseTime = rootView.findViewById(R.id.sunrise_time);
        sunsetTime = rootView.findViewById(R.id.sunset_time);
        sunriseTime.setText(String.format(Locale.getDefault(),
                "%1$02d:%2$02d", hrsSunrise, minSunrise));
        sunsetTime.setText(String.format(Locale.getDefault(),
                "%1$02d:%2$02d", hrsSunset, minSunset));

        Switch switchView = rootView.findViewById(R.id.auto_switch);
        switchView.setChecked(mSettings.getBoolean(Settings.KEY_AUTO_MODE, false));
        switchView.setOnCheckedChangeListener((compoundButton, b) -> {
            mSettings.putBoolean(Settings.KEY_AUTO_MODE, b);
            AlarmUtil.updateAlarmSettings(getContext());
        });

        rootView.findViewById(R.id.btn_ok).setOnClickListener(view -> dismiss());
        rootView.findViewById(R.id.sunrise_button).setOnClickListener(view -> {
            sunrisePicker = TimePickerDialog.newInstance(
                    SchedulerDialog.this,
                    hrsSunrise,
                    minSunrise,
                    true
            );
            sunrisePicker.show(Objects.requireNonNull(getOwnerActivity()).getFragmentManager(), "sunrise_dialog");
        });
        rootView.findViewById(R.id.sunset_button).setOnClickListener(view -> {
            sunsetPicker = TimePickerDialog.newInstance(
                    SchedulerDialog.this,
                    hrsSunset,
                    minSunset,
                    true
            );
            sunsetPicker.show(Objects.requireNonNull(getOwnerActivity()).getFragmentManager(), "sunset_dialog");
        });
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        if (view == sunrisePicker) {
            hrsSunrise = hourOfDay;
            minSunrise = minute;
            sunriseTime.setText(String.format(Locale.getDefault(),
                    "%1$02d:%2$02d", hrsSunrise, minSunrise));
            mSettings.putInt(Settings.KEY_HOURS_SUNRISE, hrsSunrise);
            mSettings.putInt(Settings.KEY_MINUTES_SUNRISE, minSunrise);
        } else if (view == sunsetPicker) {
            hrsSunset = hourOfDay;
            minSunset = minute;
            sunsetTime.setText(String.format(Locale.getDefault(),
                    "%1$02d:%2$02d", hrsSunset, minSunset));
            mSettings.putInt(Settings.KEY_HOURS_SUNSET, hrsSunset);
            mSettings.putInt(Settings.KEY_MINUTES_SUNSET, minSunset);
        }
        AlarmUtil.updateAlarmSettings(getContext());
    }

}