package org.wdd.app.android.interestcollection.utils;

import java.io.File;

/**
 * Created by richard on 1/20/17.
 */

public class FileUtils {

    public static long getFolderSize(File folder) {
        if (!folder.exists()) return 0;
        if (folder.isFile()) return folder.length();
        int size = 0;
        File[] childFiles = folder.listFiles();
        if (childFiles == null) return 0;
        for (File file : childFiles) {
            if (file.isFile()) {
                size += file.length();
            } else {
                size += getFolderSize(file);
            }
        }
        return size;
    }

    public static void deleteFolder(File folder) {
        if (!folder.exists()) return;
        File[] childFiles = folder.listFiles();
        if (childFiles == null) {
            folder.delete();
            return;
        }
        for (File childFile : childFiles) {
            if (childFile.isFile()) {
                childFile.delete();
            } else {
                deleteFolder(childFile);
            }
        }
        folder.delete();
    }
}
