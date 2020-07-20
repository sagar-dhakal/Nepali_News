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

package com.newstoday.nepalnews.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.newstoday.nepalnews.R;
import com.newstoday.nepalnews.services.ChromeOpener;

import java.util.Objects;

public class About_Developer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_developer);
        TextView navigationTitle = findViewById(R.id.navigationTitle);
        Toolbar toolbar = findViewById(R.id.base_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        navigationTitle.setText("Developer");
        ImageView dev_face = findViewById(R.id.dev_face);
        ImageView dev_mail = findViewById(R.id.dev_mail);
        ImageView dev_twit = findViewById(R.id.dev_play);
        ImageView dev_gith = findViewById(R.id.dev_gith);
        LinearLayout fork_gith = findViewById(R.id.fork_gith);
        LinearLayout licenses = findViewById(R.id.licenses);

        fork_gith.setOnClickListener(v -> sendLink(""));
        licenses.setOnClickListener(v -> {
            WebView web = (WebView) LayoutInflater.from(About_Developer.this).inflate(R.layout.dialog_licenses, null);
            web.loadUrl("file:///android_asset/licenses.html");
            new AlertDialog.Builder(About_Developer.this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                    .setTitle("Licenses")
                    .setView(web)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        });


        dev_face.setOnClickListener(v -> sendLink("https://www.facebook.com/cherrydigitalservices"));
        dev_mail.setOnClickListener(v -> {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"cherrydigital.care@gmail.com"});
            email.putExtra(Intent.EXTRA_SUBJECT, "Problems & Feedback from-- " + getPackageName());
            email.putExtra(Intent.EXTRA_TEXT, "Note : Dont't clear the subject please,\n\n");
            email.setType("message/rfc822");
            startActivity(Intent.createChooser(email, "Send Mail"));
        });
        dev_twit.setOnClickListener(v -> sendLink("https://play.google.com/store/apps/dev?id=7892052850981050382"));
        dev_gith.setOnClickListener(v -> sendLink("https://github.com/NP-Sagar-Dhakal"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendLink(String url) {
        ChromeOpener opener = new ChromeOpener();
        opener.openLink(About_Developer.this, url);
    }
}
