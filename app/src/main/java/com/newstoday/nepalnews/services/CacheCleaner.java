package com.newstoday.nepalnews.services;

import java.io.File;
import java.util.Date;
import java.util.Objects;

public class CacheCleaner {
    public void clearCacheFolder(final File cacheDir, final int numDays) {
        if (cacheDir.isDirectory()) {
            File[] files = cacheDir.listFiles();

            for (File file : Objects.requireNonNull(files)) {
                if (null != file) {
                    long lastModified = file.lastModified();
                    if (0 < lastModified) {
                        Date lastMDate = new Date(lastModified);
                        Date today = new Date(System.currentTimeMillis());
                        long diff = today.getTime() - lastMDate.getTime();
                        long diffDays = diff / (24 * 60 * 60 * 1000);
                        if (numDays < diffDays) {
                            file.deleteOnExit();
                        }
                    }

                }
            }
        }
    }
}
