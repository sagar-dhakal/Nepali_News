/*
  spaRSS
  <p/>
  Copyright (c) 2015-2016 Arnaud Renaud-Goud
  Copyright (c) 2012-2015 Frederic Julian
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

package com.newstoday.nepalnews.rssfeedreader.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.newstoday.nepalnews.R;
import com.newstoday.nepalnews.rssfeedreader.provider.FeedDataContentProvider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Objects;

public class AddGoogleNewsActivity extends BaseActivity {
    private static final String TAG = "AddGoogleNewsActivity";

    private static final int[] TOPIC_NAME = new int[]{R.string.google_news_top_stories, R.string.google_news_world, R.string.google_news_business,
            R.string.google_news_technology, R.string.google_news_entertainment, R.string.google_news_sports, R.string.google_news_science, R.string.google_news_health};

    private static final String[] TOPIC_CODES = new String[]{null, "w", "b", "t", "e", "s", "snc", "m"};

    private static final int[] CB_IDS = new int[]{R.id.cb_top_stories, R.id.cb_world, R.id.cb_business, R.id.cb_technology, R.id.cb_entertainment,
            R.id.cb_sports, R.id.cb_science, R.id.cb_health};
    private EditText mCustomTopicEditText;
    private Button ok;
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_activity_add_google_news);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setResult(RESULT_CANCELED);
        mCustomTopicEditText = findViewById(R.id.google_news_custom_topic);
        ok = findViewById(R.id.ok);
        cancel = findViewById(R.id.cancel);
        ok.setOnClickListener(v -> onClickOk());
        cancel.setOnClickListener(v -> onClickCancel());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return false;
    }

    private void onClickOk() {
        for (int topic = 0; topic < TOPIC_NAME.length; topic++) {
            if (((CheckBox) findViewById(CB_IDS[topic])).isChecked()) {
                String url = "http://news.google.com/news?hl=" + Locale.getDefault().getLanguage() + "&output=rss";
                if (TOPIC_CODES[topic] != null) {
                    url += "&topic=" + TOPIC_CODES[topic];
                }
                FeedDataContentProvider.addFeed(this, url, getString(TOPIC_NAME[topic]), true);
            }
        }

        String custom_topic = mCustomTopicEditText.getText().toString();
        if (!custom_topic.isEmpty()) {
            try {
                String url = "http://news.google.com/news?hl=" + Locale.getDefault().getLanguage() + "&output=rss&q=" + URLEncoder.encode(custom_topic, "UTF-8");
                FeedDataContentProvider.addFeed(this, url, custom_topic, true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        setResult(RESULT_OK);
        finish();
    }

    private void onClickCancel() {
        finish();
    }
}

