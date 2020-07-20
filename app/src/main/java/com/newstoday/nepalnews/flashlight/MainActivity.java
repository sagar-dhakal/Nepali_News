/*
  This file is part of Privacy Friendly Torchlight.
  Privacy Friendly Torchlight is free software:
  you can redistribute it and/or modify it under the terms of the
  GNU General Public License as published by the Free Software Foundation,
  either version 3 of the License, or any later version.
  Privacy Friendly Torchlight is distributed in the hope
  that it will be useful, but WITHOUT ANY WARRANTY; without even
  the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
  See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License
  along with Privacy Friendly Torchlight. If not, see <http://www.gnu.org/licenses/>.
 */

package com.newstoday.nepalnews.flashlight;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.newstoday.nepalnews.R;

import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity {

    private boolean flashState = false;
    private ImageView btnSwitch;
    private boolean endWhenPaused;
    private ICamera mCamera;
    private boolean isConnected;
    private Activity thisActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.flashlight_activity);

        btnSwitch = findViewById(R.id.btnSwitch);
        SharedPreferences preferences = this.getPreferences(MODE_PRIVATE);
        flashState = false;
        endWhenPaused = preferences.getBoolean("closeOnPause", false);
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void init() {
        PackageManager pm = getPackageManager();

        // if device support camera?
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) | !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Log.e("err", "Device has no camera!");
            Toast.makeText(this, R.string.no_flash, Toast.LENGTH_LONG).show();
            btnSwitch.setEnabled(false);
            return;
        }

        // Check Android Version
        if (SDK_INT >= Build.VERSION_CODES.M) {
            mCamera = new CameraMarshmallow();
        } else {
            mCamera = new CameraNormal();
        }

        // set up camera
        setUpCamera();

        btnSwitch.setOnClickListener(arg0 -> {

            // can we have permissions that are revoked?
            if (SDK_INT >= Build.VERSION_CODES.M) {
                // check if we have the permission we need -> if not request it and turn on the light afterwards
                if (ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.CAMERA}, 0);
                    return;
                }
            }

            toggleCamera(!flashState);
        });
    }

    private void setUpCamera() {
        mCamera.init(this);
        isConnected = true;
    }

    private void toggleCamera(boolean enable) {
        if (mCamera.toggle(enable)) {
            flashState = enable;
            btnSwitch.setImageResource(enable ? R.drawable.on : R.drawable.off);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // yay, we got the permission -> turn on the light!
                toggleCamera(!flashState);
            } else {
                Toast.makeText(this, "Can not use flashlight without access to the camera.", Toast.LENGTH_SHORT).show();
                // permission denied, boo!
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!flashState)
            close();
        else if (endWhenPaused) {
            stop();
        }
    }

    private void stop() {
        flashState = false;
        isConnected = false;
        mCamera.toggle(false);
        mCamera.release();
    }

    private void close() {
        flashState = false;
        isConnected = false;
        mCamera.release();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isConnected) {
            init();
        }

        if (flashState) {
            btnSwitch.setImageResource(R.drawable.on);
        } else {
            btnSwitch.setImageResource(R.drawable.off);
        }
    }
}
