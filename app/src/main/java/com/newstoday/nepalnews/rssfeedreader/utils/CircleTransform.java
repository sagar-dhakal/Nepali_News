/*
 * Copyright 2014 Julian Shen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.newstoday.nepalnews.rssfeedreader.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

public class CircleTransform implements Transformation {
    private static final String TAG = "CircleTransform";

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        Bitmap circleBitmap;
        Canvas canvas;
        try {
            circleBitmap = Bitmap.createBitmap(size, size, source.getConfig());
            canvas = new Canvas(circleBitmap);
        } catch (Exception e) {
            e.printStackTrace();
            return source;
        }

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap squaredBitmap;
        try {
            squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        } catch (Exception e) {
            e.printStackTrace();
            circleBitmap.recycle();
            return source;
        }

        if (squaredBitmap != source) {
            source.recycle();
        }
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return circleBitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}